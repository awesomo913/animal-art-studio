package com.animalartstudio.kids.ui.star.heroes

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Central dispatcher: given a lesson's `animalKey`, draw that star.
 * Add a `when` branch here when you add a new hero Composable.
 */
@Composable
fun AnimalHero(animalKey: String, modifier: Modifier = Modifier) {
  when (animalKey) {
    "cat" -> MimiCatHero(modifier)
    "dog" -> BiscuitDogHero(modifier)
    "bunny" -> CottonBunnyHero(modifier)
    "fish" -> BubblesFishHero(modifier)
    "dino" -> ChompDinoHero(modifier)
    "unicorn" -> SparkleUnicornHero(modifier)
    else -> WaddlesPenguinHero(modifier)
  }
}

/** Chunky, friendly star kitten — **Mimi** — for the Purr Parade. */
@Composable
fun MimiCatHero(modifier: Modifier = Modifier) {
  val t = rememberInfiniteTransition(label = "mimi")
  val bob =
      t.animateFloat(
          0f, -5f,
          infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "bob",
      )
  val tailWag =
      t.animateFloat(
          -8f, 8f,
          infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "tail",
      )
  Box(modifier.fillMaxWidth().height(130.dp), contentAlignment = Alignment.Center) {
    Canvas(Modifier.size(128.dp)) {
      val w = size.width
      val h = size.height
      val oy = bob.value
      val wx = w / 2f
      val grey = Brush.verticalGradient(listOf(Color(0xFFB8BEC7), Color(0xFF98A0AC)))
      val darkGrey = Color(0xFF7C838F)
      val pink = Color(0xFFFFAFC2)

      val bodyW = w * 0.70f
      val bodyH = h * 0.66f
      val left = wx - bodyW / 2f
      val top = h * 0.24f + oy

      // swishy tail (behind body), gentle wag
      val tail =
          Path().apply {
            moveTo(left + bodyW * 0.86f, top + bodyH * 0.72f)
            quadraticBezierTo(
                left + bodyW * 1.20f, top + bodyH * (0.55f) + tailWag.value,
                left + bodyW * 1.02f, top + bodyH * (0.20f) + tailWag.value,
            )
          }
      drawPath(tail, darkGrey, style = Stroke(width = bodyW * 0.16f, cap = StrokeCap.Round))

      // ears (triangles) behind the head
      fun ear(cx: Float, inner: Boolean) {
        val ew = bodyW * 0.22f
        val p = Path().apply {
          moveTo(cx - ew / 2f, top + bodyH * 0.16f)
          lineTo(cx, top - bodyH * 0.14f)
          lineTo(cx + ew / 2f, top + bodyH * 0.16f)
          close()
        }
        drawPath(p, if (inner) pink else darkGrey)
      }
      ear(wx - bodyW * 0.30f, false)
      ear(wx + bodyW * 0.30f, false)

      // body / head blob
      drawRoundRect(
          brush = grey,
          topLeft = Offset(left, top),
          size = Size(bodyW, bodyH),
          cornerRadius = CornerRadius(bodyW * 0.46f, bodyH * 0.5f),
      )

      // inner ears
      val iew = bodyW * 0.11f
      fun innerEar(cx: Float) {
        val p = Path().apply {
          moveTo(cx - iew / 2f, top + bodyH * 0.10f)
          lineTo(cx, top - bodyH * 0.02f)
          lineTo(cx + iew / 2f, top + bodyH * 0.10f)
          close()
        }
        drawPath(p, pink)
      }
      innerEar(wx - bodyW * 0.30f)
      innerEar(wx + bodyW * 0.30f)

      // muzzle
      val mzW = bodyW * 0.5f
      drawOval(
          Color(0xFFFFF7F0),
          topLeft = Offset(wx - mzW / 2f, top + bodyH * 0.40f),
          size = Size(mzW, bodyH * 0.42f),
      )
      // cheeks
      drawCircle(pink.copy(alpha = 0.8f), bodyW * 0.07f, Offset(wx - bodyW * 0.24f, top + bodyH * 0.5f))
      drawCircle(pink.copy(alpha = 0.8f), bodyW * 0.07f, Offset(wx + bodyW * 0.24f, top + bodyH * 0.5f))

      // eyes with sparkle
      val eyeY = top + bodyH * 0.40f
      val sep = bodyW * 0.20f
      val er = bodyW * 0.075f
      for (s in listOf(-1f, 1f)) {
        val ex = wx + s * sep
        drawCircle(Color(0xFF2A2E36), er, Offset(ex, eyeY))
        drawCircle(Color.White, er * 0.34f, Offset(ex - er * 0.3f, eyeY - er * 0.34f))
      }
      // nose
      val nose = Path().apply {
        moveTo(wx - bodyW * 0.05f, top + bodyH * 0.52f)
        lineTo(wx + bodyW * 0.05f, top + bodyH * 0.52f)
        lineTo(wx, top + bodyH * 0.60f)
        close()
      }
      drawPath(nose, pink)
      // smile
      drawArc(darkGrey, 20f, 130f, false,
          topLeft = Offset(wx - bodyW * 0.10f, top + bodyH * 0.55f),
          size = Size(bodyW * 0.20f, bodyH * 0.14f),
          style = Stroke(width = 3f, cap = StrokeCap.Round))

      // whiskers
      for (s in listOf(-1f, 1f)) {
        for (k in 0..1) {
          val y = top + bodyH * (0.55f + k * 0.07f)
          drawLine(darkGrey,
              Offset(wx + s * bodyW * 0.16f, y),
              Offset(wx + s * bodyW * 0.44f, y - (k - 0.5f) * 8f),
              strokeWidth = 2.4f, cap = StrokeCap.Round)
        }
      }
    }
  }
}

/** Chunky, friendly star puppy — **Biscuit** — for the Waggy Show. */
@Composable
fun BiscuitDogHero(modifier: Modifier = Modifier) {
  val t = rememberInfiniteTransition(label = "biscuit")
  val bob =
      t.animateFloat(
          0f, -5f,
          infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "bob",
      )
  val wag =
      t.animateFloat(
          -10f, 10f,
          infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "wag",
      )
  Box(modifier.fillMaxWidth().height(130.dp), contentAlignment = Alignment.Center) {
    Canvas(Modifier.size(128.dp)) {
      val w = size.width
      val h = size.height
      val oy = bob.value
      val wx = w / 2f
      val tan = Brush.verticalGradient(listOf(Color(0xFFD9A06A), Color(0xFFC2874F)))
      val brown = Color(0xFF9A5F30)
      val pink = Color(0xFFFFB2A6)

      val bodyW = w * 0.66f
      val bodyH = h * 0.64f
      val left = wx - bodyW / 2f
      val top = h * 0.26f + oy

      // waggy tail (behind)
      val tail = Path().apply {
        moveTo(left + bodyW * 0.90f, top + bodyH * 0.66f)
        quadraticBezierTo(
            left + bodyW * 1.22f, top + bodyH * 0.42f,
            left + bodyW * 1.10f + wag.value, top + bodyH * 0.16f,
        )
      }
      drawPath(tail, brown, style = Stroke(width = bodyW * 0.15f, cap = StrokeCap.Round))

      // floppy ears (behind head, on the sides)
      for (s in listOf(-1f, 1f)) {
        drawOval(
            brown,
            topLeft = Offset(wx + s * bodyW * 0.48f - bodyW * 0.11f, top + bodyH * 0.04f),
            size = Size(bodyW * 0.22f, bodyH * 0.5f),
        )
      }

      // body / head blob
      drawRoundRect(
          brush = tan,
          topLeft = Offset(left, top),
          size = Size(bodyW, bodyH),
          cornerRadius = CornerRadius(bodyW * 0.44f, bodyH * 0.48f),
      )
      // a soft ear-spot patch over one eye
      drawCircle(brown.copy(alpha = 0.5f), bodyW * 0.17f, Offset(wx + bodyW * 0.22f, top + bodyH * 0.30f))

      // muzzle
      val mzW = bodyW * 0.56f
      drawOval(
          Color(0xFFFFF3E4),
          topLeft = Offset(wx - mzW / 2f, top + bodyH * 0.44f),
          size = Size(mzW, bodyH * 0.42f),
      )
      // cheeks
      drawCircle(pink.copy(alpha = 0.7f), bodyW * 0.07f, Offset(wx - bodyW * 0.26f, top + bodyH * 0.52f))
      drawCircle(pink.copy(alpha = 0.7f), bodyW * 0.07f, Offset(wx + bodyW * 0.26f, top + bodyH * 0.52f))

      // eyes with sparkle
      val eyeY = top + bodyH * 0.38f
      val sep = bodyW * 0.19f
      val er = bodyW * 0.072f
      for (s in listOf(-1f, 1f)) {
        val ex = wx + s * sep
        drawCircle(Color(0xFF2A2E36), er, Offset(ex, eyeY))
        drawCircle(Color.White, er * 0.34f, Offset(ex - er * 0.3f, eyeY - er * 0.34f))
      }
      // round nose
      drawOval(Color(0xFF3A2A22),
          topLeft = Offset(wx - bodyW * 0.07f, top + bodyH * 0.52f),
          size = Size(bodyW * 0.14f, bodyH * 0.10f))
      // smile
      drawArc(brown, 15f, 150f, false,
          topLeft = Offset(wx - bodyW * 0.13f, top + bodyH * 0.56f),
          size = Size(bodyW * 0.26f, bodyH * 0.16f),
          style = Stroke(width = 3.2f, cap = StrokeCap.Round))
      // little tongue
      drawRoundRect(pink,
          topLeft = Offset(wx - bodyW * 0.05f, top + bodyH * 0.66f),
          size = Size(bodyW * 0.10f, bodyH * 0.12f),
          cornerRadius = CornerRadius(6f, 6f))
    }
  }
}
