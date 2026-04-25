package com.animalartstudio.server.config

import io.ktor.server.config.ApplicationConfig

data class AppConfig(
    val databaseUrl: String,
    val databaseUser: String,
    val databasePassword: String,
    val nudgesRequiredForMagic: Int,
) {
  companion object {
    fun from(config: ApplicationConfig): AppConfig {
      val a = config.config("animalArtStudio")
      val db = a.config("database")
      val c = a.config("coaching")
      return AppConfig(
          databaseUrl = db.property("url").getString(),
          databaseUser = db.property("user").getString(),
          databasePassword = db.property("password").getString(),
          nudgesRequiredForMagic = c.property("nudgesRequiredForMagic").getString().toInt(),
      )
    }
  }
}
