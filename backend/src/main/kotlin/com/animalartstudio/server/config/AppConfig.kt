package com.animalartstudio.server.config

import io.ktor.server.config.ApplicationConfig

data class AppConfig(
    val databaseUrl: String,
    val databaseUser: String,
    val databasePassword: String,
    val nudgesRequiredForMagic: Int,
    /** Hosts that get explicit CORS allow. Empty → fallback `anyHost()` for dev. */
    val allowedOrigins: List<String>,
) {
  companion object {
    fun from(config: ApplicationConfig): AppConfig {
      val a = config.config("animalArtStudio")
      val db = a.config("database")
      val c = a.config("coaching")
      val origins = a.propertyOrNull("cors.allowedOrigins")
          ?.getString()
          ?.split(",")
          ?.map { it.trim() }
          ?.filter { it.isNotEmpty() }
          ?: emptyList()
      return AppConfig(
          databaseUrl = db.property("url").getString(),
          databaseUser = db.property("user").getString(),
          databasePassword = db.property("password").getString(),
          nudgesRequiredForMagic = c.property("nudgesRequiredForMagic").getString().toInt(),
          allowedOrigins = origins,
      )
    }
  }
}
