package com.animalartstudio.kids.net

import com.animalartstudio.kids.data.ClientCrashAck
import com.animalartstudio.kids.data.ClientCrashIngest
import com.animalartstudio.kids.data.CreateSessionRequest
import com.animalartstudio.kids.data.FeedbackResponse
import com.animalartstudio.kids.data.HelpArticleDto
import com.animalartstudio.kids.data.LessonDetailDto
import com.animalartstudio.kids.data.LessonStepDto
import com.animalartstudio.kids.data.LessonSummaryDto
import com.animalartstudio.kids.data.SessionResponse
import com.animalartstudio.kids.data.SubmitStepRequest
import com.animalartstudio.kids.local.LocalContent
import com.animalartstudio.kids.local.OnDeviceImageAnalyzer
import com.animalartstudio.kids.util.RingLog
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Fully in-process implementation. The shipped self-contained APK uses this so
 * the phone needs no backend, no network, no PC.
 *
 * Port of `backend/.../service/CoachingService.kt`:
 *  - sessions live in a [ConcurrentHashMap] (memory only — restarts wipe progress)
 *  - bug B-1 fix is here too: `bringToLifeUnlocked` keys off `practiceAttempts`
 *  - bug B-4 fix: `stepComplete` means "session has reached that step"
 *  - C-10 anti-gaming: `minStrokes` enforced on the strokeCount the UI passes
 */
class LocalStudioApi(
    private val log: RingLog,
    private val nudgesForMagic: Int = DEFAULT_NUDGES_FOR_MAGIC,
) : StudioApi {

  private data class State(
      val lessonId: String,
      var nudgeCount: Int = 0,
      var practiceAttempts: Int = 0,
      var highestStepCompleted: Int = -1,
  )

  private val sessions = ConcurrentHashMap<String, State>()

  override suspend fun listLessons(): List<LessonSummaryDto> {
    log.append("local listLessons")
    return LocalContent.summaries
  }

  override suspend fun getLesson(id: String): LessonDetailDto {
    log.append("local getLesson $id")
    return when (id) {
      LocalContent.PENGUIN_LESSON_ID -> LocalContent.penguinDetail
      else -> throw ApiException(404, "lesson_not_found", "no such lesson: $id")
    }
  }

  override suspend fun createSession(body: CreateSessionRequest): SessionResponse {
    if (body.lessonId != LocalContent.PENGUIN_LESSON_ID) {
      throw ApiException(404, "lesson_not_found", "no such lesson: ${body.lessonId}")
    }
    val id = "sess_" + UUID.randomUUID().toString().replace("-", "")
    sessions[id] = State(body.lessonId)
    log.append("local createSession $id")
    return SessionResponse(
        sessionId = id,
        lessonId = body.lessonId,
        nudgeCount = 0,
        highestStepCompleted = -1,
        version = LocalContent.PENGUIN_VERSION,
    )
  }

  override suspend fun submit(
      sessionId: String,
      body: SubmitStepRequest,
  ): FeedbackResponse = withContext(Dispatchers.Default) {
    val state = sessions[sessionId]
        ?: throw ApiException(400, "invalid_session_or_image_or_step", "unknown session")
    val lesson = LocalContent.penguinDetail
    val maxIndex = lesson.steps.maxOf { it.index }
    if (body.stepIndex < 0 || body.stepIndex > maxIndex) {
      throw ApiException(400, "invalid_session_or_image_or_step", "bad stepIndex")
    }
    val expected = state.highestStepCompleted + 1
    if (body.stepIndex != expected) {
      throw ApiException(400, "invalid_session_or_image_or_step", "wrong step order")
    }
    val step = lesson.steps.first { it.index == body.stepIndex }
    val bmp = OnDeviceImageAnalyzer.decode(body.imageBase64)
        ?: throw ApiException(400, "invalid_session_or_image_or_step", "bad PNG")
    val stats = OnDeviceImageAnalyzer.stats(bmp)

    val strokesOk = step.minStrokes == 0 || body.strokeCount >= step.minStrokes
    val passed = stats.coverage in step.minCoverage..step.maxCoverage && strokesOk

    state.practiceAttempts += 1
    if (!passed) state.nudgeCount += 1
    if (passed) state.highestStepCompleted = maxOf(state.highestStepCompleted, body.stepIndex)

    val isLast = body.stepIndex == maxIndex
    val (message, tone) = feedbackCopy(step, stats.coverage, passed, isLast, strokesOk)
    val bringUnlocked = passed && isLast && state.practiceAttempts >= nudgesForMagic
    val lessonComplete = passed && isLast
    val next = if (passed && !isLast) body.stepIndex + 1 else body.stepIndex
    val animal = lesson.animalKey

    FeedbackResponse(
        message = message,
        tone = tone,
        coverage = (kotlin.math.floor(stats.coverage * 1000) / 1000.0),
        nudgeCount = state.nudgeCount,
        practiceAttempts = state.practiceAttempts,
        stepIndex = body.stepIndex,
        stepPassed = passed,
        stepComplete = state.highestStepCompleted >= body.stepIndex,
        lessonComplete = lessonComplete,
        nextStepIndex = next,
        bringToLifeUnlocked = bringUnlocked,
        animalKey = animal,
        animationPreset = animal,
        soundPackId = animal,
        magicRequiresMorePractice = lessonComplete && !bringUnlocked,
        nudgesRequiredForMagic = nudgesForMagic,
        technique = step.technique,
    )
  }

  override suspend fun help(): List<HelpArticleDto> = LocalContent.help

  /** No-op in the offline build. Crashes still land on disk via [com.animalartstudio.kids.crash.CrashStore]. */
  override suspend fun sendCrash(ing: ClientCrashIngest): ClientCrashAck =
      ClientCrashAck(id = 0L, stored = false)

  private fun feedbackCopy(
      step: LessonStepDto,
      coverage: Double,
      passed: Boolean,
      isLast: Boolean,
      strokesOk: Boolean,
  ): Pair<String, String> {
    val hints = LocalContent.penguinStepHints.getOrNull(step.index)
    if (passed) {
      val msg = if (isLast) "You did it! What a great drawing!" else (hints?.celebrate ?: "Nice!")
      return msg to "celebrate"
    }
    if (!strokesOk) {
      return "A few more separate strokes would help — try lifting your finger between marks!" to "coach"
    }
    if (coverage < 0.02) return (hints?.hintEmpty ?: "Let's add some lines!") to "coach"
    if (coverage < step.minCoverage) return (hints?.hintMore ?: "A little more, please!") to "coach"
    return (hints?.hintAlmost ?: "So close!") to "coach"
  }

  companion object {
    const val DEFAULT_NUDGES_FOR_MAGIC = 5
  }
}
