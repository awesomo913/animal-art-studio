package com.animalartstudio.kids.ui.star

import androidx.compose.ui.graphics.Color

/**
 * Each lesson is a **show** with one animal as the star. Per-star palette, copy hooks, and hero
 * identity live here; add a new block when you add a new lesson `lessonId`.
 */
data class StarShowUi(
    val buddyName: String,
    /** Shown under the hero, e.g. "Waddles' Splashy Show" */
    val showTitle: String,
    /** One line on the home card */
    val homeCardLine: String,
    /** Short line next to the mascot on the lesson screen */
    val stageLine: String,
    val trickLabel: String, // e.g. "Waddles' trick"
    val askBuddyButton: String,
    val coachBubblePrefix: String, // e.g. "Waddles says"
    val paper: Color,
    val gradientTop: Color,
    val gradientBottom: Color,
    val accent: Color,
    val inkChoices: List<Color>,
    val celebrateTitle: String,
    val celebrateSub: String,
    val celebrateSfxLine: String,
)

private val penguinShow =
    StarShowUi(
        buddyName = "Waddles",
        showTitle = "Waddles' Splashy Show",
        homeCardLine = "Starring Waddles — you're the co-star!",
        stageLine = "Tonight's star: Waddles the penguin",
        trickLabel = "Waddles' trick",
        askBuddyButton = "Tell Waddles!",
        coachBubblePrefix = "Waddles says",
        paper = Color(0xFFFFF6EB),
        gradientTop = Color(0xFFE8F4FF),
        gradientBottom = Color(0xFFFFF4DE),
        accent = Color(0xFF3B7BA4),
        inkChoices =
            listOf(
                Color(0xFF1E3A5F), // navy
                Color(0xFFFF8A5B), // beak orange
                Color(0xFF4A9FD4), // splash blue
            ),
        celebrateTitle = "Big splash! Waddles does a victory waddle!",
        celebrateSub = "Your drawing is the star of the party — Waddles put on a tiny dance with it.",
        celebrateSfxLine = "Waddle • boing • splash! (Add real sounds in res/raw when you're ready.)",
    )

private val defaultShow =
    StarShowUi(
        buddyName = "Buddy",
        showTitle = "Drawing show",
        homeCardLine = "You're the co-star!",
        stageLine = "Tonight's star: your new friend",
        trickLabel = "Try this",
        askBuddyButton = "Tell your friend!",
        coachBubblePrefix = "Friend says",
        paper = Color(0xFFFFF6EB),
        gradientTop = Color(0xFFFFF4DE),
        gradientBottom = Color(0xFFFFF4DE),
        accent = Color(0xFF2E6B4A),
        inkChoices = listOf(Color(0xFF2E6B4A), Color(0xFF6A4CFF), Color(0xFFFF6B6B)),
        celebrateTitle = "Hooray! A wiggly moment!",
        celebrateSub = "Your art made the show.",
        celebrateSfxLine = "Happy sounds go here.",
    )

fun starShowForLesson(
    lessonId: String,
    animalKey: String,
): StarShowUi =
    when (lessonId) {
      "penguin-happy" -> penguinShow
      else ->
          when (animalKey) {
            "penguin" -> penguinShow
            else -> defaultShow
          }
    }
