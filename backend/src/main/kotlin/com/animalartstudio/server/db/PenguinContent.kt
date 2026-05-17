package com.animalartstudio.server.db

import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert

internal const val PENGUIN_LESSON_ID = "penguin-happy"
internal const val PENGUIN_CONTENT_VERSION = 2

/** Text and bounds for **[Waddles' Splashy Show]** — star-first wording for youngest kids. */
internal data class StepContent(
    val index: Int,
    val title: String,
    val instruction: String,
    val technique: String,
    val minCoverage: Double,
    val maxCoverage: Double,
    val colorHint: String,
    val hintEmpty: String,
    val hintMore: String,
    val hintAlmost: String,
    val celebrate: String,
    val minStrokes: Int = 1,
)

internal fun penguinV2LessonTitle() = "Waddles' Splashy Show"

internal fun penguinV2LessonSubtitle() = "You're the co‑star — Waddles the penguin is tonight's star!"

internal fun penguinV2LessonDescription() =
    "Tonight Waddles is putting on a tiny show — you help paint the snowy belly, the grin, and the splashy flippers. No rush: art is play!"

internal fun penguinV2Steps(): List<StepContent> =
    listOf(
        StepContent(
            index = 0,
            title = "Snowy tummy",
            instruction =
                "Waddles whispers: 'Pssst — paint me one big cuddly tummy in the middle, like a soft snow pillow!'",
            technique = "Wobbly oval first: light lines, then go a little bolder!",
            minCoverage = 0.05,
            maxCoverage = 0.80,
            colorHint = "inky blue or soft charcoal",
            hintEmpty = "Hm, I'm still sleepy — scribble one big tummy egg right here for me?",
            hintMore = "A little more fluff on the tummy would make me sparkle!",
            hintAlmost = "Ooh, so cozy! Tiny wiggle if the oval feels squarish?",
            celebrate = "That tummy looks snack‑ready — thank you!",
            minStrokes = 1,
        ),
        StepContent(
            index = 1,
            title = "Grin & beak blink",
            instruction =
                "Waddles giggles: 'Two tiny bumps for sleepy eyes… then a wee orange wedge for my happy beak!'",
            technique = "Dot‑dot‑triangle: eyes spaced like two peas, tiny mountain for the beak.",
            minCoverage = 0.06,
            maxCoverage = 0.78,
            colorHint = "sunset orange",
            hintEmpty = "I need helpers to place my grin — two dots above, one tiny wedge under!",
            hintMore = "My face likes a few more giggly lines!",
            hintAlmost = "So close — smidge clearer on my beaky smile?",
            celebrate = "Peekaboo grin! I already love it!",
            minStrokes = 3,
        ),
        StepContent(
            index = 2,
            title = "Tiny flipper high‑fives",
            instruction =
                "Waddles splashes: 'Slide two slidey fins on each side — we are going SPLASH‑dancing!'",
            technique = "Curvy raindrops — thin ends, chunky middles!",
            minCoverage = 0.08,
            maxCoverage = 0.82,
            colorHint = "ocean ink",
            hintEmpty = "Paddle‑paddle here — gentle curves hugging left and right of my belly!",
            hintMore = "A few bolder curves for swimming power?",
            hintAlmost = "Flipper‑almost! Tiny bit more pizzazz on one side?",
            celebrate = "HIGH‑FIN! I'm ready for the curtain call!",
            minStrokes = 2,
        ),
    )

internal fun Transaction.insertAllPenguinStepsFromV2Catalog() {
  for (s in penguinV2Steps()) {
    LessonSteps.insert {
      it[LessonSteps.lessonId] = PENGUIN_LESSON_ID
      it[LessonSteps.stepIndex] = s.index
      it[LessonSteps.title] = s.title
      it[LessonSteps.instruction] = s.instruction
      it[LessonSteps.technique] = s.technique
      it[LessonSteps.minCoverage] = s.minCoverage
      it[LessonSteps.maxCoverage] = s.maxCoverage
      it[LessonSteps.colorHint] = s.colorHint
      it[LessonSteps.hintEmpty] = s.hintEmpty
      it[LessonSteps.hintMore] = s.hintMore
      it[LessonSteps.hintAlmost] = s.hintAlmost
      it[LessonSteps.celebrate] = s.celebrate
      it[LessonSteps.minStrokes] = s.minStrokes
    }
  }
}
