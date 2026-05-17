package com.animalartstudio.server

import com.animalartstudio.server.db.ContentMigrator
import com.animalartstudio.server.db.ContentSeed
import com.animalartstudio.server.db.DatabaseFactory
import com.animalartstudio.server.service.CoachingService
import com.animalartstudio.server.service.CrashIngestService
import com.animalartstudio.server.web.configureRouting
import com.animalartstudio.server.web.dto.ErrorBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import org.slf4j.Logger

fun main(args: Array<String>) {
  if (args.isNotEmpty() && (args[0] == "-h" || args[0] == "--help")) {
    println("Animal Art Studio API. Optional: pass port as first arg (default 8080).")
    return
  }
  val fromArg = args.firstOrNull()?.toIntOrNull()
  val port = fromArg
      ?: System.getProperty("PORT")?.toIntOrNull()
      ?: System.getenv("PORT")?.toIntOrNull()
      ?: 8080
  val env: ApplicationEngineEnvironment = applicationEngineEnvironment {
    log = LoggerFactory.getLogger("Ktor")
    module { module() }
    connector { host = "0.0.0.0"; this.port = port }
  }
  embeddedServer(Netty, env).start(wait = true)
}

fun Application.module() {
  val log: Logger = LoggerFactory.getLogger("AnimalArtStudio")
  val json =
      Json {
        prettyPrint = false
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
      }

  val dbUrl = System.getenv("DATABASE_URL")
      ?: System.getProperty("database.url")
      ?: environment.config.propertyOrNull("animalArtStudio.database.url")?.getString()
      ?: "jdbc:h2:file:./data/animal-art-studio;DB_CLOSE_DELAY=-1;AUTO_RECONNECT=TRUE"
  val dbUser = System.getenv("DATABASE_USER")
      ?: environment.config.propertyOrNull("animalArtStudio.database.user")?.getString() ?: "sa"
  val dbPassword = System.getenv("DATABASE_PASSWORD")
      ?: environment.config.propertyOrNull("animalArtStudio.database.password")?.getString() ?: ""
  val nudges = System.getenv("NUDGES_FOR_MAGIC")?.toIntOrNull()
      ?: environment.config.propertyOrNull("animalArtStudio.coaching.nudgesRequiredForMagic")?.getString()?.toIntOrNull()
      ?: 5

  // B-6: CORS allowlist comma-separated. Empty → permissive (dev). Set in prod.
  val allowedOrigins: List<String> = (System.getenv("CORS_ALLOWED_ORIGINS")
      ?: environment.config.propertyOrNull("animalArtStudio.cors.allowedOrigins")?.getString()
      ?: "")
      .split(",")
      .map { it.trim() }
      .filter { it.isNotEmpty() }

  DatabaseFactory.init(
      url = normalizeJdbc(dbUrl),
      user = dbUser,
      password = dbPassword,
  )
  ContentSeed.runIfEmpty()
  ContentMigrator.upgradePenguinLessonIfStale()

  val coaching = CoachingService(nudges)
  val crash = CrashIngestService()

  install(CORS) {
    if (allowedOrigins.isEmpty()) {
      log.warn("CORS: no CORS_ALLOWED_ORIGINS set — using anyHost(). Do NOT do this in production.")
      anyHost()
    } else {
      log.info("CORS: allowlist=$allowedOrigins")
      allowedOrigins.forEach { allowHost(it) }
    }
    allowMethod(HttpMethod.Post)
    allowMethod(HttpMethod.Get)
    allowMethod(HttpMethod.Options)
    allowHeader(HttpHeaders.ContentType)
    allowHeader(HttpHeaders.Accept)
  }
  install(ContentNegotiation) { json(json) }
  install(StatusPages) {
    status(HttpStatusCode.NotFound) { call, status ->
      log.info("404 ${call.request.httpMethod} ${call.request.uri}")
      call.respond(status, ErrorBody("not_found"))
    }
    exception<Throwable> { call, err ->
      log.error("Unhandled: ${err.message}", err)
      call.respond(
          HttpStatusCode.InternalServerError,
          ErrorBody("server_error", err.message),
      )
    }
  }
  configureRouting(coaching, crash) { dbProbe() }
}

/** C-2: `SELECT 1` to confirm the JDBC connection is alive. */
private fun dbProbe(): Boolean = runCatching {
  transaction {
    val stmt = connection.prepareStatement("SELECT 1", false)
    stmt.executeQuery().use { it.next() }
  }
  true
}.getOrDefault(false)

private fun normalizeJdbc(url: String): String = when {
  url.startsWith("jdbc:") -> url
  else -> "jdbc:h2:file:./data/animal-art-studio;DB_CLOSE_DELAY=-1;AUTO_RECONNECT=TRUE"
}
