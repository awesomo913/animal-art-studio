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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** Chunky, friendly star unicorn — **Sparkle** — for the Rainbow Show. */
@Composable
fun SparkleUnicornHero(modifier: Modifier = Modifier) {
  val t = rememberInfiniteTransition(label = "sparkle")
  val bob =
      t.animateFloat(
          0f, -5f,
          infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "bob",
      )
  val maneWave =
      t.animateFloat(
          -3f, 3f,
          infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "mane",
      )
  Box(modifier.fillMaxWidth().height(130.dp), contentAlignment = Alignment.Center) {
    Canvas(Modifier.size(128.dp)) {
      val w = size.width
      val h = size.height
      val oy = bob.value
      val wx = w / 2f
      val body = Brush.verticalGradient(listOf(Color(0xFFF6F1FF), Color(0xFFE3D8F5)))
      val lav = Color(0xFFC3AEE3)
      val gold = Color(0xFFF2C24B)
      val pink = Color(0xFFFF8FC7)

      // tail (behind), waves
      val tail = Path().apply {
        moveTo(w * 0.72f, h * 0.60f + oy)
        quadraticBezierTo(w * 0.94f, h * 0.66f + oy + maneWave.value, w * 0.86f, h * 0.40f + oy)
      }
      drawPath(tail, pink, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round))

      // horn
      val horn = Path().apply {
        moveTo(wx - w * 0.04f, h * 0.34f + oy)
        lineTo(wx + w * 0.04f, h * 0.34f + oy)
        lineTo(wx, h * 0.15f + oy)
        close()
      }
      drawPath(horn, gold)

      // ears
      for (s in listOf(-1f, 1f)) {
        val p = Path().apply {
          moveTo(wx + s * w * 0.10f, h * 0.38f + oy)
          lineTo(wx + s * w * 0.16f, h * 0.28f + oy)
          lineTo(wx + s * w * 0.05f, h * 0.30f + oy)
          close()
        }
        drawPath(p, lav)
      }

      // body / head blob
      val bodyW = w * 0.46f
      val bodyH = h * 0.48f
      val top = h * 0.36f + oy
      drawOval(body, topLeft = Offset(wx - bodyW / 2f, top), size = Size(bodyW, bodyH))

      // mane curls (left)
      for (k in 0..1) {
        val m = Path().apply {
          moveTo(wx - bodyW * 0.42f, top + bodyH * (0.10f + k * 0.18f))
          quadraticBezierTo(
              wx - bodyW * 0.72f + maneWave.value, top + bodyH * (0.28f + k * 0.18f),
              wx - bodyW * 0.40f, top + bodyH * (0.42f + k * 0.18f))
        }
        drawPath(m, pink, style = Stroke(width = w * 0.05f, cap = StrokeCap.Round))
      }

      // cheeks
      drawCircle(pink.copy(alpha = 0.6f), w * 0.05f, Offset(wx - bodyW * 0.32f, top + bodyH * 0.52f))
      drawCircle(pink.copy(alpha = 0.6f), w * 0.05f, Offset(wx + bodyW * 0.32f, top + bodyH * 0.52f))

      // eyes + sparkle
      val eyeY = top + bodyH * 0.42f
      val er = w * 0.030f
      for (s in listOf(-1f, 1f)) {
        val ex = wx + s * bodyW * 0.26f
        drawCircle(Color(0xFF3A2E4E), er, Offset(ex, eyeY))
        drawCircle(Color.White, er * 0.34f, Offset(ex - er * 0.3f, eyeY - er * 0.3f))
      }
      // smile
      drawArc(lav, 20f, 130f, false,
          topLeft = Offset(wx - bodyW * 0.10f, top + bodyH * 0.54f),
          size = Size(bodyW * 0.20f, bodyH * 0.12f),
          style = Stroke(width = 2.6f, cap = StrokeCap.Round))
    }
  }
}
