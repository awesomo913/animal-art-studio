package com.animalartstudio.server.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object ContentSeed {
  fun runIfEmpty() {
    transaction {
      if (Lessons.selectAll().any()) {
        return@transaction
      }
      Lessons.insert {
        it[Lessons.id] = PENGUIN_LESSON_ID
        it[Lessons.title] = penguinV2LessonTitle()
        it[Lessons.subtitle] = penguinV2LessonSubtitle()
        it[Lessons.description] = penguinV2LessonDescription()
        it[Lessons.animalKey] = "penguin"
        it[Lessons.orderIndex] = 0
        it[Lessons.estMinutes] = 10
        it[Lessons.version] = PENGUIN_CONTENT_VERSION
      }
      insertAllPenguinStepsFromV2Catalog()
    }
  }
}

/** Rewrites stale Waddles copy for installs still on **[content]** version 1. */
object ContentMigrator {
  fun upgradePenguinLessonIfStale() {
    transaction {
      val lesson = Lessons.selectAll().where { Lessons.id eq PENGUIN_LESSON_ID }.singleOrNull()
          ?: return@transaction
      if (lesson[Lessons.version] >= PENGUIN_CONTENT_VERSION) {
        return@transaction
      }
      Lessons.update({ Lessons.id eq PENGUIN_LESSON_ID }) {
        it[Lessons.title] = penguinV2LessonTitle()
        it[Lessons.subtitle] = penguinV2LessonSubtitle()
        it[Lessons.description] = penguinV2LessonDescription()
        it[Lessons.version] = PENGUIN_CONTENT_VERSION
      }
      // Exposed 0.55: deleteWhere lambda is `T.(ISqlExpressionBuilder) -> Op<Boolean>` (ISB is a
      // parameter, not the receiver) so `column eq value` needs the ISB receiver scope re-opened.
      LessonSteps.deleteWhere { isb -> isb.run { LessonSteps.lessonId eq PENGUIN_LESSON_ID } }
      insertAllPenguinStepsFromV2Catalog()
    }
  }
}
