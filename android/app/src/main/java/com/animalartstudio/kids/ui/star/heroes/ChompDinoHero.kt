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

/** Chunky, friendly star dinosaur — **Chomp** — for the Stomp-Stomp Show. */
@Composable
fun ChompDinoHero(modifier: Modifier = Modifier) {
  val t = rememberInfiniteTransition(label = "chomp")
  val bob =
      t.animateFloat(
          0f, -4f,
          infiniteRepeatable(tween(1250, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "bob",
      )
  Box(modifier.fillMaxWidth().height(130.dp), contentAlignment = Alignment.Center) {
    Canvas(Modifier.size(128.dp)) {
      val w = size.width
      val h = size.height
      val oy = bob.value
      val green = Brush.verticalGradient(listOf(Color(0xFF8FCB77), Color(0xFF69A857)))
      val deep = Color(0xFF4E8A44)

      // tail (behind)
      val tail = Path().apply {
        moveTo(w * 0.72f, h * 0.64f + oy)
        quadraticBezierTo(w * 0.94f, h * 0.60f + oy, w * 0.90f, h * 0.42f + oy)
      }
      drawPath(tail, deep, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round))

      // legs
      drawOval(deep, topLeft = Offset(w * 0.36f, h * 0.74f + oy), size = Size(w * 0.10f, h * 0.14f))
      drawOval(deep, topLeft = Offset(w * 0.54f, h * 0.74f + oy), size = Size(w * 0.10f, h * 0.14f))

      // back spikes
      for (sx in listOf(0.44f, 0.57f, 0.70f)) {
        val p = Path().apply {
          moveTo(w * sx, h * 0.46f + oy)
          lineTo(w * (sx + 0.06f), h * 0.30f + oy)
          lineTo(w * (sx + 0.12f), h * 0.46f + oy)
          close()
        }
        drawPath(p, deep)
      }

      // body
      drawOval(green, topLeft = Offset(w * 0.26f, h * 0.38f + oy), size = Size(w * 0.48f, h * 0.44f))
      // round head (up-left)
      drawOval(green, topLeft = Offset(w * 0.21f, h * 0.25f + oy), size = Size(w * 0.26f, h * 0.26f))
      // tummy
      drawOval(Color(0xFFE7F3D9), topLeft = Offset(w * 0.36f, h * 0.54f + oy), size = Size(w * 0.28f, h * 0.24f))

      // eye + sparkle
      val ex = w * 0.30f
      val ey = h * 0.35f + oy
      drawCircle(Color(0xFF23262E), w * 0.030f, Offset(ex, ey))
      drawCircle(Color.White, w * 0.011f, Offset(ex - w * 0.008f, ey - h * 0.008f))
      // smile
      drawArc(deep, 15f, 130f, false,
          topLeft = Offset(w * 0.28f, h * 0.38f + oy),
          size = Size(w * 0.14f, h * 0.10f),
          style = Stroke(width = 2.8f, cap = StrokeCap.Round))
    }
  }
}
