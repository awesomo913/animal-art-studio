package com.animalartstudio.kids.ui.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * The "answer key" a kid traces. Everything here is authored once per animal and
 * reused by [DoodleCanvas] for BOTH the animated demo (a coach fingertip travels
 * the shape) and the dotted guide the child then traces over.
 *
 * All coordinates are in a 0..1 unit box. At draw time the box is mapped onto a
 * centered SQUARE inside the canvas, so circles stay round no matter the canvas
 * aspect ratio.
 */

/** One continuous outline the kid traces. Closed shapes repeat the first point. */
data class TraceStroke(val points: List<Offset>)

/**
 * One teachable piece of the animal — the body, one pair of eyes, the beak…
 * Drawn in list order so the picture builds head-to-tail.
 */
data class Feature(
    val id: String,
    val strokes: List<TraceStroke>,
    /** Ink tint for this feature's guide + demo dot. */
    val color: Color,
)

/** Ordered recipe for drawing one animal from scratch. */
data class AnimalBlueprint(
    val animalKey: String,
    val features: List<Feature>,
)

// ---------------------------------------------------------------------------
// 0..1 outline generators. Pure math — no Android/Compose runtime needed.
// ---------------------------------------------------------------------------

private const val TWO_PI = (2.0 * PI).toFloat()
private const val DEG = (PI / 180.0).toFloat()

internal fun circle01(cx: Float, cy: Float, r: Float, n: Int = 40): TraceStroke =
    ellipse01(cx, cy, r, r, n)

internal fun ellipse01(cx: Float, cy: Float, rx: Float, ry: Float, n: Int = 48): TraceStroke {
  val pts = ArrayList<Offset>(n + 1)
  for (i in 0..n) {
    val a = TWO_PI * i / n
    pts.add(Offset(cx + rx * cos(a), cy + ry * sin(a)))
  }
  return TraceStroke(pts)
}

/** Straight-sided polygon (triangles for ears/beak/nose). Closes back to start. */
internal fun poly01(points: List<Offset>, closed: Boolean = true): TraceStroke {
  val pts = if (closed && points.isNotEmpty()) points + points.first() else points
  return TraceStroke(pts)
}

/** Open two-point line (whiskers). Offset is an inline value class, so it can't
 * be a vararg — take the endpoints explicitly. */
internal fun line01(a: Offset, b: Offset): TraceStroke = TraceStroke(listOf(a, b))

/**
 * Elliptical arc. Angles follow Compose's drawArc convention: 0° = 3 o'clock,
 * positive sweep = clockwise on screen (y grows downward). Used for smiles/wings.
 */
internal fun arc01(
    cx: Float,
    cy: Float,
    rx: Float,
    ry: Float,
    startDeg: Float,
    sweepDeg: Float,
    n: Int = 24,
): TraceStroke {
  val pts = ArrayList<Offset>(n + 1)
  val start = startDeg * DEG
  val sweep = sweepDeg * DEG
  for (i in 0..n) {
    val a = start + sweep * i / n
    pts.add(Offset(cx + rx * cos(a), cy + ry * sin(a)))
  }
  return TraceStroke(pts)
}

/** Quadratic bezier (swishy tails). */
internal fun quad01(p0: Offset, ctrl: Offset, p1: Offset, n: Int = 24): TraceStroke {
  val pts = ArrayList<Offset>(n + 1)
  for (i in 0..n) {
    val t = i.toFloat() / n
    val u = 1f - t
    val x = u * u * p0.x + 2f * u * t * ctrl.x + t * t * p1.x
    val y = u * u * p0.y + 2f * u * t * ctrl.y + t * t * p1.y
    pts.add(Offset(x, y))
  }
  return TraceStroke(pts)
}

/** Rounded-rectangle outline (clockwise from the top-left corner). */
internal fun roundRect01(
    cx: Float,
    cy: Float,
    halfW: Float,
    halfH: Float,
    corner: Float,
    per: Int = 8,
): TraceStroke {
  val c = min(corner, min(halfW, halfH))
  val l = cx - halfW
  val r = cx + halfW
  val t = cy - halfH
  val b = cy + halfH
  val pts = ArrayList<Offset>()
  fun cornerArc(ccx: Float, ccy: Float, startDeg: Float) {
    val s = startDeg * DEG
    val sweep = 90f * DEG
    for (i in 0..per) {
      val a = s + sweep * i / per
      pts.add(Offset(ccx + c * cos(a), ccy + c * sin(a)))
    }
  }
  cornerArc(l + c, t + c, 180f) // top-left
  cornerArc(r - c, t + c, 270f) // top-right
  cornerArc(r - c, b - c, 0f) // bottom-right
  cornerArc(l + c, b - c, 90f) // bottom-left
  if (pts.isNotEmpty()) pts.add(pts.first())
  return TraceStroke(pts)
}

/**
 * Walk a flattened point list and return the position at fraction [t] of its
 * total length. Powers the traveling "coach fingertip" in the demo.
 */
internal fun pointAtFraction(points: List<Offset>, t: Float): Offset {
  if (points.isEmpty()) return Offset.Unspecified
  if (points.size == 1) return points[0]
  val clamped = t.coerceIn(0f, 1f)
  var total = 0f
  for (i in 1 until points.size) total += (points[i] - points[i - 1]).getDistance()
  if (total <= 0f) return points[0]
  val target = total * clamped
  var acc = 0f
  for (i in 1 until points.size) {
    val seg = (points[i] - points[i - 1]).getDistance()
    if (acc + seg >= target) {
      val f = if (seg <= 0f) 0f else (target - acc) / seg
      return Offset(
          points[i - 1].x + (points[i].x - points[i - 1].x) * f,
          points[i - 1].y + (points[i].y - points[i - 1].y) * f,
      )
    }
    acc += seg
  }
  return points.last()
}
