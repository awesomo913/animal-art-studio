package com.animalartstudio.kids.ui.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import com.animalartstudio.kids.draw.InkStroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
private fun fromArgb(c: Int): Color =
    Color(
        red = (c ushr 16 and 0xFF) / 255f,
        green = (c ushr 8 and 0xFF) / 255f,
        blue = (c and 0xFF) / 255f,
        alpha = (c ushr 24 and 0xFF) / 255f,
    )

@Composable
fun DoodleCanvas(
    paper: Color,
    ink: Color,
    strokeWidth: Float,
    stepKey: Int,
    onSize: (Float, Float) -> Unit,
    onStrokesChange: (List<InkStroke>) -> Unit,
) {
  val strokes = remember { mutableStateListOf<InkStroke>() }
  val argb = ink.toArgb()
  LaunchedEffect(stepKey) {
    strokes.clear()
    onStrokesChange(strokes.toList())
  }
  BoxWithConstraints(
      Modifier
          .fillMaxSize()
          .clip(RoundedCornerShape(22.dp))
          .background(paper)
          .onSizeChanged { s -> onSize(s.width.toFloat(), s.height.toFloat()) }
          .pointerInput(argb, strokeWidth) {
            detectDragGestures(
                onDragStart = { p ->
                  strokes.add(InkStroke(listOf(p), argb, strokeWidth))
                  onStrokesChange(strokes.toList())
                },
                onDrag = { change, _ ->
                  val i = strokes.lastIndex
                  if (i < 0) return@detectDragGestures
                  change.consume()
                  val last = strokes[i]
                  val nextPts = last.points + (change.position)
                  strokes[i] = last.copy(points = nextPts, color = last.color, widthPx = last.widthPx)
                  onStrokesChange(strokes.toList())
                },
            )
          }
  ) {
    Canvas(Modifier.fillMaxSize()) {
      for (s in strokes) {
        if (s.points.isEmpty()) continue
        val c = fromArgb(s.color)
        for (i in 1 until s.points.size) {
          val a = s.points[i - 1]
          val b = s.points[i]
          drawLine(
              color = c,
              start = a,
              end = b,
              strokeWidth = s.widthPx,
              cap = StrokeCap.Round,
          )
        }
        if (s.points.size == 1) {
          val a = s.points[0]
          drawCircle(
              color = c,
              radius = s.widthPx / 2f,
              center = a,
          )
        }
      }
    }
  }
}
