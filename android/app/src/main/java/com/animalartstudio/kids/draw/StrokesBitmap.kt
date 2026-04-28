package com.animalartstudio.kids.draw

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Base64
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import java.io.ByteArrayOutputStream
import kotlin.math.hypot

@Immutable
data class InkStroke(
    val points: List<Offset>,
    val color: Int,
    val widthPx: Float,
)

object StrokesBitmap {
  private const val OUT = 512
  // Paper cream — matches the coaching canvas background
  private const val PAPER = 0xFFFF_F8E7.toInt()
  private const val MIN_MOVE = 2f

  fun toPngBase64(
      strokes: List<InkStroke>,
      width: Float,
      height: Float,
      paperArgb: Int = PAPER,
  ): String {
    val b = render(strokes, width, height, OUT, OUT, paperArgb)
    val out = ByteArrayOutputStream(OUT * OUT / 2)
    b.compress(Bitmap.CompressFormat.PNG, 100, out)
    val bytes = out.toByteArray()
    return "data:image/png;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
  }

  fun toPreviewBitmap(
      strokes: List<InkStroke>,
      width: Float,
      height: Float,
      w: Int,
      h: Int,
      paperArgb: Int = PAPER,
  ): Bitmap = render(strokes, width, height, w, h, paperArgb)

  private fun render(
      strokes: List<InkStroke>,
      srcW: Float,
      srcH: Float,
      dstW: Int,
      dstH: Int,
      paper: Int,
  ): Bitmap {
    val out = Bitmap.createBitmap(dstW, dstH, Bitmap.Config.ARGB_8888)
    out.eraseColor(paper)
    val c = Canvas(out)
    val scaleX = dstW / srcW
    val scaleY = dstH / srcH
    for (s in strokes) {
      if (s.points.isEmpty()) continue
      val p =
          Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = s.color
            style = Paint.Style.STROKE
            strokeWidth = s.widthPx * ((scaleX + scaleY) / 2f)
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
          }
      val pts = s.points
      for (i in 1 until pts.size) {
        val a = map(pts[i - 1], scaleX, scaleY)
        val b = map(pts[i], scaleX, scaleY)
        c.drawLine(a.x, a.y, b.x, b.y, p)
      }
      if (pts.size == 1) {
        val a = map(pts[0], scaleX, scaleY)
        c.drawCircle(a.x, a.y, p.strokeWidth / 2f, p)
      }
    }
    return out
  }

  private fun map(
      o: Offset,
      scaleX: Float,
      scaleY: Float,
  ) = Offset(x = o.x * scaleX, y = o.y * scaleY)
}

/** Drop tiny wiggles: merge points that are very close. */
fun simplify(points: List<Offset>): List<Offset> {
  if (points.size < 2) return points
  val out = ArrayList<Offset>(points.size)
  out.add(points.first())
  for (i in 1 until points.size) {
    val last = out.last()
    val cur = points[i]
    if (hypot((cur - last).x, (cur - last).y) >= MIN_MOVE) {
      out.add(cur)
    }
  }
  return out
}
