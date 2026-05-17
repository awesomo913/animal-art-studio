package com.animalartstudio.kids.local

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import kotlin.math.min

/**
 * On-device port of the backend's `ImageAnalyzer`.
 *
 * The shipped self-contained APK has no backend to ship the PNG to, so we
 * decode the base64 right back to a Bitmap and run the same coverage heuristic
 * locally. `Bitmap.getPixels` is one bulk copy — far faster than `getPixel(x,y)`
 * in a loop.
 *
 * Calibration is identical to the server (`backend/.../Constants.kt`) so a step
 * that passed in the LAN setup will still pass here.
 */
object OnDeviceImageAnalyzer {

  const val FAINT_INK_BOOST = 1.15
  const val WHITE_CUTOFF = 250

  data class Stats(val coverage: Double, val width: Int, val height: Int)

  fun decode(base64: String): Bitmap? {
    val clean = base64.trim().let { if (it.contains("base64,")) it.substringAfter("base64,").trim() else it }
    return try {
      val bytes = Base64.decode(clean, Base64.DEFAULT)
      BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) {
      null
    }
  }

  fun stats(bmp: Bitmap): Stats {
    val w = bmp.width
    val h = bmp.height
    if (w == 0 || h == 0) return Stats(0.0, 0, 0)
    val px = IntArray(w * h)
    bmp.getPixels(px, 0, w, 0, 0, w, h)
    var ink = 0L
    for (i in px.indices) {
      val c = px[i]
      val r = (c shr 16) and 0xFF
      val g = (c shr 8) and 0xFF
      val b = c and 0xFF
      if ((r + g + b) / 3 < WHITE_CUTOFF) ink++
    }
    val raw = ink.toDouble() / (w.toLong() * h.toLong()).toDouble()
    return Stats(coverage = min(1.0, raw * FAINT_INK_BOOST), width = w, height = h)
  }
}
