package com.animalartstudio.kids

import android.app.Application
import android.provider.Settings
import com.animalartstudio.kids.crash.CrashStore
import com.animalartstudio.kids.data.ParentSettingsRepo
import com.animalartstudio.kids.net.StudioApi
import com.animalartstudio.kids.obs.Observability
import com.animalartstudio.kids.util.RingLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class KidsApp : Application() {

  private lateinit var graph: DefaultAppGraph

  override fun onCreate() {
    super.onCreate()
    val log = RingLog()
    val api = StudioApi(BuildConfig.ANIMAL_ART_STUDIO_URL, log)
    val crashStore = CrashStore(this)
    val deviceId = stableId()
    val parentSettings = ParentSettingsRepo(this)
    graph = DefaultAppGraph(
        api = api,
        log = log,
        deviceId = deviceId,
        parentSettings = parentSettings,
        crashStore = crashStore,
    )
    Graph.attach(graph)

    // C-13 stub.
    Observability.init(this, dsn = null /* read from BuildConfig once SDK is wired */)

    // B-2: write to disk fast; upload on next launch.
    val previous = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
      runCatching { crashStore.write(deviceId, log.snapshot(), t, e) }
      previous?.uncaughtException(t, e)
    }

    // Background-drain any crashes from a prior session — fire-and-forget.
    backgroundScope.launch { runCatching { crashStore.drainPending(api) } }
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

  companion object {
    private val backgroundScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  }
}

/** Production [AppGraph] — built once in [KidsApp.onCreate]. */
class DefaultAppGraph(
    override val api: StudioApi,
    override val log: RingLog,
    override val deviceId: String,
    override val parentSettings: ParentSettingsRepo,
    override val crashStore: CrashStore,
) : AppGraph
