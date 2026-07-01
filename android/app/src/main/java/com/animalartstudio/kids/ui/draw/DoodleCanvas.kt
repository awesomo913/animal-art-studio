package com.animalartstudio.kids.ui.draw

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.animalartstudio.kids.draw.InkStroke
import kotlin.math.min

private fun fromArgb(c: Int): Color =
    Color(
        red = (c ushr 16 and 0xFF) / 255f,
        green = (c ushr 8 and 0xFF) / 255f,
        blue = (c and 0xFF) / 255f,
        alpha = (c ushr 24 and 0xFF) / 255f,
    )

private const val DEMO_MS = 1500

/**
 * The kid's drawing surface. Now teaches: it shows a faint "answer key" under the
 * child's ink so they learn where each feature goes.
 *
 * Per active [featureIndex] it: (1) draws already-finished features faint, (2)
 * plays a one-shot demo where a coach fingertip travels the current feature, then
 * (3) leaves that feature as a dotted line to trace. The child's own strokes
 * ACCUMULATE across features ([clearKey] wipes them) so they finish holding a
 * whole animal they drew.
 */
@Composable
fun DoodleCanvas(
    paper: Color,
    ink: Color,
    strokeWidth: Float,
    demoKey: Int,
    clearKey: Int,
    blueprint: AnimalBlueprint?,
    featureIndex: Int,
    onSize: (Float, Float) -> Unit,
    onStrokesChange: (List<InkStroke>) -> Unit,
) {
  val strokes = remember { mutableStateListOf<InkStroke>() }
  val argb = ink.toArgb()

  // Wipe only on explicit "start over" / lesson change — NOT on step change.
  LaunchedEffect(clearKey) {
    if (strokes.isNotEmpty()) {
      strokes.clear()
      onStrokesChange(strokes.toList())
    }
  }

  // Replay the coach demo whenever a new feature becomes active.
  val demo = remember { Animatable(0f) }
  LaunchedEffect(demoKey) {
    demo.snapTo(0f)
    demo.animateTo(1f, animationSpec = tween(DEMO_MS, easing = FastOutSlowInEasing))
  }

  BoxWithConstraints(
      Modifier.fillMaxSize()
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
                  strokes[i] = last.copy(points = last.points + change.position)
                  onStrokesChange(strokes.toList())
                },
            )
          },
  ) {
    Canvas(Modifier.fillMaxSize()) {
      val side = min(size.width, size.height)
      val inset = side * 0.09f
      val span = side - inset * 2f
      val ox = (size.width - side) / 2f + inset
      val oy = (size.height - side) / 2f + inset
      fun map(p: Offset) = Offset(ox + p.x * span, oy + p.y * span)

      val bp = blueprint
      if (bp != null && bp.features.isNotEmpty()) {
        val idx = featureIndex.coerceIn(0, bp.features.lastIndex)
        // 1) Finished features: faint solid outline (already "learned").
        for (fi in 0 until idx) {
          val f = bp.features[fi]
          for (ts in f.strokes) {
            drawTrace(ts.points.map { map(it) }, f.color.copy(alpha = 0.16f), span * 0.012f, dotted = false)
          }
        }
        // 2) Current feature: dotted line to trace.
        val cur = bp.features[idx]
        for (ts in cur.strokes) {
          drawTrace(ts.points.map { map(it) }, cur.color.copy(alpha = 0.45f), span * 0.02f, dotted = true)
        }
        // 3) Coach fingertip travels the current feature during the demo.
        if (demo.value < 1f) {
          val path = cur.strokes.flatMap { it.points }.map { map(it) }
          val head = pointAtFraction(path, demo.value)
          if (head != Offset.Unspecified) {
            drawCircle(cur.color.copy(alpha = 0.9f), radius = span * 0.026f, center = head)
            drawCircle(Color.White, radius = span * 0.010f, center = head)
          }
        }
      }

      // 4) The child's own ink, on top of the guide.
      for (s in strokes) {
        if (s.points.isEmpty()) continue
        val c = fromArgb(s.color)
        for (i in 1 until s.points.size) {
          drawLine(
              color = c,
              start = s.points[i - 1],
              end = s.points[i],
              strokeWidth = s.widthPx,
              cap = StrokeCap.Round,
          )
        }
        if (s.points.size == 1) {
          drawCircle(color = c, radius = s.widthPx / 2f, center = s.points[0])
        }
      }
    }
  }
}

/** Stroke a flattened outline; [dotted] renders it as round dots to trace. */
private fun DrawScope.drawTrace(
    pts: List<Offset>,
    color: Color,
    width: Float,
    dotted: Boolean,
) {
  if (pts.size < 2) return
  val path = Path().apply {
    moveTo(pts[0].x, pts[0].y)
    for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y)
  }
  val effect = if (dotted) PathEffect.dashPathEffect(floatArrayOf(width * 0.2f, width * 2.4f), 0f) else null
  drawPath(
      path = path,
      color = color,
      style = Stroke(width = width, cap = StrokeCap.Round, pathEffect = effect),
  )
}
