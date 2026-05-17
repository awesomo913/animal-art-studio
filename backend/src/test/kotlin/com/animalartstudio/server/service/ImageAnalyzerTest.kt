package com.animalartstudio.server.service

import com.animalartstudio.server.Constants.FAINT_INK_BOOST
import com.animalartstudio.server.Constants.WHITE_CUTOFF
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImageAnalyzerTest {

  private fun solid(color: Color, w: Int = 32, h: Int = 32): BufferedImage =
      BufferedImage(w, h, BufferedImage.TYPE_INT_RGB).apply {
        val g = createGraphics()
        try {
          g.color = color
          g.fillRect(0, 0, w, h)
        } finally {
          g.dispose()
        }
      }

  private fun pngBase64(img: BufferedImage): String {
    val baos = ByteArrayOutputStream()
    ImageIO.write(img, "PNG", baos)
    return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray())
  }

  @Test
  fun `pure white image is paper, coverage is zero`() {
    val stats = ImageAnalyzer.stats(solid(Color.WHITE))
    assertEquals(0.0, stats.coverage)
  }

  @Test
  fun `pure black image is fully inked, coverage caps at 1`() {
    val stats = ImageAnalyzer.stats(solid(Color.BLACK))
    // Raw is 1.0, boosted is 1.15, then clamped to 1.0.
    assertEquals(1.0, stats.coverage)
  }

  @Test
  fun `boost is applied to non-extremes`() {
    // Half-black, half-white horizontal split.
    val img = BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB)
    val g = img.createGraphics()
    g.color = Color.WHITE; g.fillRect(0, 0, 40, 40)
    g.color = Color.BLACK; g.fillRect(0, 0, 40, 20)
    g.dispose()
    val stats = ImageAnalyzer.stats(img)
    // Raw 0.5, boosted = 0.5 * 1.15 = 0.575.
    assertEquals(0.5 * FAINT_INK_BOOST, stats.coverage, absoluteTolerance = 0.001)
  }

  @Test
  fun `WHITE_CUTOFF treats near-white as paper`() {
    // RGB(252,252,252) — brightness 252, above the cutoff (250). Should count as paper.
    val img = solid(Color(252, 252, 252))
    assertTrue(WHITE_CUTOFF == 250)
    assertEquals(0.0, ImageAnalyzer.stats(img).coverage)
  }

  @Test
  fun `fromBase64Png strips data URL prefix`() {
    val img = solid(Color.GRAY, 16, 16)
    val b64 = pngBase64(img)
    val parsed = ImageAnalyzer.fromBase64Png(b64)
    assertNotNull(parsed)
    assertEquals(16, parsed.width)
    assertEquals(16, parsed.height)
  }

  @Test
  fun `fromBase64Png accepts raw base64 without data URL prefix`() {
    val img = solid(Color.GRAY, 8, 8)
    val raw = pngBase64(img).substringAfter("base64,")
    val parsed = ImageAnalyzer.fromBase64Png(raw)
    assertNotNull(parsed)
  }

  @Test
  fun `fromBase64Png returns null on garbage`() {
    assertNull(ImageAnalyzer.fromBase64Png("not actually base64 png data"))
    assertNull(ImageAnalyzer.fromBase64Png(""))
  }

  // kotlin-test's assertEquals(Double, Double) requires absoluteTolerance.
  private fun assertEquals(expected: Double, actual: Double, absoluteTolerance: Double) {
    assertTrue(
        kotlin.math.abs(expected - actual) <= absoluteTolerance,
        "expected $expected ± $absoluteTolerance, got $actual",
    )
  }
}
