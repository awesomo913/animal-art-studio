package com.animalartstudio.server.service

import com.animalartstudio.server.db.DrawingSessions
import com.animalartstudio.server.db.LessonSteps
import com.animalartstudio.server.db.Lessons
import com.animalartstudio.server.web.dto.CreateSessionRequest
import com.animalartstudio.server.web.dto.FeedbackResponse
import com.animalartstudio.server.web.dto.LessonDetailDto
import com.animalartstudio.server.web.dto.LessonStepDto
import com.animalartstudio.server.web.dto.LessonSummaryDto
import com.animalartstudio.server.web.dto.SessionResponse
import com.animalartstudio.server.web.dto.SubmitStepRequest
import java.util.UUID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class CoachingService(private val nudgesForMagic: Int) {

  fun listLessons(): List<LessonSummaryDto> = transaction {
    Lessons.selectAll().orderBy(Lessons.orderIndex, SortOrder.ASC).map { it.toSummary() }
  }

  fun getLesson(lessonId: String): LessonDetailDto? = transaction {
    val lesson = Lessons.selectAll().where { Lessons.id eq lessonId }.singleOrNull() ?: return@transaction null
    val steps =
        LessonSteps.selectAll().where { LessonSteps.lessonId eq lessonId }
            .orderBy(LessonSteps.stepIndex, SortOrder.ASC)
            .map { it.toStepDto() }
    lesson.toDetail(steps)
  }

  fun createSession(req: CreateSessionRequest): SessionResponse? = transaction {
    val exists = Lessons.selectAll().where { Lessons.id eq req.lessonId }.singleOrNull() ?: return@transaction null
    val id = "sess_" + UUID.randomUUID().toString().replace("-", "")
    val now = System.currentTimeMillis()
    DrawingSessions.insert {
      it[DrawingSessions.id] = id
      it[DrawingSessions.lessonId] = req.lessonId
      it[DrawingSessions.deviceId] = req.deviceId.orEmpty()
      it[DrawingSessions.nudgeCount] = 0
      it[DrawingSessions.practiceAttempts] = 0
      it[DrawingSessions.highestStepCompleted] = -1
      it[DrawingSessions.createdAt] = now
      it[DrawingSessions.updatedAt] = now
    }
    SessionResponse(
        sessionId = id,
        lessonId = req.lessonId,
        nudgeCount = 0,
        highestStepCompleted = -1,
        version = exists[Lessons.version],
    )
  }

  fun submit(
      sessionId: String,
      body: SubmitStepRequest,
  ): FeedbackResponse? = transaction {
    val row = DrawingSessions.selectAll().where { DrawingSessions.id eq sessionId }.singleOrNull() ?: return@transaction null
    val lessonId = row[DrawingSessions.lessonId]
    val stepRow =
        LessonSteps.selectAll().where { (LessonSteps.lessonId eq lessonId) and (LessonSteps.stepIndex eq body.stepIndex) }
            .singleOrNull()
            ?: return@transaction null
    val maxIndex =
        LessonSteps.selectAll().where { LessonSteps.lessonId eq lessonId }
            .map { it[LessonSteps.stepIndex] }
            .maxOrNull() ?: 0
    if (body.stepIndex < 0 || body.stepIndex > maxIndex) {
      return@transaction null
    }
    val expected = row[DrawingSessions.highestStepCompleted] + 1
    if (body.stepIndex != expected) {
      return@transaction null
    }

    val step = stepRow.toInfo()
    val image = ImageAnalyzer.fromBase64Png(body.imageBase64)
    if (image == null) {
      return@transaction null
    }
    val stats = ImageAnalyzer.stats(image)

    // Anti-gaming: if the step demands a minimum stroke count, count low-stroke
    // submissions as failures (don't crash the session). The frontend may not
    // send strokeCount yet (defaults to 0) so this only fires when both sides
    // opt in.
    val strokesOk = step.minStrokes == 0 || body.strokeCount >= step.minStrokes
    val passed = isPassing(stats.coverage, step) && strokesOk

    var nudges = row[DrawingSessions.nudgeCount]
    var practice = row[DrawingSessions.practiceAttempts] + 1 // every submit is practice
    if (!passed) nudges += 1
    var highest = row[DrawingSessions.highestStepCompleted]
    if (passed) {
      highest = maxOf(highest, body.stepIndex)
    }
    val now = System.currentTimeMillis()
    DrawingSessions.update({ DrawingSessions.id eq sessionId }) {
      it[DrawingSessions.nudgeCount] = nudges
      it[DrawingSessions.practiceAttempts] = practice
      it[DrawingSessions.highestStepCompleted] = highest
      it[DrawingSessions.updatedAt] = now
    }

    val isLast = body.stepIndex == maxIndex
    val lesson = Lessons.selectAll().where { Lessons.id eq lessonId }.single()
    val animalKey = lesson[Lessons.animalKey]
    val (message, tone) = feedbackCopy(step, stats, passed, isLast, strokesOk, body.strokeCount)
    val bringUnlocked = passed && isLast && practice >= nudgesForMagic
    val lessonComplete = passed && isLast
    val next = if (passed && !isLast) body.stepIndex + 1 else body.stepIndex

    FeedbackResponse(
        message = message,
        tone = tone,
        coverage = (kotlin.math.floor(stats.coverage * 1000) / 1000.0),
        nudgeCount = nudges,
        practiceAttempts = practice,
        stepIndex = body.stepIndex,
        stepPassed = passed,
        stepComplete = highest >= body.stepIndex,
        lessonComplete = lessonComplete,
        nextStepIndex = next,
        bringToLifeUnlocked = bringUnlocked,
        animalKey = animalKey,
        animationPreset = animalKey,
        soundPackId = animalKey,
        magicRequiresMorePractice = lessonComplete && !bringUnlocked,
        nudgesRequiredForMagic = nudgesForMagic,
        technique = step.technique,
    )
  }

  private fun isPassing(
      coverage: Double,
      step: LessonStepInfo,
  ): Boolean = coverage in step.minCoverage..step.maxCoverage

  private fun feedbackCopy(
      step: LessonStepInfo,
      stats: ImageStats,
      passed: Boolean,
      isLast: Boolean,
      strokesOk: Boolean,
      strokeCount: Int,
  ): Pair<String, String> {
    if (passed) {
      val msg = if (isLast) "You did it! What a great drawing!" else step.celebrate
      return msg to "celebrate"
    }
    if (!strokesOk) {
      return "A few more separate strokes would help — try lifting your finger between marks!" to "coach"
    }
    if (stats.coverage < 0.02) {
      return step.hintEmpty to "coach"
    }
    if (stats.coverage < step.minCoverage) {
      return step.hintMore to "coach"
    }
    return step.hintAlmost to "coach"
  }
}

private fun org.jetbrains.exposed.sql.ResultRow.toSummary() =
    LessonSummaryDto(
        id = this[Lessons.id],
        title = this[Lessons.title],
        subtitle = this[Lessons.subtitle],
        animalKey = this[Lessons.animalKey],
        orderIndex = this[Lessons.orderIndex],
        estMinutes = this[Lessons.estMinutes],
        version = this[Lessons.version],
    )

private fun org.jetbrains.exposed.sql.ResultRow.toDetail(steps: List<LessonStepDto>) =
    LessonDetailDto(
        id = this[Lessons.id],
        title = this[Lessons.title],
        subtitle = this[Lessons.subtitle],
        description = this[Lessons.description],
        animalKey = this[Lessons.animalKey],
        estMinutes = this[Lessons.estMinutes],
        version = this[Lessons.version],
        steps = steps,
    )

private fun org.jetbrains.exposed.sql.ResultRow.toStepDto() =
    LessonStepDto(
        index = this[LessonSteps.stepIndex],
        title = this[LessonSteps.title],
        instruction = this[LessonSteps.instruction],
        technique = this[LessonSteps.technique],
        minCoverage = this[LessonSteps.minCoverage],
        maxCoverage = this[LessonSteps.maxCoverage],
        colorHint = this[LessonSteps.colorHint],
        minStrokes = this[LessonSteps.minStrokes],
    )

private fun org.jetbrains.exposed.sql.ResultRow.toInfo() =
    LessonStepInfo(
        lessonId = this[LessonSteps.lessonId],
        stepIndex = this[LessonSteps.stepIndex],
        title = this[LessonSteps.title],
        instruction = this[LessonSteps.instruction],
        technique = this[LessonSteps.technique],
        minCoverage = this[LessonSteps.minCoverage],
        maxCoverage = this[LessonSteps.maxCoverage],
        minStrokes = this[LessonSteps.minStrokes],
        hintEmpty = this[LessonSteps.hintEmpty],
        hintMore = this[LessonSteps.hintMore],
        hintAlmost = this[LessonSteps.hintAlmost],
        celebrate = this[LessonSteps.celebrate],
    )
