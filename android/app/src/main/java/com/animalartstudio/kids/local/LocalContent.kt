package com.animalartstudio.kids.local

import com.animalartstudio.kids.data.HelpArticleDto
import com.animalartstudio.kids.data.LessonDetailDto
import com.animalartstudio.kids.data.LessonStepDto
import com.animalartstudio.kids.data.LessonSummaryDto

/**
 * Mirrors `backend/.../db/PenguinContent.kt` + `service/HelpCatalog.kt` so the
 * self-contained APK doesn't need to hit the API for lesson definitions.
 *
 * Each lesson now teaches the animal **feature by feature, from scratch** — one
 * step per traceable part. The step list here MUST line up 1:1 (same order and
 * count) with the feature list in `ui/draw/Blueprints.kt`: step N teaches
 * feature N, and the dotted guide the kid traces IS that feature.
 *
 * Gating note: with the cumulative canvas, `minCoverage` is read by
 * [com.animalartstudio.kids.net.LocalStudioApi] as a *per-feature delta floor*
 * (how much NEW ink this step needs), and `minStrokes` as a *delta* stroke
 * count. There is no upper coverage ceiling, so the growing drawing never
 * softlocks. `maxCoverage` is retained for the DTO but unused by the gate.
 */
internal object LocalContent {

  const val PENGUIN_LESSON_ID = "penguin-happy"
  const val PENGUIN_VERSION = 3
  const val PENGUIN_EST_MINUTES = 10

  const val CAT_LESSON_ID = "cat-happy"
  const val DOG_LESSON_ID = "dog-happy"
  const val BUNNY_LESSON_ID = "bunny-happy"
  const val FISH_LESSON_ID = "fish-happy"
  const val DINO_LESSON_ID = "dino-happy"
  const val UNICORN_LESSON_ID = "unicorn-happy"
  const val CONTENT_VERSION = 3
  const val EST_MINUTES = 10

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
      LessonSummaryDto(
          id = CAT_LESSON_ID,
          title = "Mimi's Purr Parade",
          subtitle = "You're the co-star — Mimi the kitten is tonight's star!",
          animalKey = "cat",
          orderIndex = 1,
          estMinutes = EST_MINUTES,
          version = CONTENT_VERSION,
      ),
      LessonSummaryDto(
          id = DOG_LESSON_ID,
          title = "Biscuit's Waggy Show",
          subtitle = "You're the co-star — Biscuit the puppy is tonight's star!",
          animalKey = "dog",
          orderIndex = 2,
          estMinutes = EST_MINUTES,
          version = CONTENT_VERSION,
      ),
      LessonSummaryDto(
          id = BUNNY_LESSON_ID,
          title = "Cotton's Hop-Hop Show",
          subtitle = "You're the co-star — Cotton the bunny is tonight's star!",
          animalKey = "bunny",
          orderIndex = 3,
          estMinutes = EST_MINUTES,
          version = CONTENT_VERSION,
      ),
      LessonSummaryDto(
          id = FISH_LESSON_ID,
          title = "Bubbles' Splish-Splash Show",
          subtitle = "You're the co-star — Bubbles the fish is tonight's star!",
          animalKey = "fish",
          orderIndex = 4,
          estMinutes = EST_MINUTES,
          version = CONTENT_VERSION,
      ),
      LessonSummaryDto(
          id = DINO_LESSON_ID,
          title = "Chomp's Stomp-Stomp Show",
          subtitle = "You're the co-star — Chomp the dinosaur is tonight's star!",
          animalKey = "dino",
          orderIndex = 5,
          estMinutes = EST_MINUTES,
          version = CONTENT_VERSION,
      ),
      LessonSummaryDto(
          id = UNICORN_LESSON_ID,
          title = "Sparkle's Rainbow Show",
          subtitle = "You're the co-star — Sparkle the unicorn is tonight's star!",
          animalKey = "unicorn",
          orderIndex = 6,
          estMinutes = EST_MINUTES,
          version = CONTENT_VERSION,
      ),
  )

  /** Lesson lookup for the generalized (multi-animal) local content. */
  fun detailFor(id: String): LessonDetailDto? =
      when (id) {
        PENGUIN_LESSON_ID -> penguinDetail
        CAT_LESSON_ID -> catDetail
        DOG_LESSON_ID -> dogDetail
        BUNNY_LESSON_ID -> bunnyDetail
        FISH_LESSON_ID -> fishDetail
        DINO_LESSON_ID -> dinoDetail
        UNICORN_LESSON_ID -> unicornDetail
        else -> null
      }

  fun stepHintsFor(id: String): List<StepHints> =
      when (id) {
        PENGUIN_LESSON_ID -> penguinStepHints
        CAT_LESSON_ID -> catStepHints
        DOG_LESSON_ID -> dogStepHints
        BUNNY_LESSON_ID -> bunnyStepHints
        FISH_LESSON_ID -> fishStepHints
        DINO_LESSON_ID -> dinoStepHints
        UNICORN_LESSON_ID -> unicornStepHints
        else -> emptyList()
      }

  private const val FULL = 1.0

  // ---- Penguin (feature order: body, belly, eyes, beak, feet, wings) --------
  val penguinDetail: LessonDetailDto = LessonDetailDto(
      id = PENGUIN_LESSON_ID,
      title = "Waddles' Splashy Show",
      subtitle = "You're the co-star — Waddles the penguin is tonight's star!",
      description =
          "Tonight you learn to draw Waddles from scratch — trace each dotted part and watch a " +
              "whole penguin appear. No rush: art is play!",
      animalKey = "penguin",
      estMinutes = PENGUIN_EST_MINUTES,
      version = PENGUIN_VERSION,
      steps = listOf(
          LessonStepDto(0, "Round tummy",
              "Waddles whispers: 'Start me with one big round body in the middle — like a soft snow egg!'",
              "Trace the dotted egg — go slow, right on the dots.",
              minCoverage = 0.03, maxCoverage = FULL, colorHint = "inky blue", minStrokes = 1),
          LessonStepDto(1, "Snowy belly",
              "Waddles giggles: 'Now a smaller tummy patch INSIDE — that's my fuzzy white belly!'",
              "Trace the little oval inside the body.",
              minCoverage = 0.018, maxCoverage = FULL, colorHint = "soft blue", minStrokes = 1),
          LessonStepDto(2, "Two happy eyes",
              "Waddles blinks: 'Two little dot-eyes up high so I can watch the show!'",
              "Trace both tiny circles up top.",
              minCoverage = 0.006, maxCoverage = FULL, colorHint = "inky blue", minStrokes = 1),
          LessonStepDto(3, "Sunny beak",
              "Waddles beams: 'A tiny orange triangle under my eyes — that's my happy beak!'",
              "Trace the little mountain shape.",
              minCoverage = 0.006, maxCoverage = FULL, colorHint = "sunset orange", minStrokes = 1),
          LessonStepDto(4, "Splashy feet",
              "Waddles wiggles: 'Two little flippy feet at the bottom — ready to SPLASH!'",
              "Trace both little ovals along the bottom.",
              minCoverage = 0.008, maxCoverage = FULL, colorHint = "sunset orange", minStrokes = 1),
          LessonStepDto(5, "Waving wings",
              "Waddles cheers: 'Two curvy wings on my sides for a big hello wave!'",
              "Trace the curve down each side.",
              minCoverage = 0.012, maxCoverage = FULL, colorHint = "splash blue", minStrokes = 1),
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
      StepHints("Trace the big dotted egg for my tummy — right on the dots!",
          "A little more along the tummy dots?",
          "So close — follow the last of the dots!",
          "That tummy looks snack-ready — thank you!"),
      StepHints("Trace the little oval inside for my fuzzy belly!",
          "A bit more of the belly patch, please!",
          "Almost — close up the belly oval!",
          "So fluffy! My belly is perfect!"),
      StepHints("Pop two little dots up high for my eyes!",
          "One more little eye for me?",
          "Nearly there — finish that eye!",
          "Peekaboo! I can see you now!"),
      StepHints("Trace the tiny triangle for my beak!",
          "A little more on the beak!",
          "So close — one more beak line!",
          "What a sunny beak — thank you!"),
      StepHints("Trace two little feet along the bottom!",
          "One more flippy foot?",
          "Almost — finish that foot!",
          "SPLASH-ready feet! Yay!"),
      StepHints("Trace a curvy wing down each side!",
          "A little more wing, please!",
          "So close — finish the wave!",
          "Big hello wave — I'm ready for the show!"),
  )

  // ---- Cat (body, ears, eyes, nose, smile, whiskers, tail) ------------------
  val catDetail: LessonDetailDto = LessonDetailDto(
      id = CAT_LESSON_ID,
      title = "Mimi's Purr Parade",
      subtitle = "You're the co-star — Mimi the kitten is tonight's star!",
      description =
          "Learn to draw Mimi from scratch — trace each dotted part and a whole smiley kitten " +
              "appears. Take your time!",
      animalKey = "cat",
      estMinutes = EST_MINUTES,
      version = CONTENT_VERSION,
      steps = listOf(
          LessonStepDto(0, "Round tummy",
              "Mimi purrs: 'Draw me a soft round body in the middle — like a warm little bun!'",
              "Trace the big dotted circle — nice and slow.",
              minCoverage = 0.03, maxCoverage = FULL, colorHint = "cozy grey", minStrokes = 1),
          LessonStepDto(1, "Pointy ears",
              "Mimi wiggles: 'Two pointy triangle ears up on top!'",
              "Trace each little triangle up top.",
              minCoverage = 0.014, maxCoverage = FULL, colorHint = "cozy grey", minStrokes = 1),
          LessonStepDto(2, "Sweet eyes",
              "Mimi blinks: 'Two round eyes so I can see my parade!'",
              "Trace both little circles.",
              minCoverage = 0.006, maxCoverage = FULL, colorHint = "soft charcoal", minStrokes = 1),
          LessonStepDto(3, "Tiny nose",
              "Mimi sniffs: 'A tiny triangle nose right in the middle!'",
              "Trace the little nose triangle.",
              minCoverage = 0.005, maxCoverage = FULL, colorHint = "candy pink", minStrokes = 1),
          LessonStepDto(4, "Happy smile",
              "Mimi grins: 'A little curvy smile under my nose!'",
              "Trace the two little smile curves.",
              minCoverage = 0.005, maxCoverage = FULL, colorHint = "soft charcoal", minStrokes = 1),
          LessonStepDto(5, "Whiskers",
              "Mimi twitches: 'Little whisker lines out each cheek — tickle tickle!'",
              "Trace the little lines on both sides.",
              minCoverage = 0.004, maxCoverage = FULL, colorHint = "cozy grey", minStrokes = 1),
          LessonStepDto(6, "Swishy tail",
              "Mimi swishes: 'One long curvy tail off to the side — make it dance!'",
              "Trace the long curl of the tail.",
              minCoverage = 0.012, maxCoverage = FULL, colorHint = "cozy grey", minStrokes = 1),
      ),
  )

  // ---- Dog (body, ears, eyes, nose, smile, tongue, tail) --------------------
  val dogDetail: LessonDetailDto = LessonDetailDto(
      id = DOG_LESSON_ID,
      title = "Biscuit's Waggy Show",
      subtitle = "You're the co-star — Biscuit the puppy is tonight's star!",
      description =
          "Learn to draw Biscuit from scratch — trace each dotted part and a whole waggy puppy " +
              "appears. No rush — just have fun!",
      animalKey = "dog",
      estMinutes = EST_MINUTES,
      version = CONTENT_VERSION,
      steps = listOf(
          LessonStepDto(0, "Cuddly body",
              "Biscuit woofs: 'Paint me a big cuddly body in the middle — nice and huggable!'",
              "Trace the big dotted oval — slow and steady.",
              minCoverage = 0.03, maxCoverage = FULL, colorHint = "warm brown", minStrokes = 1),
          LessonStepDto(1, "Floppy ears",
              "Biscuit flops: 'Two long floppy ears hanging down each side!'",
              "Trace each long droopy ear.",
              minCoverage = 0.014, maxCoverage = FULL, colorHint = "chocolate brown", minStrokes = 1),
          LessonStepDto(2, "Happy eyes",
              "Biscuit blinks: 'Two happy eyes so I can see you!'",
              "Trace both little circles.",
              minCoverage = 0.006, maxCoverage = FULL, colorHint = "dark brown", minStrokes = 1),
          LessonStepDto(3, "Boop nose",
              "Biscuit sniffs: 'A round boop-nose in the middle!'",
              "Trace the little nose oval.",
              minCoverage = 0.006, maxCoverage = FULL, colorHint = "dark brown", minStrokes = 1),
          LessonStepDto(4, "Big smile",
              "Biscuit pants: 'A big happy smile under my nose!'",
              "Trace the wide smile curve.",
              minCoverage = 0.005, maxCoverage = FULL, colorHint = "warm brown", minStrokes = 1),
          LessonStepDto(5, "Silly tongue",
              "Biscuit slurps: 'A little tongue peeking out — bleh!'",
              "Trace the little tongue shape.",
              minCoverage = 0.005, maxCoverage = FULL, colorHint = "puppy pink", minStrokes = 1),
          LessonStepDto(6, "Waggy tail",
              "Biscuit wiggles: 'A curvy tail out the back — wag-wag-WAG!'",
              "Trace the happy curl of the tail.",
              minCoverage = 0.012, maxCoverage = FULL, colorHint = "warm brown", minStrokes = 1),
      ),
  )

  val catStepHints: List<StepHints> = listOf(
      StepHints("Trace the big dotted circle for my tummy!",
          "A little more around the tummy?",
          "So close — close up the circle!",
          "That tummy is purr-fect for a nap!"),
      StepHints("Trace two pointy ears up on top!",
          "One more pointy ear for me?",
          "Almost — finish that ear point!",
          "Pointy ears — I hear everything now!"),
      StepHints("Pop two round eyes on my face!",
          "One more eye, please!",
          "Nearly there — finish that eye!",
          "Meow! I can see my parade!"),
      StepHints("Trace the tiny nose triangle!",
          "A bit more on the nose!",
          "So close — one more nose line!",
          "Boop! What a cute nose!"),
      StepHints("Trace a little smile under my nose!",
          "A bit more smile, please!",
          "Almost — finish the smile curve!",
          "Purr — I love my smile!"),
      StepHints("Trace little whisker lines out each cheek!",
          "A few more whisker tickles?",
          "So close — one more whisker!",
          "Tickle-tickle whiskers — yay!"),
      StepHints("Trace one long swishy tail off the side!",
          "Make the tail a little longer?",
          "Tail-almost — finish the swish!",
          "Swish-swish! Ready for the parade!"),
  )

  val dogStepHints: List<StepHints> = listOf(
      StepHints("Trace the big dotted oval for my body!",
          "A little more around the body?",
          "So close — close up the oval!",
          "That body is ready for belly rubs!"),
      StepHints("Trace two long floppy ears on the sides!",
          "One more floppy ear?",
          "Almost — finish that ear!",
          "Floppy ears — woof!"),
      StepHints("Pop two happy eyes on my face!",
          "One more eye, please!",
          "Nearly there — finish that eye!",
          "I can see you now — woof-woof!"),
      StepHints("Trace my little round boop-nose!",
          "A bit more on the nose!",
          "So close — finish the nose!",
          "Boop! Best nose ever!"),
      StepHints("Trace a big smile under my nose!",
          "A bit more smile, please!",
          "Almost — finish the smile!",
          "Happiest smile — thank you!"),
      StepHints("Trace a silly little tongue peeking out!",
          "A bit more tongue — bleh!",
          "So close — finish the tongue!",
          "Bleh! Silliest tongue!"),
      StepHints("Trace a waggy tail out the back!",
          "Make the tail a little bigger?",
          "Wag-almost — finish the curl!",
          "WAG-WAG! I'm ready for the show!"),
  )

  // ---- Bunny (body, ears, eyes, nose, tail) ---------------------------------
  val bunnyDetail: LessonDetailDto = LessonDetailDto(
      id = BUNNY_LESSON_ID,
      title = "Cotton's Hop-Hop Show",
      subtitle = "You're the co-star — Cotton the bunny is tonight's star!",
      description = "Learn to draw Cotton from scratch — trace each dotted part and a whole hoppy bunny appears!",
      animalKey = "bunny",
      estMinutes = EST_MINUTES,
      version = CONTENT_VERSION,
      steps = listOf(
          LessonStepDto(0, "Round tummy",
              "Cotton wiggles: 'Start me with one big round body — soft as a cloud!'",
              "Trace the big dotted circle — nice and slow.",
              minCoverage = 0.03, maxCoverage = FULL, colorHint = "soft grey", minStrokes = 1),
          LessonStepDto(1, "Tall ears",
              "Cotton perks up: 'Two long floppy ears way up on top!'",
              "Trace each tall dotted ear.",
              minCoverage = 0.014, maxCoverage = FULL, colorHint = "candy pink", minStrokes = 1),
          LessonStepDto(2, "Sweet eyes",
              "Cotton blinks: 'Two shiny eyes so I can spot the carrots!'",
              "Trace both little circles.",
              minCoverage = 0.006, maxCoverage = FULL, colorHint = "soft charcoal", minStrokes = 1),
          LessonStepDto(3, "Twitchy nose",
              "Cotton sniffs: 'A tiny triangle nose — twitch twitch!'",
              "Trace the little nose triangle.",
              minCoverage = 0.005, maxCoverage = FULL, colorHint = "candy pink", minStrokes = 1),
          LessonStepDto(4, "Puffy tail",
              "Cotton hops: 'A round puffy tail on my side — boing!'",
              "Trace the little dotted puff.",
              minCoverage = 0.008, maxCoverage = FULL, colorHint = "soft grey", minStrokes = 1),
      ),
  )

  // ---- Fish (body, tail, fin, eye, smile) -----------------------------------
  val fishDetail: LessonDetailDto = LessonDetailDto(
      id = FISH_LESSON_ID,
      title = "Bubbles' Splish-Splash Show",
      subtitle = "You're the co-star — Bubbles the fish is tonight's star!",
      description = "Learn to draw Bubbles from scratch — trace each dotted part and a whole splashy fish appears!",
      animalKey = "fish",
      estMinutes = EST_MINUTES,
      version = CONTENT_VERSION,
      steps = listOf(
          LessonStepDto(0, "Round body",
              "Bubbles blubs: 'Draw me a big round body — glub glub!'",
              "Trace the big dotted oval.",
              minCoverage = 0.03, maxCoverage = FULL, colorHint = "sunny orange", minStrokes = 1),
          LessonStepDto(1, "Swishy tail",
              "Bubbles wiggles: 'A big fan tail at the back to swish!'",
              "Trace the dotted triangle tail.",
              minCoverage = 0.012, maxCoverage = FULL, colorHint = "deep orange", minStrokes = 1),
          LessonStepDto(2, "Top fin",
              "Bubbles flips: 'A little fin right on top of me!'",
              "Trace the little dotted fin.",
              minCoverage = 0.008, maxCoverage = FULL, colorHint = "deep orange", minStrokes = 1),
          LessonStepDto(3, "Big eye",
              "Bubbles peeks: 'One big round eye at the front!'",
              "Trace the dotted circle.",
              minCoverage = 0.006, maxCoverage = FULL, colorHint = "soft charcoal", minStrokes = 1),
          LessonStepDto(4, "Happy smile",
              "Bubbles grins: 'A little smile so I look glubby-happy!'",
              "Trace the little smile curve.",
              minCoverage = 0.005, maxCoverage = FULL, colorHint = "deep orange", minStrokes = 1),
      ),
  )

  // ---- Dino (body, head, eye, spikes, tail, legs) ---------------------------
  val dinoDetail: LessonDetailDto = LessonDetailDto(
      id = DINO_LESSON_ID,
      title = "Chomp's Stomp-Stomp Show",
      subtitle = "You're the co-star — Chomp the dinosaur is tonight's star!",
      description = "Learn to draw Chomp from scratch — trace each dotted part and a whole stompy dino appears!",
      animalKey = "dino",
      estMinutes = EST_MINUTES,
      version = CONTENT_VERSION,
      steps = listOf(
          LessonStepDto(0, "Big body",
              "Chomp rumbles: 'Draw me a big round body — RAWR (a friendly one)!'",
              "Trace the big dotted oval.",
              minCoverage = 0.03, maxCoverage = FULL, colorHint = "leafy green", minStrokes = 1),
          LessonStepDto(1, "Round head",
              "Chomp grins: 'A round head up front so I can smile!'",
              "Trace the dotted circle up on the left.",
              minCoverage = 0.02, maxCoverage = FULL, colorHint = "leafy green", minStrokes = 1),
          LessonStepDto(2, "One eye",
              "Chomp winks: 'One happy eye on my head!'",
              "Trace the little dotted circle.",
              minCoverage = 0.005, maxCoverage = FULL, colorHint = "soft charcoal", minStrokes = 1),
          LessonStepDto(3, "Back spikes",
              "Chomp puffs up: 'Three pointy spikes down my back — zig-zag!'",
              "Trace each dotted triangle.",
              minCoverage = 0.012, maxCoverage = FULL, colorHint = "deep green", minStrokes = 1),
          LessonStepDto(4, "Long tail",
              "Chomp swishes: 'A long swishy tail out the back!'",
              "Trace the dotted tail curve.",
              minCoverage = 0.012, maxCoverage = FULL, colorHint = "deep green", minStrokes = 1),
          LessonStepDto(5, "Stompy legs",
              "Chomp stomps: 'Two little stompy legs to stand on!'",
              "Trace both little dotted legs.",
              minCoverage = 0.008, maxCoverage = FULL, colorHint = "deep green", minStrokes = 1),
      ),
  )

  // ---- Unicorn (body, horn, ears, eyes, mane, tail) -------------------------
  val unicornDetail: LessonDetailDto = LessonDetailDto(
      id = UNICORN_LESSON_ID,
      title = "Sparkle's Rainbow Show",
      subtitle = "You're the co-star — Sparkle the unicorn is tonight's star!",
      description = "Learn to draw Sparkle from scratch — trace each dotted part and a whole magic unicorn appears!",
      animalKey = "unicorn",
      estMinutes = EST_MINUTES,
      version = CONTENT_VERSION,
      steps = listOf(
          LessonStepDto(0, "Round body",
              "Sparkle shimmers: 'Draw me a soft round body — sparkle sparkle!'",
              "Trace the big dotted oval.",
              minCoverage = 0.03, maxCoverage = FULL, colorHint = "soft lavender", minStrokes = 1),
          LessonStepDto(1, "Magic horn",
              "Sparkle glows: 'A tall pointy horn on top — that's my magic!'",
              "Trace the dotted triangle up top.",
              minCoverage = 0.008, maxCoverage = FULL, colorHint = "golden", minStrokes = 1),
          LessonStepDto(2, "Little ears",
              "Sparkle wiggles: 'Two little ears beside my horn!'",
              "Trace each dotted ear.",
              minCoverage = 0.008, maxCoverage = FULL, colorHint = "soft lavender", minStrokes = 1),
          LessonStepDto(3, "Sparkly eyes",
              "Sparkle blinks: 'Two sparkly eyes to see the rainbow!'",
              "Trace both little circles.",
              minCoverage = 0.006, maxCoverage = FULL, colorHint = "soft charcoal", minStrokes = 1),
          LessonStepDto(4, "Swirly mane",
              "Sparkle tosses: 'A swirly mane down my side — swish!'",
              "Trace the dotted curls.",
              minCoverage = 0.012, maxCoverage = FULL, colorHint = "rainbow pink", minStrokes = 1),
          LessonStepDto(5, "Flowy tail",
              "Sparkle swishes: 'A long flowy tail at the back!'",
              "Trace the dotted tail curl.",
              minCoverage = 0.012, maxCoverage = FULL, colorHint = "rainbow pink", minStrokes = 1),
      ),
  )

  val bunnyStepHints: List<StepHints> = listOf(
      StepHints("Trace the big dotted circle for my tummy!", "A little more around the tummy?", "So close — close up the circle!", "That tummy is so soft — thank you!"),
      StepHints("Trace two tall ears up on top!", "One more tall ear for me?", "Almost — finish that ear!", "Tall ears — I can hear everything!"),
      StepHints("Pop two shiny eyes on my face!", "One more eye, please!", "Nearly there — finish that eye!", "I can see the carrots now!"),
      StepHints("Trace my tiny twitchy nose!", "A bit more on the nose!", "So close — one more nose line!", "Twitch twitch — cute nose!"),
      StepHints("Trace my round puffy tail on the side!", "A little more puff?", "Tail-almost — close it up!", "Boing! Puffy tail done!"),
  )

  val fishStepHints: List<StepHints> = listOf(
      StepHints("Trace the big dotted oval for my body!", "A little more around the body?", "So close — close up the oval!", "Glub glub — great body!"),
      StepHints("Trace my big fan tail at the back!", "A bit more tail, please!", "Almost — finish the tail!", "Swish! Ready to swim!"),
      StepHints("Trace the little fin on top!", "A bit more fin?", "So close — finish the fin!", "Fancy fin — yay!"),
      StepHints("Trace my one big round eye!", "A little more on the eye!", "Nearly — close up the eye!", "I can see you now!"),
      StepHints("Trace a little smile on my face!", "A bit more smile?", "Almost — finish the smile!", "Glubby-happy smile!"),
  )

  val dinoStepHints: List<StepHints> = listOf(
      StepHints("Trace the big dotted oval for my body!", "A little more around the body?", "So close — close up the oval!", "Big friendly body — RAWR!"),
      StepHints("Trace my round head up on the left!", "A bit more on the head?", "Almost — close up the head!", "What a happy head!"),
      StepHints("Pop one happy eye on my head!", "A little more on the eye!", "Nearly — finish the eye!", "I can see the jungle!"),
      StepHints("Trace three pointy spikes on my back!", "One more spike, please!", "So close — finish that spike!", "Spiky and cool!"),
      StepHints("Trace my long swishy tail!", "Make the tail a bit longer?", "Tail-almost — finish the curve!", "Swish — mighty tail!"),
      StepHints("Trace two little stompy legs!", "One more leg for me?", "Almost — finish that leg!", "STOMP STOMP — ready!"),
  )

  val unicornStepHints: List<StepHints> = listOf(
      StepHints("Trace the big dotted oval for my body!", "A little more around the body?", "So close — close up the oval!", "Soft sparkly body — yay!"),
      StepHints("Trace my tall magic horn on top!", "A bit more on the horn?", "So close — finish the horn tip!", "My magic horn shines!"),
      StepHints("Trace two little ears by my horn!", "One more little ear?", "Almost — finish that ear!", "Cute little ears!"),
      StepHints("Pop two sparkly eyes on my face!", "One more eye, please!", "Nearly — finish that eye!", "Sparkly eyes see the rainbow!"),
      StepHints("Trace my swirly mane down the side!", "A bit more swirl?", "So close — finish the curls!", "Swish — magic mane!"),
      StepHints("Trace my long flowy tail!", "A little more tail?", "Tail-almost — finish the curl!", "Flowy tail — off we go!"),
  )

  val help: List<HelpArticleDto> = listOf(
      HelpArticleDto(
          id = "start-drawing",
          title = "How a lesson works",
          body =
              "Each step shows a dotted shape and a coach dot that traces it first — then you " +
                  "trace it too. Your parts stay on the page, so piece by piece you draw the whole " +
                  "animal yourself. The coach never grades you; it just cheers you on to the next part.",
      ),
      HelpArticleDto(
          id = "troubleshoot-blank",
          title = "My drawing is not showing up",
          body =
              "Make sure the canvas is in color (not the eraser) and that your screen brightness " +
                  "is comfortable. If nothing appears, try bigger, slower lines along the dots — the " +
                  "coach loves to see your marks!",
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
              "The longer you play and the more parts you trace, the more practice stars you save " +
                  "up. When you finish the last part, your animal can wiggle, hop, or splash in a " +
                  "little celebration.",
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
