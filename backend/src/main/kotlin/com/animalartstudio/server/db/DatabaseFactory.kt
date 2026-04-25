package com.animalartstudio.server.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
  fun init(
      url: String,
      user: String,
      password: String,
  ) {
    val cfg =
        HikariConfig().apply {
          driverClassName = "org.h2.Driver"
          jdbcUrl = url
          this.username = user
          this.password = password
          maximumPoolSize = 6
        }
    val ds = HikariDataSource(cfg)
    Database.connect(ds)
    transaction {
      SchemaUtils.create(
          Lessons,
          LessonSteps,
          DrawingSessions,
          ClientCrashReports,
      )
    }
  }
}
