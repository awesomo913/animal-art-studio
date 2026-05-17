package com.animalartstudio.kids.local

import com.animalartstudio.kids.data.HelpArticleDto
import com.animalartstudio.kids.data.LessonDetailDto
import com.animalartstudio.kids.data.LessonStepDto
import com.animalartstudio.kids.data.LessonSummaryDto

/**
 * Mirrors `backend/.../db/PenguinContent.kt` + `service/HelpCatalog.kt` so the
 * self-contained APK doesn't need to hit the API for lesson definitions.
 *
 * When new content lands server-side, port it here too — or wire a small
 * remote-content fallback later.
 */
internal object LocalContent {

  const val PENGUIN_LESSON_ID = "penguin-happy"
  const val PENGUIN_VERSION = 2
  const val PENGUIN_EST_MINUTES = 10

  val summaries: List<LessonSummaryDto> = listOf(
      LessonSummaryDto(
          id = PENGUIN_LESSON_ID,
          title = "Waddles' Splashy Show",
          subtitle = "You're the co-star — Waddles the penguin is tonight's star!",
          animalKey = "penguin",
          orderIndex = 0,
          estMinutes = PENGUIN_EST_MINUTES,
          version = PENGUIN_VERSION,
      ),
  )

  val penguinDetail: LessonDetailDto = LessonDetailDto(
      id = PENGUIN_LESSON_ID,
      title = "Waddles' Splashy Show",
      subtitle = "You're the co-star — Waddles the penguin is tonight's star!",
      description =
          "Tonight Waddles is putting on a tiny show — you help paint the snowy belly, the grin, " +
              "and the splashy flippers. No rush: art is play!",
      animalKey = "penguin",
      estMinutes = PENGUIN_EST_MINUTES,
      version = PENGUIN_VERSION,
      steps = listOf(
          LessonStepDto(
              index = 0,
              title = "Snowy tummy",
              instruction = "Waddles whispers: 'Pssst — paint me one big cuddly tummy in the middle, like a soft snow pillow!'",
              technique = "Wobbly oval first: light lines, then go a little bolder!",
              minCoverage = 0.05,
              maxCoverage = 0.80,
              colorHint = "inky blue or soft charcoal",
              minStrokes = 1,
          ),
          LessonStepDto(
              index = 1,
              title = "Grin & beak blink",
              instruction = "Waddles giggles: 'Two tiny bumps for sleepy eyes… then a wee orange wedge for my happy beak!'",
              technique = "Dot-dot-triangle: eyes spaced like two peas, tiny mountain for the beak.",
              minCoverage = 0.06,
              maxCoverage = 0.78,
              colorHint = "sunset orange",
              minStrokes = 3,
          ),
          LessonStepDto(
              index = 2,
              title = "Tiny flipper high-fives",
              instruction = "Waddles splashes: 'Slide two slidey fins on each side — we are going SPLASH-dancing!'",
              technique = "Curvy raindrops — thin ends, chunky middles!",
              minCoverage = 0.08,
              maxCoverage = 0.82,
              colorHint = "ocean ink",
              minStrokes = 2,
          ),
      ),
  )

  internal data class StepHints(
      val hintEmpty: String,
      val hintMore: String,
      val hintAlmost: String,
      val celebrate: String,
  )

  /** Per-step coach hints (not part of the public step DTO — local-only). */
  val penguinStepHints: List<StepHints> = listOf(
      StepHints(
          hintEmpty = "Hm, I'm still sleepy — scribble one big tummy egg right here for me?",
          hintMore = "A little more fluff on the tummy would make me sparkle!",
          hintAlmost = "Ooh, so cozy! Tiny wiggle if the oval feels squarish?",
          celebrate = "That tummy looks snack-ready — thank you!",
      ),
      StepHints(
          hintEmpty = "I need helpers to place my grin — two dots above, one tiny wedge under!",
          hintMore = "My face likes a few more giggly lines!",
          hintAlmost = "So close — smidge clearer on my beaky smile?",
          celebrate = "Peekaboo grin! I already love it!",
      ),
      StepHints(
          hintEmpty = "Paddle-paddle here — gentle curves hugging left and right of my belly!",
          hintMore = "A few bolder curves for swimming power?",
          hintAlmost = "Flipper-almost! Tiny bit more pizzazz on one side?",
          celebrate = "HIGH-FIN! I'm ready for the curtain call!",
      ),
  )

  val help: List<HelpArticleDto> = listOf(
      HelpArticleDto(
          id = "start-drawing",
          title = "How a lesson works",
          body =
              "Each step shows you what to add next. Your coach never grades you — it helps you " +
                  "play with lines until it looks right to you. When the coach says a step is complete, " +
                  "you move to the next part of the animal.",
      ),
      HelpArticleDto(
          id = "troubleshoot-blank",
          title = "My drawing is not showing up",
          body =
              "Make sure the canvas is in color (not the eraser) and that your screen brightness " +
                  "is comfortable. If nothing appears, try bigger, slower lines — the coach loves to " +
                  "see your marks!",
      ),
      HelpArticleDto(
          id = "troubleshoot-sound",
          title = "I cannot hear the animal friends",
          body =
              "Ask a grown-up to check the tablet volume, and the mute switch if your device has one. " +
                  "You can also enjoy the app quietly if sound needs to stay off.",
      ),
      HelpArticleDto(
          id = "magic-unlock",
          title = "What is the wiggly magic surprise?",
          body =
              "The longer you play with a step and use the coach's gentle tips, the more practice " +
                  "stars you save up. When you have enough practice and finish the last step, your " +
                  "animal can wiggle, hop, or splash in a little celebration.",
      ),
      HelpArticleDto(
          id = "parents-privacy",
          title = "Privacy & sharing",
          body =
              "This build is fully offline — no drawings or crash notes ever leave the phone. " +
                  "Ask a parent before sharing your drawing outside the family.",
      ),
  )
}
