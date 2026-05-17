package com.animalartstudio.kids.obs

import android.content.Context
import android.util.Log

/**
 * REVIEW_NOTES C-13 (stub).
 *
 * Placeholder for Sentry / Crashlytics wiring. The actual SDK pull is deferred
 * because it requires a DSN (Sentry) or `google-services.json` (Crashlytics),
 * both of which need an external account.
 *
 * Wiring later:
 *   1. Add `implementation("io.sentry:sentry-android:7.18.0")` to `app/build.gradle.kts`.
 *   2. Provide `sentry.dsn=https://...@sentry.io/...` in `local.properties`.
 *   3. Expose it as a BuildConfig field in the app build config (see commented block below).
 *   4. Uncomment the `SentryAndroid.init(...)` call below.
 *
 * Until then this just routes to Logcat so the call sites are in place.
 */
object Observability {

  fun init(ctx: Context, dsn: String?) {
    if (dsn.isNullOrBlank()) {
      Log.i(TAG, "Observability: no DSN configured, running in local-only mode")
      return
    }
    // io.sentry.android.core.SentryAndroid.init(ctx) { options ->
    //   options.dsn = dsn
    //   options.tracesSampleRate = 0.0      // turn on once we know the cost
    //   options.isEnableAutoSessionTracking = true
    //   options.sampleRate = 1.0
    // }
    Log.i(TAG, "Observability: DSN present; SDK install pending (REVIEW_NOTES C-13)")
  }

  fun reportNonFatal(t: Throwable, breadcrumbs: List<String> = emptyList()) {
    // Sentry.captureException(t) once the SDK is in.
    Log.w(TAG, "non-fatal: ${t.message}", t)
    for (line in breadcrumbs.takeLast(20)) Log.d(TAG, "breadcrumb: $line")
  }

  private const val TAG = "Observability"
}
