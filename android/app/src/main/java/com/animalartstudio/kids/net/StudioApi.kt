package com.animalartstudio.kids.net

import com.animalartstudio.kids.data.ClientCrashAck
import com.animalartstudio.kids.data.ClientCrashIngest
import com.animalartstudio.kids.data.CreateSessionRequest
import com.animalartstudio.kids.data.FeedbackResponse
import com.animalartstudio.kids.data.HelpArticleDto
import com.animalartstudio.kids.data.LessonDetailDto
import com.animalartstudio.kids.data.LessonSummaryDto
import com.animalartstudio.kids.data.SessionResponse
import com.animalartstudio.kids.data.SubmitStepRequest
import java.io.IOException

/**
 * Coaching surface the UI talks to.
 *
 * Two implementations:
 *  - [HttpStudioApi] — OkHttp client against the Ktor backend (LAN / PC setup).
 *  - [LocalStudioApi] — fully in-process; ships in the self-contained APK.
 *
 * Keeps the existing call sites in [com.animalartstudio.kids.ui.LessonViewModel],
 * [com.animalartstudio.kids.ui.home.HomeRoute], etc. unchanged.
 */
interface StudioApi {
  suspend fun listLessons(): List<LessonSummaryDto>
  suspend fun getLesson(id: String): LessonDetailDto
  suspend fun createSession(body: CreateSessionRequest): SessionResponse
  suspend fun submit(sessionId: String, body: SubmitStepRequest): FeedbackResponse
  suspend fun help(): List<HelpArticleDto>
  suspend fun sendCrash(ing: ClientCrashIngest): ClientCrashAck
}

class ApiException(
    val code: Int,
    val error: String,
    val detail: String?,
) : IOException("$code $error ${detail.orEmpty()}")
