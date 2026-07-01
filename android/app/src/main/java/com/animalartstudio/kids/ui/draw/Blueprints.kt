package com.animalartstudio.kids.ui.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * The three star animals, decomposed from their hero art (the
 * ui/star/heroes package) into an ordered list of traceable features. Feature order
 * MUST line up 1:1 with the lesson steps in `local/LocalContent.kt` — step N
 * teaches feature N.
 *
 * Coordinates are 0..1 (see [AnimalBlueprint]). Values echo the hero drawings so
 * the guide the kid traces produces the same friendly character.
 */

// Theme inks (kept in sync with StarShowCatalog palettes).
private val PenguinNavy = Color(0xFF1E3A5F)
private val PenguinBelly = Color(0xFF6FA8CF) // soft blue so the white tummy is visible as a guide
private val PenguinOrange = Color(0xFFFF8A5B)
private val PenguinBlue = Color(0xFF4A9FD4)

private val CatGrey = Color(0xFF7C838F)
private val CatPink = Color(0xFFFF8FB0)
private val CatDark = Color(0xFF3A3E46)

private val DogBrown = Color(0xFF9A5F30)
private val DogDark = Color(0xFF3A2A22)
private val DogPink = Color(0xFFFF8FA6)

private fun p(x: Float, y: Float) = Offset(x, y)

private val penguinBlueprint =
    AnimalBlueprint(
        animalKey = "penguin",
        features =
            listOf(
                Feature(
                    id = "body",
                    color = PenguinNavy,
                    strokes = listOf(ellipse01(0.50f, 0.53f, 0.26f, 0.30f)),
                ),
                Feature(
                    id = "belly",
                    color = PenguinBelly,
                    strokes = listOf(ellipse01(0.50f, 0.58f, 0.155f, 0.20f)),
                ),
                Feature(
                    id = "eyes",
                    color = PenguinNavy,
                    strokes =
                        listOf(
                            circle01(0.41f, 0.44f, 0.045f),
                            circle01(0.59f, 0.44f, 0.045f),
                        ),
                ),
                Feature(
                    id = "beak",
                    color = PenguinOrange,
                    strokes =
                        listOf(
                            poly01(listOf(p(0.44f, 0.52f), p(0.56f, 0.52f), p(0.50f, 0.60f))),
                        ),
                ),
                Feature(
                    id = "feet",
                    color = PenguinOrange,
                    strokes =
                        listOf(
                            ellipse01(0.41f, 0.85f, 0.07f, 0.03f),
                            ellipse01(0.59f, 0.85f, 0.07f, 0.03f),
                        ),
                ),
                Feature(
                    id = "wings",
                    color = PenguinBlue,
                    strokes =
                        listOf(
                            quad01(p(0.26f, 0.42f), p(0.13f, 0.56f), p(0.29f, 0.69f)),
                            quad01(p(0.74f, 0.42f), p(0.87f, 0.56f), p(0.71f, 0.69f)),
                        ),
                ),
            ),
    )

private val catBlueprint =
    AnimalBlueprint(
        animalKey = "cat",
        features =
            listOf(
                Feature(
                    id = "body",
                    color = CatGrey,
                    strokes = listOf(ellipse01(0.50f, 0.56f, 0.24f, 0.28f)),
                ),
                Feature(
                    id = "ears",
                    color = CatGrey,
                    strokes =
                        listOf(
                            poly01(listOf(p(0.34f, 0.34f), p(0.29f, 0.15f), p(0.45f, 0.29f))),
                            poly01(listOf(p(0.66f, 0.34f), p(0.71f, 0.15f), p(0.55f, 0.29f))),
                        ),
                ),
                Feature(
                    id = "eyes",
                    color = CatDark,
                    strokes =
                        listOf(
                            circle01(0.42f, 0.50f, 0.035f),
                            circle01(0.58f, 0.50f, 0.035f),
                        ),
                ),
                Feature(
                    id = "nose",
                    color = CatPink,
                    strokes =
                        listOf(
                            poly01(listOf(p(0.47f, 0.58f), p(0.53f, 0.58f), p(0.50f, 0.63f))),
                        ),
                ),
                Feature(
                    id = "smile",
                    color = CatDark,
                    strokes =
                        listOf(
                            arc01(0.455f, 0.63f, 0.045f, 0.04f, 20f, 140f),
                            arc01(0.545f, 0.63f, 0.045f, 0.04f, 20f, 140f),
                        ),
                ),
                Feature(
                    id = "whiskers",
                    color = CatGrey,
                    strokes =
                        listOf(
                            line01(p(0.40f, 0.62f), p(0.24f, 0.60f)),
                            line01(p(0.40f, 0.65f), p(0.24f, 0.66f)),
                            line01(p(0.60f, 0.62f), p(0.76f, 0.60f)),
                            line01(p(0.60f, 0.65f), p(0.76f, 0.66f)),
                        ),
                ),
                Feature(
                    id = "tail",
                    color = CatGrey,
                    strokes = listOf(quad01(p(0.72f, 0.66f), p(0.95f, 0.56f), p(0.86f, 0.30f))),
                ),
            ),
    )

private val dogBlueprint =
    AnimalBlueprint(
        animalKey = "dog",
        features =
            listOf(
                Feature(
                    id = "body",
                    color = DogBrown,
                    strokes = listOf(ellipse01(0.50f, 0.56f, 0.24f, 0.27f)),
                ),
                Feature(
                    id = "ears",
                    color = DogBrown,
                    strokes =
                        listOf(
                            ellipse01(0.28f, 0.45f, 0.06f, 0.16f),
                            ellipse01(0.72f, 0.45f, 0.06f, 0.16f),
                        ),
                ),
                Feature(
                    id = "eyes",
                    color = DogDark,
                    strokes =
                        listOf(
                            circle01(0.42f, 0.48f, 0.033f),
                            circle01(0.58f, 0.48f, 0.033f),
                        ),
                ),
                Feature(
                    id = "nose",
                    color = DogDark,
                    strokes = listOf(ellipse01(0.50f, 0.575f, 0.05f, 0.038f)),
                ),
                Feature(
                    id = "smile",
                    color = DogBrown,
                    strokes = listOf(arc01(0.50f, 0.60f, 0.09f, 0.06f, 15f, 150f)),
                ),
                Feature(
                    id = "tongue",
                    color = DogPink,
                    strokes = listOf(ellipse01(0.50f, 0.69f, 0.04f, 0.052f)),
                ),
                Feature(
                    id = "tail",
                    color = DogBrown,
                    strokes = listOf(quad01(p(0.72f, 0.62f), p(0.93f, 0.50f), p(0.90f, 0.32f))),
                ),
            ),
    )

private val BunnyGrey = Color(0xFF9AA0AC)
private val BunnyPink = Color(0xFFFF9DB6)
private val BunnyDark = Color(0xFF3A3E46)
private val FishOrange = Color(0xFFF07B3F)
private val FishDeep = Color(0xFFDE6A32)
private val FishDark = Color(0xFF23262E)
private val DinoGreen = Color(0xFF69A857)
private val DinoDeep = Color(0xFF4E8A44)
private val DinoDark = Color(0xFF23262E)
private val UniLav = Color(0xFFC3AEE3)
private val UniGold = Color(0xFFF2C24B)
private val UniPink = Color(0xFFFF8FC7)
private val UniDark = Color(0xFF3A2E4E)

private val bunnyBlueprint =
    AnimalBlueprint(
        animalKey = "bunny",
        features =
            listOf(
                Feature(id = "body", color = BunnyGrey,
                    strokes = listOf(ellipse01(0.50f, 0.60f, 0.22f, 0.26f))),
                Feature(id = "ears", color = BunnyPink,
                    strokes = listOf(
                        ellipse01(0.41f, 0.28f, 0.055f, 0.17f),
                        ellipse01(0.59f, 0.28f, 0.055f, 0.17f))),
                Feature(id = "eyes", color = BunnyDark,
                    strokes = listOf(circle01(0.42f, 0.55f, 0.03f), circle01(0.58f, 0.55f, 0.03f))),
                Feature(id = "nose", color = BunnyPink,
                    strokes = listOf(poly01(listOf(p(0.47f, 0.62f), p(0.53f, 0.62f), p(0.50f, 0.66f))))),
                Feature(id = "tail", color = BunnyGrey,
                    strokes = listOf(circle01(0.73f, 0.71f, 0.05f))),
            ),
    )

private val fishBlueprint =
    AnimalBlueprint(
        animalKey = "fish",
        features =
            listOf(
                Feature(id = "body", color = FishOrange,
                    strokes = listOf(ellipse01(0.46f, 0.52f, 0.24f, 0.17f))),
                Feature(id = "tail", color = FishDeep,
                    strokes = listOf(poly01(listOf(p(0.68f, 0.52f), p(0.90f, 0.40f), p(0.90f, 0.64f))))),
                Feature(id = "fin", color = FishDeep,
                    strokes = listOf(poly01(listOf(p(0.40f, 0.37f), p(0.52f, 0.37f), p(0.46f, 0.22f))))),
                Feature(id = "eye", color = FishDark,
                    strokes = listOf(circle01(0.30f, 0.49f, 0.03f))),
                Feature(id = "smile", color = FishDeep,
                    strokes = listOf(arc01(0.30f, 0.56f, 0.05f, 0.035f, 20f, 120f))),
            ),
    )

private val dinoBlueprint =
    AnimalBlueprint(
        animalKey = "dino",
        features =
            listOf(
                Feature(id = "body", color = DinoGreen,
                    strokes = listOf(ellipse01(0.50f, 0.60f, 0.24f, 0.22f))),
                Feature(id = "head", color = DinoGreen,
                    strokes = listOf(ellipse01(0.34f, 0.38f, 0.13f, 0.13f))),
                Feature(id = "eye", color = DinoDark,
                    strokes = listOf(circle01(0.30f, 0.35f, 0.028f))),
                Feature(id = "spikes", color = DinoDeep,
                    strokes = listOf(
                        poly01(listOf(p(0.44f, 0.46f), p(0.50f, 0.30f), p(0.56f, 0.46f))),
                        poly01(listOf(p(0.57f, 0.46f), p(0.63f, 0.30f), p(0.69f, 0.46f))),
                        poly01(listOf(p(0.70f, 0.46f), p(0.76f, 0.30f), p(0.82f, 0.46f))))),
                Feature(id = "tail", color = DinoDeep,
                    strokes = listOf(quad01(p(0.72f, 0.64f), p(0.94f, 0.60f), p(0.90f, 0.42f)))),
                Feature(id = "legs", color = DinoDeep,
                    strokes = listOf(
                        ellipse01(0.41f, 0.81f, 0.05f, 0.07f),
                        ellipse01(0.59f, 0.81f, 0.05f, 0.07f))),
            ),
    )

private val unicornBlueprint =
    AnimalBlueprint(
        animalKey = "unicorn",
        features =
            listOf(
                Feature(id = "body", color = UniLav,
                    strokes = listOf(ellipse01(0.50f, 0.60f, 0.23f, 0.24f))),
                Feature(id = "horn", color = UniGold,
                    strokes = listOf(poly01(listOf(p(0.46f, 0.34f), p(0.54f, 0.34f), p(0.50f, 0.15f))))),
                Feature(id = "ears", color = UniLav,
                    strokes = listOf(
                        poly01(listOf(p(0.40f, 0.38f), p(0.34f, 0.28f), p(0.45f, 0.30f))),
                        poly01(listOf(p(0.60f, 0.38f), p(0.66f, 0.28f), p(0.55f, 0.30f))))),
                Feature(id = "eyes", color = UniDark,
                    strokes = listOf(circle01(0.40f, 0.55f, 0.028f), circle01(0.60f, 0.55f, 0.028f))),
                Feature(id = "mane", color = UniPink,
                    strokes = listOf(
                        quad01(p(0.31f, 0.41f), p(0.17f, 0.52f), p(0.31f, 0.60f)),
                        quad01(p(0.30f, 0.50f), p(0.16f, 0.61f), p(0.30f, 0.69f)))),
                Feature(id = "tail", color = UniPink,
                    strokes = listOf(quad01(p(0.72f, 0.60f), p(0.92f, 0.66f), p(0.86f, 0.40f)))),
            ),
    )

/** Blueprint for a lesson's animal; penguin is the safe default. */
fun blueprintFor(animalKey: String): AnimalBlueprint =
    when (animalKey) {
      "cat" -> catBlueprint
      "dog" -> dogBlueprint
      "bunny" -> bunnyBlueprint
      "fish" -> fishBlueprint
      "dino" -> dinoBlueprint
      "unicorn" -> unicornBlueprint
      else -> penguinBlueprint
    }
