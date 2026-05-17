<#
.SYNOPSIS
  Files the 19 issues drafted in docs/REVIEW_NOTES.md to this repo via the gh CLI.

.DESCRIPTION
  Run from the repo root:
    .\scripts\open-issues.ps1

  Prerequisites:
    * gh CLI authenticated (`gh auth status`)
    * The labels backend / android / product / security / infra / coach-checklist / bug
      already exist (they do; created during the 2026-05-17 review pass).

  Each issue body is short — links into docs/REVIEW_NOTES.md for the full write-up.
  Re-running is safe but will create duplicates; close the dupes if you do.
#>

$ErrorActionPreference = "Stop"

if (-not (Test-Path "docs/REVIEW_NOTES.md")) {
  Write-Error "Run me from the repo root (where docs/REVIEW_NOTES.md lives)."
  exit 1
}

$issues = @(
  @{ title = "Bug: magic-unlock denies kids who clear every step on the first try"
     labels = "backend,bug,product"
     anchor = "b-1--magic-unlock-denies-kids-who-clear-every-step-on-the-first-try" },
  @{ title = "Bug: runBlocking in uncaught exception handler blocks crashing thread"
     labels = "android,bug"
     anchor = "b-2--runblocking-inside-uncaught-exception-handler-blocks-the-crashing-thread" },
  @{ title = "Backend: ImageAnalyzer coverage*1.15 boost is an undocumented magic number"
     labels = "backend,product"
     anchor = "b-3--floating-point-coverage--115-boost-is-a-hidden-magic-number" },
  @{ title = "Backend: stepComplete duplicates stepPassed in FeedbackResponse"
     labels = "backend,product"
     anchor = "b-4--stepcomplete-field-is-a-duplicate-of-steppassed" },
  @{ title = "Backend: No request size limit on POST /v1/sessions/{id}/submit"
     labels = "backend,security"
     anchor = "b-5--no-request-size-limit-on-post-v1sessionsidsubmit" },
  @{ title = "Backend: CORS anyHost() ships to production by default"
     labels = "backend,security"
     anchor = "b-6--cors--anyhost--ships-to-production-by-default" },
  @{ title = "Backend: PostgreSQL + Flyway/Liquibase migrations (replace H2 + SchemaUtils.create)"
     labels = "backend,infra,coach-checklist"
     anchor = "c-1--postgresql--flywayliquibase-migrations" },
  @{ title = "Backend: /healthz should verify DB connectivity"
     labels = "backend,infra,coach-checklist"
     anchor = "c-2--healthz-should-verify-db-connectivity" },
  @{ title = "Tests: zero unit tests in either module despite testImplementation block"
     labels = "backend,android,infra,coach-checklist"
     anchor = "c-3--zero-unit-tests-despite-testimplementation-block" },
  @{ title = "Android: replace Graph singleton + DrawScratchpad static with DI + scoped state"
     labels = "android,coach-checklist"
     anchor = "c-4--replace-graph-singleton--drawscratchpad-with-di--scoped-state" },
  @{ title = "Android: gate cleartext to debug builds + enable R8 minification for release"
     labels = "android,security,coach-checklist"
     anchor = "c-5--gate-usescleartexttraffic--enable-r8-for-release" },
  @{ title = "Android: accessibility audit (TalkBack, contrast, touch targets, reduce-motion)"
     labels = "android,product,coach-checklist"
     anchor = "c-6--accessibility-audit" },
  @{ title = "Infra: GitHub Actions CI for backend tests + Android lint/assembleDebug"
     labels = "infra,coach-checklist"
     anchor = "c-7--ci-workflow-github-actions" },
  @{ title = "Product: UX backlog — SFX, Lottie celebrations, onboarding, parent settings, TTS"
     labels = "product,coach-checklist"
     anchor = "c-8--product-ux-backlog" },
  @{ title = "Privacy/legal: COPPA, data retention, parental gate, right-to-delete"
     labels = "security,product,coach-checklist"
     anchor = "c-9--privacy--legal--coppa-retention-parental-gate" },
  @{ title = "Backend: Coaching v2 — pluggable scorers (CNN/LLM) with coverage fallback"
     labels = "backend,product,coach-checklist"
     anchor = "c-10--coaching-v2-richer-vision" },
  @{ title = "Android: split god-screen LessonScreen.kt (290 lines)"
     labels = "android,coach-checklist"
     anchor = "c-11--split-god-screen-lessonscreenkt" },
  @{ title = "Backend: publish OpenAPI spec for /v1/* routes"
     labels = "backend,coach-checklist"
     anchor = "c-12--openapi-spec-for-v1" },
  @{ title = "Android: crash analytics (Sentry/Crashlytics) alongside /v1/client-logs"
     labels = "android,infra,coach-checklist"
     anchor = "c-13--crash-analytics-sentry--crashlytics-alongside-v1client-logs" }
)

$created = 0
$failed  = 0

foreach ($i in $issues) {
  $body = @"
See [docs/REVIEW_NOTES.md#$($i.anchor)](../blob/master/docs/REVIEW_NOTES.md#$($i.anchor)) for full context, code links, and proposed fix.

Generated 2026-05-17 from a code-level review pass that consolidated the legacy ``COWORKER_IMPROVEMENTS_CHECKLIST.md`` (now deleted).
"@

  Write-Host "Creating: $($i.title)" -ForegroundColor Cyan
  try {
    gh issue create --title $i.title --label $i.labels --body $body | Out-Host
    $created++
  } catch {
    Write-Host "  failed: $_" -ForegroundColor Red
    $failed++
  }
}

Write-Host ""
Write-Host "Done. created=$created failed=$failed" -ForegroundColor Green
