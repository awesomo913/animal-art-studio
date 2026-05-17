package com.animalartstudio.server.service

import com.animalartstudio.server.Constants.FAINT_INK_BOOST
import com.animalartstudio.server.Constants.WHITE_CUTOFF
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
   * Coverage = non-background ink ratio, with a small boost so faint kids' lines
   * still register. See [FAINT_INK_BOOST] for the calibration contract — the
   * seeded step bounds in [com.animalartstudio.server.db.PenguinContent] assume
   * the boosted value.
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
    val rawRatio = if (total == 0L) 0.0 else ink.toDouble() / total.toDouble()
    return ImageStats(
        coverage = min(1.0, rawRatio * FAINT_INK_BOOST),
        width = w,
        height = h,
    )
  }
}
