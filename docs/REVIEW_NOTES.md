# Animal Art Studio — review notes (2026-05-17)

Replaces the deleted `COWORKER_IMPROVEMENTS_CHECKLIST.md`. Captures both the original handoff backlog and findings from a fresh code-level review.

Each item is a candidate GitHub issue. Run `scripts/open-issues.ps1` from the repo root to file them under your account.

---

## Bugs found in code review (not in original checklist)

### B-1 — Magic-unlock denies kids who clear every step on the first try
**Labels:** `backend, bug, product`
**Where:** [`backend/.../CoachingService.kt:103`](backend/src/main/kotlin/com/animalartstudio/server/service/CoachingService.kt#L103)

```kotlin
val bringUnlocked = passed && isLast && nudges >= nudgesForMagic
```

`nudges` only increments on **failure** (line 86). A child who clears every step first try has `nudges = 0` → `bringUnlocked = false` → lands in the `magicRequiresMorePractice` branch instead of celebration. Inverted incentive.

**Pick a fix:** (1) increment `practiceAttempts` on every submit, gate on that; (2) drop gate entirely; (3) bypass gate for first-try clears.

---

### B-2 — `runBlocking` inside uncaught exception handler blocks the crashing thread
**Labels:** `android, bug`
**Where:** [`android/.../KidsApp.kt:25-30, 43-63`](android/app/src/main/java/com/animalartstudio/kids/KidsApp.kt#L25)

```kotlin
Thread.setDefaultUncaughtExceptionHandler { t, e ->
  runCatching { persistAndUploadCrash(t, e) }
  previous?.uncaughtException(t, e)
}
// persistAndUploadCrash:
runBlocking { withTimeoutOrNull(1_500) { runCatching { api.sendCrash(...) } } }
```

Fires on the crashing thread (usually main). Freezes the dying app for up to 1.5 s on a network call, and loses crashes entirely when the network is down.

**Fix:** write crash to `filesDir/crashes/` synchronously; upload + delete on next `onCreate`. Drop `runBlocking` from the handler.

---

### B-3 — Floating-point `coverage * 1.15` boost is a hidden magic number
**Labels:** `backend, product`
**Where:** [`backend/.../ImageAnalyzer.kt:52`](backend/src/main/kotlin/com/animalartstudio/server/service/ImageAnalyzer.kt#L52)

```kotlin
coverage = min(1.0, c * 1.15), // light boost: kids often draw faintly
```

Step `minCoverage` / `maxCoverage` in the DB are calibrated against the **boosted** value, not the raw value, but that's not documented anywhere. If someone later removes the boost or changes it to `* 1.2`, every step's pass/fail bounds drift silently.

**Fix:** name it (`const val FAINT_INK_BOOST = 1.15`), document the calibration, consider moving the boost into per-step config.

---

### B-4 — `stepComplete` field is a duplicate of `stepPassed`
**Labels:** `backend, product` (low severity)
**Where:** [`backend/.../CoachingService.kt:114-115`](backend/src/main/kotlin/com/animalartstudio/server/service/CoachingService.kt#L114)

`stepComplete = passed` and `lessonComplete = passed && isLast`. The names imply different concepts; the values don't. Either drop `stepComplete` from the DTO or make it mean "session has progressed past this step" (i.e. `passed || stepIndex <= highestStepCompleted`).

---

### B-5 — No request size limit on `POST /v1/sessions/{id}/submit`
**Labels:** `backend, security`
**Where:** [`backend/.../Routing.kt:44`](backend/src/main/kotlin/com/animalartstudio/server/web/Routing.kt#L44)

A buggy or hostile client can POST a 50 MB base64 string. Ktor buffers the whole body before our `runCatching` ever runs.

**Fix:** an Nginx/CDN limit if hosted, plus Ktor `install(RequestValidation)` capping `imageBase64.length`.

---

### B-6 — `CORS { anyHost() }` ships to production by default
**Labels:** `backend, security`
**Where:** [`backend/.../Application.kt:80`](backend/src/main/kotlin/com/animalartstudio/server/Application.kt#L80)

Fine for local dev. If you ever expose this API publicly, lock CORS to the Android app origin and your own admin tools.

---

## Triaged from original COWORKER_IMPROVEMENTS_CHECKLIST.md

### C-1 — PostgreSQL + Flyway/Liquibase migrations
**Labels:** `backend, infra, coach-checklist`
Replace H2 file DB + `SchemaUtils.create` ([`DatabaseFactory.kt:25`](backend/src/main/kotlin/com/animalartstudio/server/db/DatabaseFactory.kt#L25)) with a real driver + versioned migrations. Pre-req for multi-instance hosting and for the GDPR/COPPA work.

### C-2 — Healthz should verify DB connectivity
**Labels:** `backend, infra, coach-checklist`
[`Routing.kt:25`](backend/src/main/kotlin/com/animalartstudio/server/web/Routing.kt#L25) currently returns `{status: ok}` regardless of DB state. Add a `SELECT 1`.

### C-3 — Zero unit tests despite `testImplementation` block
**Labels:** `backend, android, infra, coach-checklist`
Both `backend/build.gradle.kts` and `android/app/build.gradle.kts` declare test deps; neither module has a single test file. Start with:
- `ImageAnalyzerTest` — empty image, full black, base64 round-trip.
- `CoachingServiceTest` — session progression, nudge counter, gate logic (covers B-1).
- Android Compose test — `LessonRoute` happy path with a fake `StudioApi`.

### C-4 — Replace `Graph` singleton + `DrawScratchpad` with DI + scoped state
**Labels:** `android, coach-checklist`
[`KidsApp.kt:66`](android/app/src/main/java/com/animalartstudio/kids/KidsApp.kt#L66) (global mutable singleton) and [`DrawScratchpad.kt:5`](android/app/src/main/java/com/animalartstudio/kids/ui/shared/DrawScratchpad.kt) (static `Bitmap`) both block testing and risk leaks/blank-screen on process death.

### C-5 — Gate `usesCleartextTraffic` + enable R8 for release
**Labels:** `android, security, coach-checklist`
[`AndroidManifest.xml:7`](android/app/src/main/AndroidManifest.xml#L7) sets cleartext globally. Split via `network_security_config.xml` debug vs release, flip `isMinifyEnabled = true` in [`android/app/build.gradle.kts:31`](android/app/build.gradle.kts#L31) with ProGuard rules for OkHttp + kotlinx.serialization.

### C-6 — Accessibility audit
**Labels:** `android, product, coach-checklist`
TalkBack labels on icon-only buttons, color contrast on tertiary text, 48dp touch targets in the home grid, reduce-motion support in `CoachBubble` pulse + home `wiggle`.

### C-7 — CI workflow (GitHub Actions)
**Labels:** `infra, coach-checklist`
- `backend`: `gradle test` (after C-3) + `gradle run` smoke-up + curl `/healthz`.
- `android`: `./gradlew lint assembleDebug`.
- Cache `~/.gradle/caches`.

### C-8 — Product UX backlog
**Labels:** `product, coach-checklist`
Per-animal SFX in `res/raw`, Lottie/Rive celebration packs, onboarding overlays, parent settings (session length, mute, offline-only), TTS for pre-readers, take-a-break flow, progress chips framed as "practice stars".

### C-9 — Privacy / legal — COPPA, retention, parental gate
**Labels:** `security, product, coach-checklist`
If targeting under-13 in US, COPPA-compliant parental consent. Retention policy for `client_crash_reports`. Encrypted-at-rest if hosted. Right-to-delete by device ID.

### C-10 — Coaching v2 (richer vision)
**Labels:** `backend, product, coach-checklist`
Pluggable scorers (small CNN, or LLM with strict child-safety prompts) behind a feature flag, with the coverage heuristic as the always-on fallback. Anti-gaming: min stroke count, max coverage cap per step. Resolves B-3 implicitly.

### C-11 — Split god-screen `LessonScreen.kt`
**Labels:** `android, coach-checklist`
[`LessonScreen.kt`](android/app/src/main/java/com/animalartstudio/kids/ui/lesson/LessonScreen.kt) is 290 lines holding state, layout, gradient, hero, ink picker, canvas, coach bubble, and CTA row. Pull `CoachBubble`, `InkPicker`, `LessonHeaderCard` into siblings.

### C-12 — OpenAPI spec for `/v1/*`
**Labels:** `backend, coach-checklist`
Adopt `io.ktor:ktor-server-openapi` or hand-author. Publish for mobile + future web clients.

### C-13 — Crash analytics (Sentry / Crashlytics) alongside `/v1/client-logs`
**Labels:** `android, infra, coach-checklist`
Resolves the privacy review the checklist asked for, surfaces aggregated trends instead of one-by-one DB rows.

---

## Dropped (redundant / covered / scope creep)

- "Rename grader → coach": already done in code.
- "A/B test 'nudges' vs 'try-agains'": product research, not code.
- "Clarify in-app what 'magic unlock' means": handled by HelpCatalog article `magic-unlock`.
- "Add more animals and lessons": ongoing work item, not debt.
- "Idempotency-Key for sessions": premature, no observed retry hazard.
- "Rate limiting per device": premature without traffic; folds into C-1 prod migration.
- "Certificate pinning": premature without host ownership.
- "Open source licenses screen": only required if you ship to Play Store.
- "Resizable foldables / stylus pressure": post-MVP.
- "Real `.ogg` sounds": rolled into C-8.
- "FileProvider + share sheet": rolled into C-8.

---

## How to file these as GitHub Issues

```powershell
cd C:\Users\computer\Desktop\AI\animal-art-studio
.\scripts\open-issues.ps1
```
