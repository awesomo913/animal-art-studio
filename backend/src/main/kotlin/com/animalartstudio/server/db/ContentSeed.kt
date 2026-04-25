package com.animalartstudio.server.db

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object ContentSeed {
  fun runIfEmpty() {
    transaction {
      if (Lessons.selectAll().any()) {
        return@transaction
      }
      Lessons.insert {
        it[id] = "penguin-happy"
        it[title] = "Happy Penguin"
        it[subtitle] = "A round buddy with wiggly flippers"
        it[description] =
            "We build the body first, add a smile, then the flippers. Take your time — art is play!"
        it[animalKey] = "penguin"
        it[orderIndex] = 0
        it[estMinutes] = 10
        it[version] = 1
      }
      val steps =
          listOf(
              StepSeed(
                  index = 0,
                  title = "Penguin belly",
                  instruction = "Draw a big wobbly oval for the penguin’s fluffy tummy.",
                  technique = "Gentle shapes: draw light, then go bolder",
                  min = 0.05,
                  max = 0.80,
                  colorHint = "navy and white",
                  empty = "Try a big puddle shape in the middle — the belly is a soft egg!",
                  more = "A little more ink will help the belly pop off the page.",
                  almost = "So close! Wiggle the line if it needs to be rounder.",
                  celebrate = "That belly is looking cozy!",
              ),
              StepSeed(
                  index = 1,
                  title = "Happy face",
                  instruction = "Add two dots for eyes and a small triangle beak.",
                  technique = "Spacing: two tiny hills for the eyes, one cute peak for the beak",
                  min = 0.06,
                  max = 0.78,
                  colorHint = "orange for beak",
                  empty = "The face loves the center! Two dots up top, beak in the middle.",
                  more = "A few more face lines will bring your penguin to life.",
                  almost = "Nice! Make the beak a tiny bit clearer if you like.",
                  celebrate = "What a sweet face — I can tell it’s happy!",
              ),
              StepSeed(
                  index = 2,
                  title = "Wiggly flippers",
                  instruction = "Add two curvy teardrop wings on the sides for swimming power.",
                  technique = "Curves: start thin, get wider, end pointy for splashy flippers",
                  min = 0.08,
                  max = 0.82,
                  colorHint = "black flippers",
                  empty = "Two slidey shapes on the left and right make perfect flippers.",
                  more = "Try again with smooth curves — wobbly is wonderful!",
                  almost = "Almost! Just a smidge more on one side and you are done.",
                  celebrate = "Flipper high-five — your penguin is ready for the waves!",
              ),
          )
      for (s in steps) {
        LessonSteps.insert {
          it[lessonId] = "penguin-happy"
          it[stepIndex] = s.index
          it[title] = s.title
          it[instruction] = s.instruction
          it[technique] = s.technique
          it[minCoverage] = s.min
          it[maxCoverage] = s.max
          it[colorHint] = s.colorHint
          it[hintEmpty] = s.empty
          it[hintMore] = s.more
          it[hintAlmost] = s.almost
          it[celebrate] = s.celebrate
        }
      }
    }
  }
}

private data class StepSeed(
    val index: Int,
    val title: String,
    val instruction: String,
    val technique: String,
    val min: Double,
    val max: Double,
    val colorHint: String,
    val empty: String,
    val more: String,
    val almost: String,
    val celebrate: String,
)
