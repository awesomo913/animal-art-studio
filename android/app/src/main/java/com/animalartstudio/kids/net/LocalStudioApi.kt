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
      // Running totals of the cumulative drawing at the last PASSED step. Each
      // step is judged on how much the child added ON TOP of these.
      var baselineCoverage: Double = 0.0,
      var baselineStrokeCount: Int = 0,
  )

  private val sessions = ConcurrentHashMap<String, State>()

  override suspend fun listLessons(): List<LessonSummaryDto> {
    log.append("local listLessons")
    return LocalContent.summaries
  }

  override suspend fun getLesson(id: String): LessonDetailDto {
    log.append("local getLesson $id")
    return LocalContent.detailFor(id)
        ?: throw ApiException(404, "lesson_not_found", "no such lesson: $id")
  }

  override suspend fun createSession(body: CreateSessionRequest): SessionResponse {
    val lesson = LocalContent.detailFor(body.lessonId)
        ?: throw ApiException(404, "lesson_not_found", "no such lesson: ${body.lessonId}")
    val id = "sess_" + UUID.randomUUID().toString().replace("-", "")
    sessions[id] = State(body.lessonId)
    log.append("local createSession $id")
    return SessionResponse(
        sessionId = id,
        lessonId = body.lessonId,
        nudgeCount = 0,
        highestStepCompleted = -1,
        version = lesson.version,
    )
  }

  override suspend fun submit(
      sessionId: String,
      body: SubmitStepRequest,
  ): FeedbackResponse = withContext(Dispatchers.Default) {
    val state = sessions[sessionId]
        ?: throw ApiException(400, "invalid_session_or_image_or_step", "unknown session")
    val lesson = LocalContent.detailFor(state.lessonId)
        ?: throw ApiException(400, "invalid_session_or_image_or_step", "unknown lesson")
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

    // "Start over" wipes the child's canvas, so strokeCount drops below the
    // running baseline. Detect that and rebaseline to zero — otherwise the delta
    // goes negative, clamps to 0, and the current step becomes impossible to pass
    // (a softlock). See REVIEW: silent-failure scan 2026-07-01.
    if (body.strokeCount < state.baselineStrokeCount) {
      log.append("local rebaseline: canvas cleared (strokes ${body.strokeCount} < ${state.baselineStrokeCount})")
      state.baselineStrokeCount = 0
      state.baselineCoverage = 0.0
    }

    // Cumulative canvas: judge what the child added THIS step (delta from the
    // running baseline), never absolute coverage — a growing drawing can't blow
    // past a ceiling and softlock. `minCoverage` = per-feature delta floor,
    // `minStrokes` = delta stroke count. There is deliberately no upper bound.
    val deltaCoverage = (stats.coverage - state.baselineCoverage).coerceAtLeast(0.0)
    val deltaStrokes = (body.strokeCount - state.baselineStrokeCount).coerceAtLeast(0)
    val strokesOk = step.minStrokes == 0 || deltaStrokes >= step.minStrokes
    val drewEnough = deltaCoverage >= step.minCoverage
    val passed = drewEnough && strokesOk
    log.append(
        "local submit step=${body.stepIndex} cov=${stats.coverage} base=${state.baselineCoverage} " +
            "dCov=$deltaCoverage dStk=$deltaStrokes need=${step.minCoverage}/${step.minStrokes} passed=$passed")

    state.practiceAttempts += 1
    if (!passed) state.nudgeCount += 1
    if (passed) {
      state.highestStepCompleted = maxOf(state.highestStepCompleted, body.stepIndex)
      state.baselineCoverage = stats.coverage
      state.baselineStrokeCount = body.strokeCount
    }

    val isLast = body.stepIndex == maxIndex
    val (message, tone) =
        feedbackCopy(step, deltaCoverage, passed, isLast, strokesOk, LocalContent.stepHintsFor(state.lessonId))
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
      deltaCoverage: Double,
      passed: Boolean,
      isLast: Boolean,
      strokesOk: Boolean,
      stepHints: List<LocalContent.StepHints>,
  ): Pair<String, String> {
    val hints = stepHints.getOrNull(step.index)
    if (passed) {
      val msg = if (isLast) "You did it! What a great drawing!" else (hints?.celebrate ?: "Nice!")
      return msg to "celebrate"
    }
    if (!strokesOk) {
      return "A few more separate strokes would help — try lifting your finger between marks!" to "coach"
    }
    if (deltaCoverage < 0.002) return (hints?.hintEmpty ?: "Let's add some lines!") to "coach"
    if (deltaCoverage < step.minCoverage) return (hints?.hintMore ?: "A little more, please!") to "coach"
    return (hints?.hintAlmost ?: "So close!") to "coach"
  }

  companion object {
    const val DEFAULT_NUDGES_FOR_MAGIC = 5
  }
}
