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

/** Chunky, friendly star bunny — **Cotton** — for the Hop-Hop Show. */
@Composable
fun CottonBunnyHero(modifier: Modifier = Modifier) {
  val t = rememberInfiniteTransition(label = "cotton")
  val bob =
      t.animateFloat(
          0f, -5f,
          infiniteRepeatable(tween(1150, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "bob",
      )
  val earTwitch =
      t.animateFloat(
          -3f, 3f,
          infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "ear",
      )
  Box(modifier.fillMaxWidth().height(130.dp), contentAlignment = Alignment.Center) {
    Canvas(Modifier.size(128.dp)) {
      val w = size.width
      val h = size.height
      val oy = bob.value
      val wx = w / 2f
      val fur = Brush.verticalGradient(listOf(Color(0xFFF3F1F7), Color(0xFFDDD9E6)))
      val furLine = Color(0xFF9AA0AC)
      val pink = Color(0xFFFF9DB6)

      // ears (behind body), gentle twitch
      for (s in listOf(-1f, 1f)) {
        val ex = wx + s * w * 0.09f + earTwitch.value * s
        drawOval(furLine.copy(alpha = 0.9f),
            topLeft = Offset(ex - w * 0.055f, h * 0.10f + oy),
            size = Size(w * 0.11f, h * 0.30f))
        drawOval(pink,
            topLeft = Offset(ex - w * 0.03f, h * 0.14f + oy),
            size = Size(w * 0.06f, h * 0.22f))
      }

      // body / head blob
      val bodyW = w * 0.44f
      val bodyH = h * 0.52f
      val top = h * 0.34f + oy
      drawOval(fur, topLeft = Offset(wx - bodyW / 2f, top), size = Size(bodyW, bodyH))

      // fluffy tail
      drawCircle(Color(0xFFFBFAFD), w * 0.055f, Offset(wx + bodyW * 0.6f, top + bodyH * 0.72f))

      // cheeks
      drawCircle(pink.copy(alpha = 0.7f), w * 0.05f, Offset(wx - bodyW * 0.34f, top + bodyH * 0.5f))
      drawCircle(pink.copy(alpha = 0.7f), w * 0.05f, Offset(wx + bodyW * 0.34f, top + bodyH * 0.5f))

      // eyes with sparkle
      val eyeY = top + bodyH * 0.4f
      val er = w * 0.032f
      for (s in listOf(-1f, 1f)) {
        val ex = wx + s * bodyW * 0.28f
        drawCircle(Color(0xFF2A2E36), er, Offset(ex, eyeY))
        drawCircle(Color.White, er * 0.34f, Offset(ex - er * 0.3f, eyeY - er * 0.3f))
      }

      // nose + tiny mouth
      val nose = Path().apply {
        moveTo(wx - w * 0.03f, top + bodyH * 0.52f)
        lineTo(wx + w * 0.03f, top + bodyH * 0.52f)
        lineTo(wx, top + bodyH * 0.60f)
        close()
      }
      drawPath(nose, pink)
      drawArc(furLine, 20f, 140f, false,
          topLeft = Offset(wx - bodyW * 0.10f, top + bodyH * 0.58f),
          size = Size(bodyW * 0.20f, bodyH * 0.12f),
          style = Stroke(width = 2.6f, cap = StrokeCap.Round))
    }
  }
}
