package com.animalartstudio.kids

import com.animalartstudio.kids.crash.CrashStore
import com.animalartstudio.kids.data.ParentSettingsRepo
import com.animalartstudio.kids.net.StudioApi
import com.animalartstudio.kids.util.RingLog
import java.util.concurrent.atomic.AtomicReference

/**
 * REVIEW_NOTES C-4 (lite).
 *
 * The old `Graph` exposed the entire `KidsApp` (the Android Application class)
 * as a global mutable singleton. That made every consumer impossible to test
 * without standing up a real Android process.
 *
 * The fix keeps the singleton ergonomic (no Hilt, no Koin) but narrows the
 * surface to an interface so tests can `Graph.attach(FakeAppGraph())` and the
 * production wiring lives in [KidsApp.onCreate].
 */
interface AppGraph {
  val api: StudioApi
  val log: RingLog
  val deviceId: String
  val parentSettings: ParentSettingsRepo
  val crashStore: CrashStore
}

object Graph {
  private val ref = AtomicReference<AppGraph?>()

  fun attach(g: AppGraph) {
    ref.set(g)
  }

  fun get(): AppGraph =
      ref.get() ?: error("AppGraph not attached — call Graph.attach() in Application.onCreate()")

  /** Test/debug hook: swap the live graph mid-process. */
  fun overrideForTest(g: AppGraph?) {
    ref.set(g)
  }
}
