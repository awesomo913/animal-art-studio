package com.animalartstudio.server

/**
 * Shared compile-time constants for the coaching API.
 *
 * Centralized here so a single edit changes behavior everywhere — see
 * [FAINT_INK_BOOST] for why this matters.
 */
object Constants {

  /**
   * Multiplier applied to the raw "non-paper pixel" ratio before clamping to 1.0.
   *
   * Kids draw faintly. Without a small boost, even a clearly-recognizable stroke
   * lands far below the per-step [minCoverage][com.animalartstudio.server.web.dto.LessonStepDto.minCoverage].
   *
   * IMPORTANT: every step's `minCoverage` / `maxCoverage` in the seeded
   * [PenguinContent][com.animalartstudio.server.db.PenguinContent] catalog is
   * **calibrated against the boosted value**, not the raw. If this constant
   * changes, the catalog bounds must change too. See `docs/OPENAPI.md`.
   */
  const val FAINT_INK_BOOST = 1.15

  /** Pixels brighter than this are considered "paper", not ink. 0..255. */
  const val WHITE_CUTOFF = 250

  /**
   * Maximum bytes of the raw HTTP request body we'll accept on /submit.
   * Large enough for a 512x512 PNG base64 (~700 KB max) with slack.
   */
  const val MAX_SUBMIT_BYTES = 2L * 1024 * 1024 // 2 MB

  /** Maximum length of the imageBase64 field after JSON parse. */
  const val MAX_IMAGE_BASE64_LENGTH = 2_000_000

  /** Default minimum stroke count for any step (anti-gaming floor). */
  const val DEFAULT_MIN_STROKES = 0
}
