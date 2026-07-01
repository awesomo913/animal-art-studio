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

/** Chunky, friendly star fish — **Bubbles** — for the Splish-Splash Show. */
@Composable
fun BubblesFishHero(modifier: Modifier = Modifier) {
  val t = rememberInfiniteTransition(label = "bubbles")
  val swim =
      t.animateFloat(
          -4f, 4f,
          infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "swim",
      )
  val tailFlick =
      t.animateFloat(
          -6f, 6f,
          infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "tail",
      )
  Box(modifier.fillMaxWidth().height(130.dp), contentAlignment = Alignment.Center) {
    Canvas(Modifier.size(128.dp)) {
      val w = size.width
      val h = size.height
      val ox = swim.value
      val orange = Brush.verticalGradient(listOf(Color(0xFFFFB067), Color(0xFFF07B3F)))
      val deep = Color(0xFFDE6A32)
      val cx = w * 0.46f + ox
      val cy = h * 0.52f

      // fan tail (behind), flicks
      val tail = Path().apply {
        moveTo(cx + w * 0.22f, cy)
        lineTo(w * 0.92f, cy - h * 0.14f + tailFlick.value)
        lineTo(w * 0.92f, cy + h * 0.14f + tailFlick.value)
        close()
      }
      drawPath(tail, deep)

      // dorsal fin
      val fin = Path().apply {
        moveTo(cx - w * 0.06f, cy - h * 0.15f)
        lineTo(cx + w * 0.06f, cy - h * 0.15f)
        lineTo(cx, cy - h * 0.30f)
        close()
      }
      drawPath(fin, deep)

      // body
      drawOval(orange, topLeft = Offset(cx - w * 0.24f, cy - h * 0.17f), size = Size(w * 0.48f, h * 0.34f))

      // eye (front / left)
      val ex = cx - w * 0.16f
      val ey = cy - h * 0.03f
      drawCircle(Color.White, w * 0.045f, Offset(ex, ey))
      drawCircle(Color(0xFF23262E), w * 0.026f, Offset(ex, ey))
      drawCircle(Color.White, w * 0.010f, Offset(ex - w * 0.008f, ey - h * 0.008f))

      // smile
      drawArc(deep, 20f, 120f, false,
          topLeft = Offset(cx - w * 0.22f, cy + h * 0.01f),
          size = Size(w * 0.12f, h * 0.10f),
          style = Stroke(width = 3f, cap = StrokeCap.Round))

      // little bubble
      drawCircle(Color(0xFF8CC8FA).copy(alpha = 0.7f), w * 0.03f, Offset(cx - w * 0.30f, cy - h * 0.16f))
    }
  }
}
