package com.animalartstudio.kids.ui.shared

import android.graphics.Bitmap

object DrawScratchpad {
  var lastDrawing: Bitmap? = null
  var animalKey: String? = null
  fun clear() {
    lastDrawing = null
    animalKey = null
  }
}
