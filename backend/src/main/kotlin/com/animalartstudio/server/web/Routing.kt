package com.animalartstudio.server.web

import com.animalartstudio.server.service.CoachingService
import io.ktor.server.application.Application
import com.animalartstudio.server.service.CrashIngestService
import com.animalartstudio.server.service.HelpCatalog
import com.animalartstudio.server.web.dto.ClientCrashIngest
import com.animalartstudio.server.web.dto.CreateSessionRequest
import com.animalartstudio.server.web.dto.ErrorBody
import com.animalartstudio.server.web.dto.SubmitStepRequest
import io.ktor.http.HttpStatusCode
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
) {
  routing {
    get("/healthz") { call.respond(mapOf("status" to "ok", "time" to System.currentTimeMillis())) }

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
        val sessionId = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorBody("bad_session_id"))
        val body = runCatching { call.receive<SubmitStepRequest>() }.getOrElse {
          return@post call.respond(HttpStatusCode.BadRequest, ErrorBody("invalid_json", it.message))
        }
        if (body.imageBase64.isBlank()) {
          return@post call.respond(HttpStatusCode.BadRequest, ErrorBody("missing_image"))
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
