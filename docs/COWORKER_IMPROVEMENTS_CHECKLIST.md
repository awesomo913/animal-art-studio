# Animal Art Studio — improvement backlog and cleanup notice

> **COWORKER: Delete this file from the repository when you have finished the updates you take from it.**  
> This document is a one-time handoff. Keeping it after work is done creates stale planning debt and confuses the next person.  
> If something is still open, move it to GitHub Issues or `README.md` “Roadmap” instead, then **delete this file** in a follow-up commit.

---

## 1. Product and pedagogy

- [ ] Rename any internal “grader” language everywhere to “coach” / “friend” (code comments, API field names in future versions, analytics).
- [ ] A/B test copy: “nudges” vs “try-agains” with parents; ensure no shaming subtext in edge cases.
- [ ] Add a “take a break” or “I’m done for today” flow that always feels positive.
- [ ] Add optional read-aloud (TTS) for step instructions and coach messages for pre-readers.
- [ ] Parent settings: max session length, mute sounds, disable network (offline-only mode).
- [ ] Clarify in-app what “magic unlock” means so expectations match the animation quality you ship.
- [ ] Add more animals and lessons; keep `lessonId` and `version` in API stable for app compatibility.

## 2. Backend (Ktor) — correctness and quality

- [ ] **PostgreSQL (or other production DB)**: H2 is dev-only. Add JDBC driver, connection pool tuning, and migrations (Flyway/Liquibase).
- [ ] **Migrations** instead of `SchemaUtils.create` on boot for production (create vs migrate split).
- [ ] **Idempotency** for `POST /v1/sessions` if clients retry (optional `Idempotency-Key` header).
- [ ] **Rate limiting** per `deviceId` or IP to reduce abuse and cost.
- [ ] **Request size limits** for base64 image payloads; reject with `413` and friendly error.
- [ ] **Input validation** with explicit max dimensions / PNG-only / virus scan if you accept user uploads in cloud storage later.
- [ ] **Structured JSON logging** (request id, session id) instead of ad hoc strings; correlation with Android `deviceId` + build.
- [ ] **Health check** depth: `GET /healthz` can verify DB connectivity and schema version.
- [ ] **OpenAPI** spec for all `/v1/*` routes; publish for mobile and future web clients.
- [ ] **Coaching v2**: optional pluggable scorers (ML vision) with strict fallbacks to rule-based coverage if timeout or error.
- [ ] **Unit tests** for `ImageAnalyzer` and `CoachingService` (golden images: empty, light pencil, overfilled).
- [ ] **Integration tests** for session progression and nudge/ unlock rules.
- [ ] **GDPR / COPPA**: data retention for `client_crash_reports` and PII; encryption at rest; right-to-delete for device id.

## 3. Image analysis (current weakness)

- [ ] The coverage heuristic is **not** true shape or template matching. Consider: normalized histograms, downsampled mask comparison, or a tiny CNN on the server.
- [ ] Unify “paper” color between Android export (`PAPER` constant) and server `WHITE_CUTOFF` so tuning is one place (shared constants or spec doc).
- [ ] Add anti-gaming: minimum stroke count or path length in addition to coverage (if kids flood-fill).
- [ ] Add maximum reasonable coverage to catch accidental full-bucket fill; tune per step in DB.

## 4. Android app — architecture and platform

- [ ] **Dependency injection** (Hilt or Koin) for `StudioApi`, `RingLog`, and repositories.
- [ ] **Repository layer** over raw API; cache lesson list in DataStore; offline read of last fetched lessons.
- [ ] **Navigation**: type-safe routes (e.g. Kotlin Serialization or custom sealed routes).
- [ ] **ViewModel** scope: use `BackStackEntry` scoping; avoid `Graph.get()` from ViewModels in tests; inject interfaces.
- [ ] **Bitmap lifecycle**: `DrawScratchpad` is a static; use `ViewModel` + `SaveStateHandle` or `rememberSaveable` patterns where possible, or `Navigation` argument with file URI in cache.
- [ ] **ProGuard/R8** for release: rules for OkHttp, Kotlin serialization, and Retrofit if you add it; enable minify after testing.
- [ ] **Edge-to-edge** and **WindowInsets** for notches and gesture nav (especially drawing canvas).
- [ ] **Large screen / foldables**: resizable window for tablets; optional stylus pressure if API available.
- [ ] **Accessibility**: TalkBack labels on all icons; minimum 48dp touch targets; color contrast (WCAG) on CTA buttons; reduce motion respects system setting (disable parallax/celebration wobble).
- [ ] **Localization**: `strings.xml` for all user-visible copy; RTL layout verification.
- [ ] **Instrumented tests**: Espresso or Compose test for “start lesson → draw → ask coach” happy path (mock server).

## 5. Android — UX and engagement

- [ ] Real **SFX** in `res/raw` (per `animalKey`); respect mute and focus/AudioManager.
- [ ] **Lottie or Rive** for celebratory “life” per animal instead of only bitmap motion.
- [ ] Onboarding **tutorial** with overlay coach marks, not just a static screen.
- [ ] **Progress** chips: which step, how many nudges used (framed as “practice stars”).
- [ ] **Error states**: if API fails, show retry + offline tip; never dead-end.
- [ ] **Photos**: export/share final drawing (with parent gate) using `FileProvider` and a share sheet.

## 6. Network and security

- [ ] **HTTPS only** in production; remove or gate `usesCleartextTraffic` to debug builds only (`networkSecurityConfig`).
- [ ] **Certificate pinning** (optional) if you control the API host and threat model justifies it.
- [ ] **No secrets in repo**; `local.properties` already gitignored — document CI for Android (GitHub Actions) with **encrypted secrets** or no secrets for public forks.
- [ ] **Auth** for API if you add accounts; otherwise keep anonymous device id and document data minimization.

## 7. Observability and operations

- [ ] **CI**: GitHub Actions — backend `gradle test`, `gradle run` smoke, Android `lint` + `assembleDebug`.
- [ ] **Crash analytics**: Sentry or Firebase Crashlytics in addition to your `/v1/client-logs` (with privacy review).
- [ ] **Uptime** for API if hosted (Render, Fly, Railway, etc.); health checks; alerts.
- [ ] **Cost caps** for any future paid vision API.

## 8. Design and content

- [ ] Unify **typography** (one display font for kids if brand allows) and spacing scale.
- [ ] **Illustration** assets per lesson step (reference art) instead of text-only; keep sizes small.
- [ ] **Voice** review of all copy by someone who works with children.

## 9. Legal and store

- [ ] **Play Store** listing: child-directed compliance, data safety form, and accurate description of data collection.
- [ ] If targeting under-13 in US, **COPPA** compliance and parental consent flows as required by your counsel.
- [ ] **Open source licenses** for dependencies in app UI (Settings → About) if required by licenses.

## 10. Process

- [ ] Turn checked items from this file into **GitHub Issues** with labels (`backend`, `android`, `product`).
- [ ] **COWORKER: After you have implemented, deferred, or ticketed the items you care about, delete this file and push. Do not leave this checklist as a permanent artifact.**

---

*Generated as a one-off planning artifact. Per the notice at the top, remove it once the work is complete.*
