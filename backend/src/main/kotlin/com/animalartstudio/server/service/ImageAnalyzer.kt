package com.animalartstudio.server.service

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.util.Base64
import javax.imageio.ImageIO
import kotlin.math.min

data class ImageStats(
    val coverage: Double,
    val width: Int,
    val height: Int,
)

object ImageAnalyzer {
  private const val WHITE_CUTOFF = 250

  fun fromBase64Png(b64: String): BufferedImage? {
    val clean =
        b64.trim().let { s ->
          if (s.contains("base64,")) s.substringAfter("base64,").trim() else s
        }
    return try {
      val bytes = Base64.getDecoder().decode(clean)
      ImageIO.read(ByteArrayInputStream(bytes))
    } catch (_: Exception) {
      null
    }
  }

  /**
   * Coverage = non-background ink ratio. We treat very light pixels as "paper" and count darker
   * pixels as drawing. This is intentionally simple for a kid's MVP coach.
   */
  fun stats(image: BufferedImage): ImageStats {
    var ink = 0L
    val w = image.width
    val h = image.height
    val total = w.toLong() * h.toLong()
    for (y in 0 until h) {
      for (x in 0 until w) {
        val rgb = image.getRGB(x, y)
        val r = (rgb shr 16) and 0xFF
        val g = (rgb shr 8) and 0xFF
        val b = rgb and 0xFF
        val brightness = (r + g + b) / 3
        if (brightness < WHITE_CUTOFF) ink++
      }
    }
    val c = if (total == 0L) 0.0 else ink.toDouble() / total.toDouble()
    return ImageStats(
        coverage = min(1.0, c * 1.15), // light boost: kids often draw faintly
        width = w,
        height = h,
    )
  }
}
