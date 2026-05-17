package com.animalartstudio.kids.crash

import android.content.Context
import com.animalartstudio.kids.BuildConfig
import com.animalartstudio.kids.data.ClientCrashIngest
import com.animalartstudio.kids.net.StudioApi
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * REVIEW_NOTES B-2 fix.
 *
 * Old behavior: `Thread.setDefaultUncaughtExceptionHandler` made a `runBlocking`
 * network call on the dying (often main) thread, freezing the app up to 1.5s
 * AND losing the crash if the network was slow.
 *
 * New behavior: handler writes a JSON file to `filesDir/crashes/` synchronously
 * (under 100ms) then delegates to the previous handler. On next app launch
 * [drainPending] uploads + deletes any queued files.
 *
 * Cap at 20 files / 256 KB each so a misbehaving runtime can't fill the disk.
 */
class CrashStore(private val ctx: Context) {

  private val json = Json { prettyPrint = false; encodeDefaults = true; ignoreUnknownKeys = true }
  private val dir: File by lazy { File(ctx.filesDir, "crashes").also { it.mkdirs() } }
  private val fmt = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US)

  /** Synchronous, fast, allocation-bounded. Safe to call from a uncaught-exception handler. */
  fun write(deviceId: String, recentLog: List<String>, t: Thread, e: Throwable) {
    if (countPending() >= MAX_FILES) return // drop silently — we're already over the cap.
    val ingest = ClientCrashIngest(
        deviceId = deviceId,
        appVersion = BuildConfig.VERSION_NAME,
        payloadJson = """{"thread":"${t.name.escape()}","type":"${e::class.java.name}"}""",
        recentLogLines = (listOf(crashSummary(t, e)) + recentLog).take(200),
    )
    val body = json.encodeToString(ingest)
    if (body.length > MAX_BYTES) return // oversized — drop, no point burning disk on one event.
    val name = "crash_${fmt.format(Date())}_${counter++}.json"
    runCatching {
      File(dir, name).writeText(body)
    }
  }

  fun countPending(): Int = dir.listFiles()?.size ?: 0

  /**
   * Best-effort upload pass for any queued crashes. Call from `Application.onCreate`
   * after the API is wired up. Each file is uploaded then deleted on 2xx; left in
   * place on failure so the next launch retries.
   */
  suspend fun drainPending(api: StudioApi) {
    val files = dir.listFiles()?.toList() ?: return
    for (f in files.sortedBy { it.name }) {
      val text = runCatching { f.readText() }.getOrNull() ?: continue
      val ingest = runCatching { json.decodeFromString<ClientCrashIngest>(text) }.getOrNull()
      if (ingest == null) {
        f.delete() // corrupt file — don't keep retrying it.
        continue
      }
      val ok = runCatching { api.sendCrash(ingest) }.isSuccess
      if (ok) f.delete()
    }
  }

  private fun crashSummary(t: Thread, e: Throwable): String =
      buildString {
        append("thread=").append(t.name).append('\n')
        append("type=").append(e::class.java.name).append('\n')
        append("message=").append(e.message).append('\n')
        append("-----\n")
        append(e.stackTraceToString().take(12_000))
      }

  private fun String.escape() = replace("\\", "\\\\").replace("\"", "\\\"")

  companion object {
    private const val MAX_FILES = 20
    private const val MAX_BYTES = 256 * 1024
    private var counter = 0
  }
}
