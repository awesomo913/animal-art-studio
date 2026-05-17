package com.animalartstudio.server.db

import org.jetbrains.exposed.sql.Table

object Lessons : Table("lessons") {
  val id = varchar("id", 64)
  val title = varchar("title", 200)
  val subtitle = varchar("subtitle", 500).nullable()
  val description = text("description").nullable()
  val animalKey = varchar("animal_key", 64)
  val orderIndex = integer("order_index").default(0)
  val estMinutes = integer("est_minutes").default(8)
  val version = integer("version").default(1)
  override val primaryKey = PrimaryKey(id)
}

object LessonSteps : Table("lesson_steps") {
  val id = long("id").autoIncrement()
  val lessonId = varchar("lesson_id", 64).references(Lessons.id, onDelete = org.jetbrains.exposed.sql.ReferenceOption.CASCADE)
  val stepIndex = integer("step_index")
  val title = varchar("title", 200)
  val instruction = text("instruction")
  val technique = varchar("technique", 200)
  val minCoverage = double("min_coverage")
  val maxCoverage = double("max_coverage")
  val colorHint = varchar("color_hint", 64).nullable()
  val hintEmpty = text("hint_empty")
  val hintMore = text("hint_more")
  val hintAlmost = text("hint_almost")
  val celebrate = text("celebrate")

  /** Anti-gaming floor: how many discrete strokes the client must report. 0 disables. */
  val minStrokes = integer("min_strokes").default(0)

  override val primaryKey = PrimaryKey(id)
}

object DrawingSessions : Table("drawing_sessions") {
  val id = varchar("id", 64)
  val lessonId = varchar("lesson_id", 64).references(Lessons.id)
  val deviceId = varchar("device_id", 128).default("")

  /** Failures only. Drives the "coach gave a tip" framing in the UI. */
  val nudgeCount = integer("nudge_count").default(0)

  /**
   * Every submission counts as practice. Drives the "bring to life" magic-unlock
   * gate so that a child who clears every step on the first try **still** earns
   * the celebration after enough total tries. See bug B-1 in `docs/REVIEW_NOTES.md`.
   */
  val practiceAttempts = integer("practice_attempts").default(0)

  val highestStepCompleted = integer("highest_step_completed").default(-1)
  val createdAt = long("created_at")
  val updatedAt = long("updated_at")
  override val primaryKey = PrimaryKey(id)
}

object ClientCrashReports : Table("client_crash_reports") {
  val id = long("id").autoIncrement()
  val deviceId = varchar("device_id", 128)
  val appVersion = varchar("app_version", 32)
  val payloadJson = text("payload_json")
  val createdAt = long("created_at")
  override val primaryKey = PrimaryKey(id)
}
