package com.animalartstudio.kids

import android.app.Application
import android.provider.Settings
import com.animalartstudio.kids.data.ClientCrashIngest
import com.animalartstudio.kids.net.StudioApi
import com.animalartstudio.kids.util.RingLog
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.atomic.AtomicReference

class KidsApp : Application() {
  val log = RingLog()
  lateinit var deviceId: String
    private set
  lateinit var api: StudioApi
    private set

  override fun onCreate() {
    super.onCreate()
    Graph.attach(this)
    deviceId = stableId()
    val base = BuildConfig.ANIMAL_ART_STUDIO_URL
    api = StudioApi(base, log)
    val previous = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
      runCatching { persistAndUploadCrash(t, e) }
      previous?.uncaughtException(t, e)
    }
  }

  private fun stableId(): String {
    val a = getSharedPreferences("aas", MODE_PRIVATE)
    var id = a.getString("id", null)
    if (id.isNullOrBlank()) {
      val android = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
      id = android?.takeIf { it.isNotBlank() && it != "9774d56d682e549c" } ?: "dev-${System.nanoTime()}"
    }
    a.edit().putString("id", id).apply()
    return id
  }

  private fun persistAndUploadCrash(
      t: Thread,
      e: Throwable,
  ) {
    val stack = e.stackTraceToString().take(12_000)
    val snap = log.snapshot()
    val pay =
        "thread=${t.name}\ntype=${e::class.java.name}\nmessage=${e.message}\n-----\n$stack"
    runBlocking {
      withTimeoutOrNull(1_500) {
        runCatching {
          api.sendCrash(
              ClientCrashIngest(
                  deviceId = deviceId,
                  appVersion = BuildConfig.VERSION_NAME,
                  payloadJson = "{}",
                  recentLogLines = (listOf(pay) + snap).take(200)))
        }
      }
    }
  }
}

object Graph {
  private val ref = AtomicReference<KidsApp?>()
  fun attach(app: KidsApp) {
    ref.set(app)
  }
  fun get(): KidsApp = ref.get() ?: error("Application not ready")
}
