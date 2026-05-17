package com.animalartstudio.server.web

import com.animalartstudio.server.Constants.MAX_IMAGE_BASE64_LENGTH
import com.animalartstudio.server.Constants.MAX_SUBMIT_BYTES
import com.animalartstudio.server.service.CoachingService
import com.animalartstudio.server.service.CrashIngestService
import com.animalartstudio.server.service.HelpCatalog
import com.animalartstudio.server.web.dto.ClientCrashIngest
import com.animalartstudio.server.web.dto.CreateSessionRequest
import com.animalartstudio.server.web.dto.ErrorBody
import com.animalartstudio.server.web.dto.SubmitStepRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting(
    coaching: CoachingService,
    crash: CrashIngestService,
    dbProbe: () -> Boolean,
) {
  routing {
    get("/healthz") {
      // C-2: actually check DB connectivity, not just process liveness.
      val dbOk = runCatching { dbProbe() }.getOrDefault(false)
      val payload = mapOf(
          "status" to if (dbOk) "ok" else "degraded",
          "db" to if (dbOk) "ok" else "down",
          "time" to System.currentTimeMillis(),
      )
      if (dbOk) call.respond(payload)
      else call.respond(HttpStatusCode.ServiceUnavailable, payload)
    }

    route("/v1") {
      get("/lessons") { call.respond(coaching.listLessons()) }
      get("/lessons/{id}") {
        val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorBody("bad_id"))
        val lesson = coaching.getLesson(id) ?: return@get call.respond(HttpStatusCode.NotFound, ErrorBody("lesson_not_found"))
        call.respond(lesson)
      }
      post("/sessions") {
        val body = runCatching { call.receive<CreateSessionRequest>() }.getOrElse {
          return@post call.respond(HttpStatusCode.BadRequest, ErrorBody("invalid_json", it.message))
        }
        if (body.lessonId.isBlank()) {
          return@post call.respond(HttpStatusCode.BadRequest, ErrorBody("missing_lesson_id"))
        }
        val s = coaching.createSession(body) ?: return@post call.respond(HttpStatusCode.NotFound, ErrorBody("lesson_not_found"))
        call.respond(s)
      }
      post("/sessions/{id}/submit") {
        // B-5: short-circuit on absurd Content-Length before we buffer the body.
        val contentLen = call.request.headers["Content-Length"]?.toLongOrNull() ?: 0L
        if (contentLen > MAX_SUBMIT_BYTES) {
          return@post call.respond(
              HttpStatusCode.PayloadTooLarge,
              ErrorBody("payload_too_large", "limit=${MAX_SUBMIT_BYTES} bytes"))
        }
        val sessionId = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorBody("bad_session_id"))
        val body = runCatching { call.receive<SubmitStepRequest>() }.getOrElse {
          return@post call.respond(HttpStatusCode.BadRequest, ErrorBody("invalid_json", it.message))
        }
        if (body.imageBase64.isBlank()) {
          return@post call.respond(HttpStatusCode.BadRequest, ErrorBody("missing_image"))
        }
        if (body.imageBase64.length > MAX_IMAGE_BASE64_LENGTH) {
          return@post call.respond(
              HttpStatusCode.PayloadTooLarge,
              ErrorBody("payload_too_large", "imageBase64 max=${MAX_IMAGE_BASE64_LENGTH} chars"))
        }
        val result =
            coaching.submit(sessionId, body)
                ?: return@post
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorBody("invalid_session_or_image_or_step", "Check the active step, PNG base64, and try again."))
        call.respond(result)
      }
      get("/help") { call.respond(HelpCatalog.articles) }
      post("/client-logs") {
        val body = runCatching { call.receive<ClientCrashIngest>() }.getOrElse {
          return@post call.respond(HttpStatusCode.BadRequest, ErrorBody("invalid_json", it.message))
        }
        if (body.deviceId.isBlank() || body.appVersion.isBlank()) {
          return@post call.respond(HttpStatusCode.BadRequest, ErrorBody("missing_device_or_version"))
        }
        val ack = crash.store(body)
        call.respond(ack)
      }
    }
  }
}
