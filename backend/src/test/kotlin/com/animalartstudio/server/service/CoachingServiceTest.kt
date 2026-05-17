package com.animalartstudio.server.service

import com.animalartstudio.server.db.ClientCrashReports
import com.animalartstudio.server.db.DrawingSessions
import com.animalartstudio.server.db.LessonSteps
import com.animalartstudio.server.db.Lessons
import com.animalartstudio.server.web.dto.CreateSessionRequest
import com.animalartstudio.server.web.dto.SubmitStepRequest
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Service tests against an in-memory H2 database.
 *
 * Each test gets a fresh schema via [setUp] so ordering can't bleed state.
 */
class CoachingServiceTest {

  companion object {
    private val dbCounter = AtomicInteger()
  }

  private val service = CoachingService(nudgesForMagic = 2)

  @BeforeTest
  fun setUp() {
    // Unique URL per test → JVM-wide unique in-memory database, no cross-test bleed.
    val url = "jdbc:h2:mem:test_${dbCounter.incrementAndGet()};DB_CLOSE_DELAY=-1;MODE=MYSQL"
    Database.connect(url, driver = "org.h2.Driver", user = "sa", password = "")
    transaction {
      SchemaUtils.create(Lessons, LessonSteps, DrawingSessions, ClientCrashReports)
      seedTwoStepLesson()
    }
  }

  /**
   * Two-step lesson where any non-zero coverage passes step 0 and any coverage passes step 1.
   * Lets tests pick "obviously passing" vs "obviously failing" images deterministically.
   */
  private fun seedTwoStepLesson() {
    Lessons.deleteAll()
    LessonSteps.deleteAll()
    Lessons.insert {
      it[id] = "test-lesson"
      it[title] = "Test Lesson"
      it[subtitle] = "sub"
      it[description] = "desc"
      it[animalKey] = "penguin"
      it[orderIndex] = 0
      it[estMinutes] = 5
      it[version] = 1
    }
    for (i in 0..1) {
      LessonSteps.insert {
        it[lessonId] = "test-lesson"
        it[stepIndex] = i
        it[title] = "Step $i"
        it[instruction] = "draw"
        it[technique] = "tech"
        it[minCoverage] = 0.10
        it[maxCoverage] = 1.0
        it[colorHint] = "black"
        it[hintEmpty] = "empty"
        it[hintMore] = "more"
        it[hintAlmost] = "almost"
        it[celebrate] = "yay"
        it[minStrokes] = 0
      }
    }
  }

  // ---------- helpers ----------

  /** PNG base64 of a fully-black 64x64 square (coverage = 1.0, always passes). */
  private fun blackPng(): String = solidPng(Color.BLACK)

  /** PNG base64 of a fully-white 64x64 square (coverage = 0.0, always fails). */
  private fun whitePng(): String = solidPng(Color.WHITE)

  private fun solidPng(c: Color): String {
    val img = BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB)
    val g = img.createGraphics()
    try { g.color = c; g.fillRect(0, 0, 64, 64) } finally { g.dispose() }
    val baos = ByteArrayOutputStream()
    ImageIO.write(img, "PNG", baos)
    return Base64.getEncoder().encodeToString(baos.toByteArray())
  }

  private fun newSession(): String =
      service.createSession(CreateSessionRequest("test-lesson"))!!.sessionId

  // ---------- tests ----------

  @Test
  fun `creating a session for an unknown lesson returns null`() {
    val r = service.createSession(CreateSessionRequest("does-not-exist"))
    assertNull(r)
  }

  @Test
  fun `bug B-1 - magic unlocks once practiceAttempts reaches threshold even on first-try success`() {
    val sid = newSession()
    val r0 = service.submit(sid, SubmitStepRequest(0, blackPng()))!!
    assertTrue(r0.stepPassed)
    assertEquals(1, r0.practiceAttempts)
    assertEquals(0, r0.nudgeCount)
    // First step passed → not lessonComplete yet → no magic.
    assertFalse(r0.bringToLifeUnlocked)

    val r1 = service.submit(sid, SubmitStepRequest(1, blackPng()))!!
    assertTrue(r1.stepPassed)
    assertTrue(r1.lessonComplete)
    assertEquals(2, r1.practiceAttempts)
    assertEquals(0, r1.nudgeCount)
    // Pre-fix, this would be FALSE (nudges=0 < 2). Post-fix, practice=2 >= 2 ⇒ TRUE.
    assertTrue(r1.bringToLifeUnlocked, "Perfect first-try clears MUST unlock magic")
    assertFalse(r1.magicRequiresMorePractice)
  }

  @Test
  fun `nudgeCount increments only on failures`() {
    val sid = newSession()
    val miss = service.submit(sid, SubmitStepRequest(0, whitePng()))!!
    assertFalse(miss.stepPassed)
    assertEquals(1, miss.nudgeCount)
    assertEquals(1, miss.practiceAttempts)
    val hit = service.submit(sid, SubmitStepRequest(0, blackPng()))!!
    assertTrue(hit.stepPassed)
    assertEquals(1, hit.nudgeCount)
    assertEquals(2, hit.practiceAttempts)
  }

  @Test
  fun `cannot skip ahead beyond the next expected step`() {
    val sid = newSession()
    // highestStepCompleted starts at -1, so only stepIndex 0 is valid.
    val skip = service.submit(sid, SubmitStepRequest(1, blackPng()))
    assertNull(skip, "skipping should yield null (400 in the route)")
  }

  @Test
  fun `submitting unknown sessionId returns null`() {
    assertNull(service.submit("does-not-exist", SubmitStepRequest(0, blackPng())))
  }

  @Test
  fun `garbage base64 fails gracefully without crashing the session`() {
    val sid = newSession()
    val r = service.submit(sid, SubmitStepRequest(0, "not even base64"))
    // ImageAnalyzer.fromBase64Png returns null → submit returns null → route returns 400.
    assertNull(r)
  }

  @Test
  fun `anti-gaming - minStrokes gate counts low-stroke submission as a nudge`() {
    // Tighten step 0 to require 3 strokes.
    transaction {
      LessonSteps.deleteAll()
      LessonSteps.insert {
        it[lessonId] = "test-lesson"
        it[stepIndex] = 0
        it[title] = "Step 0"
        it[instruction] = "draw"
        it[technique] = "tech"
        it[minCoverage] = 0.10
        it[maxCoverage] = 1.0
        it[colorHint] = "black"
        it[hintEmpty] = "empty"
        it[hintMore] = "more"
        it[hintAlmost] = "almost"
        it[celebrate] = "yay"
        it[minStrokes] = 3
      }
    }
    val sid = newSession()
    val flooded = service.submit(sid, SubmitStepRequest(0, blackPng(), strokeCount = 1))!!
    assertFalse(flooded.stepPassed, "1-stroke flood-fill must not pass a 3-stroke step")
    assertEquals(1, flooded.nudgeCount)

    val honest = service.submit(sid, SubmitStepRequest(0, blackPng(), strokeCount = 3))!!
    assertTrue(honest.stepPassed)
  }

  @Test
  fun `stepComplete now means session has reached that step, not a duplicate of stepPassed`() {
    val sid = newSession()
    val pass0 = service.submit(sid, SubmitStepRequest(0, blackPng()))!!
    // After passing step 0, stepComplete[0] should be true going forward.
    assertTrue(pass0.stepComplete)
    // Now miss step 1 — stepPassed is false but stepComplete is also false (haven't reached it).
    val miss1 = service.submit(sid, SubmitStepRequest(1, whitePng()))!!
    assertFalse(miss1.stepPassed)
    assertFalse(miss1.stepComplete)
    // Then pass step 1.
    val pass1 = service.submit(sid, SubmitStepRequest(1, blackPng()))!!
    assertTrue(pass1.stepPassed)
    assertTrue(pass1.stepComplete)
  }
}
