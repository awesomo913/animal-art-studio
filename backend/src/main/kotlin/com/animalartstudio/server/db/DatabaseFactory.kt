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
          driverClassName = driverFor(url)
          jdbcUrl = url
          this.username = user
          this.password = password
          maximumPoolSize = 6
        }
    val ds = HikariDataSource(cfg)
    Database.connect(ds)
    transaction {
      // `createMissingTablesAndColumns` is idempotent and additive:
      //  - new tables get created
      //  - new columns (e.g. drawing_sessions.practice_attempts, lesson_steps.min_strokes)
      //    are ALTER-ADDED on existing installs
      //  - existing data is preserved
      // Works on both H2 + Postgres. See REVIEW_NOTES C-1 — graduate to Flyway once
      // we need type widening, FK changes, or data backfills.
      SchemaUtils.createMissingTablesAndColumns(
          Lessons,
          LessonSteps,
          DrawingSessions,
          ClientCrashReports,
      )
    }
  }

  /** Pick the driver class from the JDBC URL so the same factory serves H2 + Postgres. */
  private fun driverFor(url: String): String = when {
    url.startsWith("jdbc:postgresql:") -> "org.postgresql.Driver"
    url.startsWith("jdbc:h2:")         -> "org.h2.Driver"
    else -> error("Unsupported JDBC URL: $url")
  }
}
