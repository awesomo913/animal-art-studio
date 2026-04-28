package com.animalartstudio.kids.ui.shared

import android.graphics.Bitmap

object DrawScratchpad {
  var lastDrawing: Bitmap? = null
  var animalKey: String? = null
  /** Which show this drawing belonged to — for star-specific celebration copy. */
  var lessonId: String? = null

  fun clear() {
    lastDrawing = null
    animalKey = null
    lessonId = null
  }
}
