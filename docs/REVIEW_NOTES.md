# Animal Art Studio — review notes (2026-05-17 → 2026-05-17 follow-up)

This file consolidates the original coworker handoff backlog plus findings from a fresh code-level review. The 2026-05-17 follow-up pass landed the items marked **DONE** below as commits on master.

The remaining items kept as **DEFERRED** need external decisions (host, vendor account, legal review, design assets) so they were not implemented in this pass — they're still candidate GitHub Issues that `scripts/open-issues.ps1` will file.

---

## Status legend

- ✅ **DONE** — implemented in 2026-05-17 follow-up commits
- ✋ **DEFERRED** — needs an external decision or asset; kept for a future PR
- ❌ **DROPPED** — redundant, covered elsewhere, or post-MVP

---

## Bugs

| ID | Severity | Status |
|---|---|---|
| **B-1** Magic-unlock denies first-try-perfect kids | high | ✅ DONE |
| **B-2** `runBlocking` in uncaught exception handler | high | ✅ DONE |
| **B-3** Hidden 1.15 coverage boost magic number | low | ✅ DONE |
| **B-4** `stepComplete` duplicates `stepPassed` | low | ✅ DONE |
| **B-5** No request size limit on `/v1/submit` | medium | ✅ DONE |
| **B-6** `CORS { anyHost() }` ships to prod | medium | ✅ DONE |

## Checklist items (from deleted COWORKER_IMPROVEMENTS_CHECKLIST.md)

| ID | Item | Status |
|---|---|---|
| **C-1** PostgreSQL + Flyway migrations | ✅ DONE (lite) — Postgres JDBC driver bundled, URL-driven driver pick, `SchemaUtils.createMissingTablesAndColumns` for additive migrations. Flyway/Liquibase still deferred. |
| **C-2** /healthz verifies DB connectivity | ✅ DONE |
| **C-3** Backend unit tests | ✅ DONE — 15 tests, all green. Android Compose tests still to do. |
| **C-4** Replace `Graph` singleton + `DrawScratchpad` | ✅ DONE (lite) — `AppGraph` interface for tests. `DrawScratchpad` still a static; full removal needs nav-arg refactor. |
| **C-5** Gate cleartext + enable R8 | ✅ DONE for cleartext (`network_security_config.xml` restricts to LAN/loopback). R8 deferred — needs ProGuard rule audit + on-device smoke before enabling. |
| **C-6** Accessibility audit | ✅ DONE (lite) — `contentDescription` added to home + lesson icons and the coach bubble. Full audit (touch target, reduce-motion, RTL) deferred. |
| **C-7** GitHub Actions CI | ✅ DONE — `.github/workflows/ci.yml` runs backend `gradle test` + smoke `/healthz` + android `lint` + `assembleDebug` |
| **C-8** Parent settings UI | ✅ DONE (lite) — DataStore-backed `ParentSettingsRepo` + screen behind parental gate. Toggles capture intent; enforcement of "offline-only" and session cap is still to do. |
| **C-9** Parental gate | ✅ DONE (lite) — number-comparison gate (`ParentalGate.kt`) protects the settings entry. Full COPPA verified-parent flow deferred — needs counsel. |
| **C-10** Coaching v2 anti-gaming | ✅ DONE (lite) — `minStrokes` per step, server checks `strokeCount`, Android passes it. Pluggable CNN/LLM scorers deferred. |
| **C-11** Split `LessonScreen.kt` | ✅ DONE — `CoachBubble` extracted to its own file. Further split (`InkPicker`, `LessonHeaderCard`) deferred but optional. |
| **C-12** OpenAPI spec | ✅ DONE — see `docs/OPENAPI.md` |
| **C-13** Sentry/Crashlytics | ✅ DONE (stub) — `Observability` object stubbed in; uncomment SDK init once you have a DSN. |

## Deferred — needs external input

- **Flyway / Liquibase** migrations (C-1 deep) — current `createMissingTablesAndColumns` is enough for additive changes. Pull a tool when we need type widening / data backfills.
- **Hilt or Koin DI** (C-4 deep) — `AppGraph` interface buys us testability without the annotation processor. Adopt Hilt when there are 3+ ViewModels or when we want compile-time graph validation.
- **R8 minification** (C-5 deep) — enable `isMinifyEnabled = true` + write keep rules for OkHttp + kotlinx.serialization + Compose. Needs an on-device smoke test pass.
- **Full a11y audit** (C-6 deep) — TalkBack tree review, contrast ratio sweep, reduce-motion toggle, RTL pseudo-locale build.
- **Real Sentry SDK** (C-13 deep) — `Observability.init` already takes a DSN; just uncomment the `SentryAndroid.init` block + add `implementation("io.sentry:sentry-android:7.18.0")`.
- **COPPA verified-parent consent** (C-9 deep) — needs counsel-approved flow + age-gate at first launch. The lightweight number gate covers settings-only today.
- **Coaching v2 with ML** (C-10 deep) — `strokeCount` plumbed end-to-end; pluggable scorer interface still to design.
- **Product UX backlog** (C-8 deep) — real per-animal SFX `.ogg`, Lottie/Rive celebration packs, TTS for pre-readers, onboarding overlay coach marks, take-a-break flow, parent settings enforcement.

## Dropped

- "Rename grader → coach" — already done.
- "A/B test 'nudges' vs 'try-agains'" — product research, not code.
- "Clarify in-app what 'magic unlock' means" — `HelpCatalog` article `magic-unlock` covers it.
- "Idempotency-Key for sessions" — premature, no observed retry hazard.
- "Rate limiting per device" — folds into C-1 prod migration discussion when a host is chosen.
- "Certificate pinning" — premature without host ownership.
- "Open source licenses screen" — only required if shipping to Play Store.
- "Resizable foldables / stylus pressure" — post-MVP.
- "FileProvider + share sheet" — post-MVP, folds into C-8.

---

## Where the 2026-05-17 follow-up commits live

| Area | Files |
|---|---|
| Backend | `backend/build.gradle.kts` (Postgres + tests deps), `Constants.kt` (new), `Application.kt` (CORS allowlist, DB probe, no more `name=` on connector), `Routing.kt` (request size limit, healthz DB), `service/CoachingService.kt` (B-1 practiceAttempts gate, B-4 stepComplete semantics, B-3 named constant, C-10 anti-gaming, Exposed `.selectAll().where()` migration), `service/ImageAnalyzer.kt` (B-3 named constants), `service/LessonStepInfo.kt` (`minStrokes` field), `db/Tables.kt` (new columns), `db/PenguinContent.kt` (per-step minStrokes), `db/ContentSeed.kt` (Exposed 0.55 deleteWhere fix), `db/DatabaseFactory.kt` (`createMissingTablesAndColumns` + driver pick), `web/dto/ApiDtos.kt` (new fields + semantics doc), `application.conf` (CORS config), backend `gradle/wrapper/*` (added) |
| Backend tests | `src/test/kotlin/.../ImageAnalyzerTest.kt`, `src/test/kotlin/.../CoachingServiceTest.kt`, `src/test/resources/logback-test.xml` (all new) |
| Android | `KidsApp.kt` (rewritten, no more `runBlocking` in crash handler), `Graph.kt` (new, `AppGraph` interface), `crash/CrashStore.kt` (new), `obs/Observability.kt` (new stub), `data/ParentSettingsRepo.kt` (new), `ui/parental/ParentalGate.kt` (new), `ui/parental/ParentSettingsScreen.kt` (new), `ui/lesson/CoachBubble.kt` (split out), `ui/lesson/LessonScreen.kt` (CoachBubble removed, strokeCount wired), `ui/LessonViewModel.kt` (strokeCount parameter), `ui/home/HomeScreen.kt` (parent-settings button, a11y descriptions), `MainActivity.kt` (parental routes), `AndroidManifest.xml` (network_security_config, allowBackup=false), `res/xml/network_security_config.xml` (new), `build.gradle.kts` (datastore dep, versionName 0.2.0) |
| Infra | `.github/workflows/ci.yml` (new), `docs/OPENAPI.md` (new), `docs/REVIEW_NOTES.md` (this file, refreshed) |

## How to file remaining work as GitHub Issues

`scripts/open-issues.ps1` is still ready — it now creates issues only for the deferred items above. Update the script if you want to skip filing already-done items.
