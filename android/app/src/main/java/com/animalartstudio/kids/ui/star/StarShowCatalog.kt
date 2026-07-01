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

private val catShow =
    StarShowUi(
        buddyName = "Mimi",
        showTitle = "Mimi's Purr Parade",
        homeCardLine = "Starring Mimi — you're the co-star!",
        stageLine = "Tonight's star: Mimi the kitten",
        trickLabel = "Mimi's trick",
        askBuddyButton = "Tell Mimi!",
        coachBubblePrefix = "Mimi says",
        paper = Color(0xFFFFF6EB),
        gradientTop = Color(0xFFF3EEFF),
        gradientBottom = Color(0xFFFFF0F5),
        accent = Color(0xFF8A6FB0),
        inkChoices =
            listOf(
                Color(0xFF7C838F), // cat grey
                Color(0xFFFF8FB0), // nose pink
                Color(0xFF6A4CFF), // playful purple
            ),
        celebrateTitle = "Purr-fect! Mimi does a happy parade wiggle!",
        celebrateSub = "Your drawing stole the show — Mimi is purring with joy!",
        celebrateSfxLine = "Purr • mew • swish! (Add real sounds in res/raw when you're ready.)",
    )

private val dogShow =
    StarShowUi(
        buddyName = "Biscuit",
        showTitle = "Biscuit's Waggy Show",
        homeCardLine = "Starring Biscuit — you're the co-star!",
        stageLine = "Tonight's star: Biscuit the puppy",
        trickLabel = "Biscuit's trick",
        askBuddyButton = "Tell Biscuit!",
        coachBubblePrefix = "Biscuit says",
        paper = Color(0xFFFFF6EB),
        gradientTop = Color(0xFFFFF1E0),
        gradientBottom = Color(0xFFFFF7E8),
        accent = Color(0xFFB07B4A),
        inkChoices =
            listOf(
                Color(0xFF9A5F30), // dog brown
                Color(0xFF3A2A22), // nose dark
                Color(0xFFFF8A5B), // waggy orange
            ),
        celebrateTitle = "WAG-WAG! Biscuit spins with happy zoomies!",
        celebrateSub = "Your drawing made Biscuit's whole tail wag — best show ever!",
        celebrateSfxLine = "Woof • pant • wag! (Add real sounds in res/raw when you're ready.)",
    )

private val bunnyShow =
    StarShowUi(
        buddyName = "Cotton",
        showTitle = "Cotton's Hop-Hop Show",
        homeCardLine = "Starring Cotton — you're the co-star!",
        stageLine = "Tonight's star: Cotton the bunny",
        trickLabel = "Cotton's trick",
        askBuddyButton = "Tell Cotton!",
        coachBubblePrefix = "Cotton says",
        paper = Color(0xFFFFF6EB),
        gradientTop = Color(0xFFF1F0F7),
        gradientBottom = Color(0xFFFFF0F5),
        accent = Color(0xFFB57BA0),
        inkChoices =
            listOf(
                Color(0xFF9AA0AC), // bunny grey
                Color(0xFFFF9DB6), // ear/nose pink
                Color(0xFF3A3E46), // soft charcoal
            ),
        celebrateTitle = "Hop-hop hooray! Cotton does a happy bounce!",
        celebrateSub = "Your drawing stole the show — Cotton is bouncing with joy!",
        celebrateSfxLine = "Hop • boing • wiggle! (Add real sounds in res/raw when you're ready.)",
    )

private val fishShow =
    StarShowUi(
        buddyName = "Bubbles",
        showTitle = "Bubbles' Splish-Splash Show",
        homeCardLine = "Starring Bubbles — you're the co-star!",
        stageLine = "Tonight's star: Bubbles the fish",
        trickLabel = "Bubbles' trick",
        askBuddyButton = "Tell Bubbles!",
        coachBubblePrefix = "Bubbles says",
        paper = Color(0xFFFFF6EB),
        gradientTop = Color(0xFFE6F5FF),
        gradientBottom = Color(0xFFFFF4DE),
        accent = Color(0xFF3B9BC4),
        inkChoices =
            listOf(
                Color(0xFFF07B3F), // fish orange
                Color(0xFFDE6A32), // deep orange
                Color(0xFF4A9FD4), // water blue
            ),
        celebrateTitle = "Splish-splash! Bubbles blows a happy bubble!",
        celebrateSub = "Your drawing made the whole tank sparkle — great swimming!",
        celebrateSfxLine = "Blub • splash • pop! (Add real sounds in res/raw when you're ready.)",
    )

private val dinoShow =
    StarShowUi(
        buddyName = "Chomp",
        showTitle = "Chomp's Stomp-Stomp Show",
        homeCardLine = "Starring Chomp — you're the co-star!",
        stageLine = "Tonight's star: Chomp the dinosaur",
        trickLabel = "Chomp's trick",
        askBuddyButton = "Tell Chomp!",
        coachBubblePrefix = "Chomp says",
        paper = Color(0xFFFFF6EB),
        gradientTop = Color(0xFFEAF6DF),
        gradientBottom = Color(0xFFFFF7E8),
        accent = Color(0xFF5E9A4E),
        inkChoices =
            listOf(
                Color(0xFF69A857), // dino green
                Color(0xFF4E8A44), // deep green
                Color(0xFF3A3E46), // soft charcoal
            ),
        celebrateTitle = "STOMP-STOMP! Chomp does a happy dino dance!",
        celebrateSub = "Your drawing made Chomp roar with joy — best show ever!",
        celebrateSfxLine = "Stomp • rawr • giggle! (Add real sounds in res/raw when you're ready.)",
    )

private val unicornShow =
    StarShowUi(
        buddyName = "Sparkle",
        showTitle = "Sparkle's Rainbow Show",
        homeCardLine = "Starring Sparkle — you're the co-star!",
        stageLine = "Tonight's star: Sparkle the unicorn",
        trickLabel = "Sparkle's trick",
        askBuddyButton = "Tell Sparkle!",
        coachBubblePrefix = "Sparkle says",
        paper = Color(0xFFFFF6EB),
        gradientTop = Color(0xFFF3EEFF),
        gradientBottom = Color(0xFFFFEFF7),
        accent = Color(0xFF9E7BC4),
        inkChoices =
            listOf(
                Color(0xFFC3AEE3), // lavender
                Color(0xFFFF8FC7), // mane pink
                Color(0xFFF2C24B), // horn gold
            ),
        celebrateTitle = "Sparkle-sparkle! A rainbow swirls around Sparkle!",
        celebrateSub = "Your drawing lit up the whole sky — pure magic!",
        celebrateSfxLine = "Shimmer • neigh • twinkle! (Add real sounds in res/raw when you're ready.)",
    )

fun starShowForLesson(
    lessonId: String,
    animalKey: String,
): StarShowUi =
    when (animalKey) {
      "penguin" -> penguinShow
      "cat" -> catShow
      "dog" -> dogShow
      "bunny" -> bunnyShow
      "fish" -> fishShow
      "dino" -> dinoShow
      "unicorn" -> unicornShow
      else ->
          when (lessonId) {
            "penguin-happy" -> penguinShow
            "cat-happy" -> catShow
            "dog-happy" -> dogShow
            "bunny-happy" -> bunnyShow
            "fish-happy" -> fishShow
            "dino-happy" -> dinoShow
            "unicorn-happy" -> unicornShow
            else -> defaultShow
          }
    }
