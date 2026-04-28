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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** Chunky, friendly star penguin — **Waddles** — for the Splashy Show. Built for readability on phones. */
@Composable
fun WaddlesPenguinHero(modifier: Modifier = Modifier) {
  val t = rememberInfiniteTransition(label = "waddles")
  val bob =
      t.animateFloat(
          0f,
          -5f,
          infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "bob",
      )
  val wobble =
      t.animateFloat(
          -2f,
          2f,
          infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
          label = "wobble",
      )
  Box(modifier.fillMaxWidth().height(130.dp), contentAlignment = Alignment.Center) {
    Canvas(Modifier.size(128.dp)) {
      val w = size.width
      val h = size.height
      val oy = bob.value
      val wx = w / 2f + wobble.value * 2f
      val navy =
          Brush.verticalGradient(colors = listOf(Color(0xFF2F5880), Color(0xFF1E3A5F)))
      val bodyW = w * 0.74f
      val bodyH = h * 0.62f
      val left = wx - bodyW / 2f
      val top = h * 0.26f + oy

      drawRoundRect(
          brush = navy,
          topLeft = Offset(left, top),
          size = Size(bodyW, bodyH),
          cornerRadius = CornerRadius(bodyW * 0.48f, bodyH * 0.52f),
      )
      val bellyW = bodyW * 0.62f
      val bellyH = bodyH * 0.5f
      drawOval(
          Color(0xFFFFF8F2),
          topLeft = Offset(wx - bellyW / 2f, top + bodyH * 0.24f),
          size = Size(bellyW, bellyH),
      )
      val cheek = Color(0xFFFFCCD8).copy(alpha = 0.85f)
      drawCircle(cheek, bodyW * 0.068f, center = Offset(left + bodyW * 0.26f, top + bodyH * 0.36f))
      drawCircle(cheek, bodyW * 0.068f, center = Offset(left + bodyW * 0.74f, top + bodyH * 0.36f))

      val eyeY = top + bodyH * 0.21f
      val sep = bodyW * 0.21f
      val er = bodyW * 0.068f
      drawCircle(Color.White, er + 2.5f, center = Offset(wx - sep, eyeY))
      drawCircle(Color.White, er + 2.5f, center = Offset(wx + sep, eyeY))
      drawCircle(Color(0xFF1F2228), er * 0.55f, center = Offset(wx - sep + 1.4f, eyeY + 1.5f))
      drawCircle(Color(0xFF1F2228), er * 0.55f, center = Offset(wx + sep + 1.4f, eyeY + 1.5f))

      val beak =
          Path().apply {
            moveTo(wx - bodyW * 0.065f, top + bodyH * 0.43f)
            lineTo(wx + bodyW * 0.065f, top + bodyH * 0.43f)
            lineTo(wx, top + bodyH * 0.54f)
            close()
          }
      drawPath(beak, Color(0xFFFF8F5F))

      val toe = Color(0xFFFF9B6E)
      drawOval(toe, topLeft = Offset(wx - bodyW * 0.32f - 10f, top + bodyH * 0.9f), size = Size(24f, 11f))
      drawOval(toe, topLeft = Offset(wx + bodyW * 0.11f - 10f, top + bodyH * 0.9f), size = Size(24f, 11f))

      drawArc(
          color = Color(0xFF253D5C),
          startAngle = 172f,
          sweepAngle = 68f,
          useCenter = false,
          topLeft = Offset(wx - bodyW * 0.02f + 34f, top + bodyH * 0.3f),
          size = Size(28f, 34f),
          style = Stroke(width = bodyW * 0.09f),
      )
      drawArc(
          color = Color(0xFF253D5C),
          startAngle = -8f,
          sweepAngle = -68f,
          useCenter = false,
          topLeft = Offset(left - bodyW * 0.06f + 6f, top + bodyH * 0.3f),
          size = Size(28f, 34f),
          style = Stroke(width = bodyW * 0.09f),
      )

      drawRoundRect(
          color = Color(0xFF8CC8FA).copy(alpha = 0.45f),
          topLeft = Offset(left - 3f, top - 3f),
          size = Size(bodyW + 6f, bodyH + 6f),
          cornerRadius = CornerRadius(bodyW * 0.48f + 3f, bodyH * 0.52f + 3f),
          style =
              Stroke(
                  width = 2.8f),
      )
    }
  }
}
