package com.animalartstudio.kids.net

import com.animalartstudio.kids.data.ClientCrashAck
import com.animalartstudio.kids.data.ClientCrashIngest
import com.animalartstudio.kids.data.CreateSessionRequest
import com.animalartstudio.kids.data.ErrorBody
import com.animalartstudio.kids.data.FeedbackResponse
import com.animalartstudio.kids.data.HelpArticleDto
import com.animalartstudio.kids.data.LessonDetailDto
import com.animalartstudio.kids.data.LessonSummaryDto
import com.animalartstudio.kids.data.SessionResponse
import com.animalartstudio.kids.data.SubmitStepRequest
import com.animalartstudio.kids.util.RingLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/** OkHttp-backed [StudioApi]. Used by the LAN/PC-backed build flavour. */
class HttpStudioApi(
    private val base: String,
    private val log: RingLog,
) : StudioApi {
  private val client =
      OkHttpClient.Builder()
          .connectTimeout(12, java.util.concurrent.TimeUnit.SECONDS)
          .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
          .build()
  private val json =
      Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
      }
  private val appJson = "application/json; charset=utf-8".toMediaType()

  private fun url(path: String) =
      ("${base.trimEnd('/')}/$path").toHttpUrlOrNull()
          ?: error("Invalid API base URL: $base")

  override suspend fun listLessons(): List<LessonSummaryDto> = withContext(Dispatchers.IO) { get("v1/lessons") }
  override suspend fun getLesson(id: String): LessonDetailDto = withContext(Dispatchers.IO) { get("v1/lessons/$id") }
  override suspend fun createSession(body: CreateSessionRequest): SessionResponse = withContext(Dispatchers.IO) { post("v1/sessions", body) }
  override suspend fun submit(
      sessionId: String,
      body: SubmitStepRequest,
  ): FeedbackResponse = withContext(Dispatchers.IO) { post("v1/sessions/$sessionId/submit", body) }
  override suspend fun help(): List<HelpArticleDto> = withContext(Dispatchers.IO) { get("v1/help") }
  override suspend fun sendCrash(ing: ClientCrashIngest): ClientCrashAck = withContext(Dispatchers.IO) { post("v1/client-logs", ing) }

  private inline fun <reified T> get(path: String): T {
    val req = Request.Builder().url(url(path)).get().build()
    val res = client.newCall(req).execute()
    val text = res.body?.string().orEmpty()
    if (!res.isSuccessful) {
      err(res.code, text)
    }
    log.append("GET $path -> ${res.code}")
    return json.decodeFromString(text)
  }

  private inline fun <reified B : Any, reified T> post(path: String, body: B): T {
    val jsonBody = json.encodeToString(serializer<B>(), body)
    val req =
        Request.Builder()
            .url(url(path))
            .post(jsonBody.toRequestBody(appJson))
            .build()
    val res = client.newCall(req).execute()
    val text = res.body?.string().orEmpty()
    if (!res.isSuccessful) {
      err(res.code, text)
    }
    log.append("POST $path -> ${res.code}")
    return json.decodeFromString(text)
  }

  private fun err(code: Int, s: String): Nothing {
    val m = runCatching { json.decodeFromString<ErrorBody>(s) }.getOrNull()
    throw ApiException(code, m?.error ?: "http", m?.detail)
  }
}
