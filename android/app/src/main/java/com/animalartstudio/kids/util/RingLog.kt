package com.animalartstudio.kids.util

import java.util.ArrayDeque
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class RingLog(private val cap: Int = 220) {
  private val lines = ArrayDeque<String>(cap)
  private val lock = ReentrantLock()

  fun append(s: String) = lock.withLock {
    val t = time()
    if (lines.size >= cap) lines.removeFirst()
    lines.addLast("$t  $s")
  }

  fun snapshot(): List<String> = lock.withLock { ArrayList(lines) }

  private fun time(): String = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
}
