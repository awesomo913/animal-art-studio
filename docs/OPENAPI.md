# Animal Art Studio — API specification

Version: `v1` (path-prefixed). Base URL in dev: `http://localhost:8080`. All responses are `application/json; charset=utf-8`.

REVIEW_NOTES item: **C-12** (hand-authored to unblock mobile + future web clients without pulling a runtime OpenAPI generator).

---

## Authentication

None today. Anonymous `deviceId` is the only identifier carried by clients. See REVIEW_NOTES item **C-9** for the planned parental-gate + COPPA path.

## Conventions

- **Error body**: every non-2xx response uses [`ErrorBody`](../backend/src/main/kotlin/com/animalartstudio/server/web/dto/ApiDtos.kt):
  ```json
  { "error": "short_machine_code", "detail": "human friendly text" }
  ```
- **Limits**:
  - `Content-Length` capped at `MAX_SUBMIT_BYTES = 2 MiB` on `POST /v1/sessions/{id}/submit` (see B-5).
  - `imageBase64` field capped at `MAX_IMAGE_BASE64_LENGTH = 2,000,000` chars.
- **CORS**: `CORS_ALLOWED_ORIGINS` env (comma-separated) restricts origins. Empty = permissive (dev only).
- **Idempotency**: `POST /v1/sessions` creates a fresh session every call. Retries are NOT deduplicated — see REVIEW_NOTES.

---

## `GET /healthz`

Liveness + database probe. `200` when both process and DB are healthy, `503` when DB is unreachable.

**Response (200)**
```json
{ "status": "ok", "db": "ok", "time": 1747100000000 }
```

**Response (503)**
```json
{ "status": "degraded", "db": "down", "time": 1747100000000 }
```

---

## `GET /v1/lessons`

List all lessons ordered by `orderIndex` ASC.

**Response (200) — `[LessonSummary]`**
```json
[
  {
    "id": "penguin-happy",
    "title": "Waddles' Splashy Show",
    "subtitle": "You're the co-star — Waddles the penguin is tonight's star!",
    "animalKey": "penguin",
    "orderIndex": 0,
    "estMinutes": 10,
    "version": 2
  }
]
```

---

## `GET /v1/lessons/{id}`

Lesson detail including all steps.

**Path params**: `id` — lesson id (e.g. `penguin-happy`).

**Response (200) — `LessonDetail`**
```json
{
  "id": "penguin-happy",
  "title": "Waddles' Splashy Show",
  "subtitle": "You're the co-star — …",
  "description": "Tonight Waddles is putting on a tiny show…",
  "animalKey": "penguin",
  "estMinutes": 10,
  "version": 2,
  "steps": [
    {
      "index": 0,
      "title": "Snowy tummy",
      "instruction": "Waddles whispers: …",
      "technique": "Wobbly oval first…",
      "minCoverage": 0.05,
      "maxCoverage": 0.80,
      "colorHint": "inky blue or soft charcoal",
      "minStrokes": 1
    }
  ]
}
```

**Errors**: `404 lesson_not_found`.

---

## `POST /v1/sessions`

Create a new drawing session for a lesson.

**Body — `CreateSessionRequest`**
```json
{ "lessonId": "penguin-happy", "deviceId": "abc123" }
```
`deviceId` optional but recommended for crash correlation.

**Response (200) — `SessionResponse`**
```json
{
  "sessionId": "sess_abc...",
  "lessonId": "penguin-happy",
  "nudgeCount": 0,
  "highestStepCompleted": -1,
  "version": 2
}
```

**Errors**: `400 missing_lesson_id`, `400 invalid_json`, `404 lesson_not_found`.

---

## `POST /v1/sessions/{id}/submit`

Submit a drawing for the current expected step. Server validates that `stepIndex == highestStepCompleted + 1` (no skipping ahead, no re-submitting past steps).

**Path params**: `id` — session id from `POST /v1/sessions`.

**Body — `SubmitStepRequest`**
```json
{
  "stepIndex": 0,
  "imageBase64": "data:image/png;base64,iVBORw0KGgo...",
  "strokeCount": 4
}
```
`imageBase64` accepts data-URL or raw base64. `strokeCount` is optional anti-gaming signal — when the step has `minStrokes > 0`, a `strokeCount` below it counts the submission as a nudge (see C-10).

**Response (200) — `FeedbackResponse`**
```json
{
  "message": "That tummy looks snack-ready — thank you!",
  "tone": "celebrate",
  "coverage": 0.234,
  "nudgeCount": 0,
  "practiceAttempts": 1,
  "stepIndex": 0,
  "stepPassed": true,
  "stepComplete": true,
  "lessonComplete": false,
  "nextStepIndex": 1,
  "bringToLifeUnlocked": false,
  "animalKey": "penguin",
  "animationPreset": "penguin",
  "soundPackId": "penguin",
  "magicRequiresMorePractice": false,
  "nudgesRequiredForMagic": 5,
  "technique": "Wobbly oval first…"
}
```

### Field semantics (post 2026-05-17 review)

| field | meaning |
|---|---|
| `tone` | `"coach"` for hints, `"celebrate"` on pass |
| `coverage` | boosted, see [Constants.FAINT_INK_BOOST](../backend/src/main/kotlin/com/animalartstudio/server/Constants.kt) |
| `nudgeCount` | total **failed** attempts this session — drives "coach gave a tip" framing |
| `practiceAttempts` | every submission, pass or fail — drives magic unlock (B-1 fix) |
| `stepPassed` | did THIS submission satisfy the coverage check? |
| `stepComplete` | has session passed this step before (`highestStepCompleted >= stepIndex`)? |
| `lessonComplete` | last step passed in this submission |
| `bringToLifeUnlocked` | `stepPassed && lessonComplete && practiceAttempts >= nudgesRequiredForMagic` |
| `magicRequiresMorePractice` | `lessonComplete && !bringToLifeUnlocked` (rare now that practice counts every try) |

**Errors**:
- `400 invalid_json`
- `400 bad_session_id`
- `400 missing_image`
- `400 invalid_session_or_image_or_step` (covers unknown session, malformed image, wrong step index)
- `413 payload_too_large` (Content-Length or `imageBase64.length` over limit)

---

## `GET /v1/help`

Static help articles (no DB hit). Pinned by `id`.

**Response (200) — `[HelpArticle]`**
```json
[ { "id": "start-drawing", "title": "How a lesson works", "body": "Each step…" } ]
```

---

## `POST /v1/client-logs`

Crash + log ring upload from the Android app. See **B-2** in REVIEW_NOTES — the current handler in `KidsApp.kt` will be rewritten to write to disk first.

**Body — `ClientCrashIngest`**
```json
{
  "deviceId": "abc123",
  "appVersion": "0.1.0",
  "payloadJson": "{}",
  "recentLogLines": ["…"]
}
```

**Response (200) — `ClientCrashAck`**
```json
{ "id": 42, "stored": true }
```

**Errors**: `400 missing_device_or_version`, `400 invalid_json`.

---

## Schema migrations

Boot calls `SchemaUtils.createMissingTablesAndColumns(...)` in [`DatabaseFactory`](../backend/src/main/kotlin/com/animalartstudio/server/db/DatabaseFactory.kt). Exposed compares the live schema to the Kotlin `Table` definitions and adds any missing tables or columns. Idempotent on H2 + Postgres.

Graduate to Flyway / Liquibase when changes need type widening, FK alterations, or data backfills — see REVIEW_NOTES C-1.
