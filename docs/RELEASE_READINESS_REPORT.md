# Smart Ledger — Release Readiness Report

Date: 2026-10-06
HEAD: cbb0eee3e1b5ce4b096e814e912826466ea9ed96

## Decision

STATUS: NEEDS_VERIFICATION

The product is at Release Candidate maturity for engineering review. It is not yet certified for commercial production deployment because the latest HEAD still needs a fresh CI result and the environment has no authorized Android runtime for device/E2E/accessibility/performance/backup-restore verification.

## Critical work completed

- Account statement UI now opens from a person account and shows transaction rows with running balance.
- Money handling now exposes an exact minor-unit constructor and has a regression test.
- Sale/purchase entry preserves stored minor-unit prices.
- Arabic numeric money input is normalized through MoneyParser.
- Sale editing reverses prior stock and outstanding balance before applying the edited invoice.
- Sale returns are audited and protected against repeated returns.
- Inventory price display uses the currency formatter instead of raw internal minor units.
- Release/status and QA evidence documents were refreshed.

## Previously verified evidence

GitHub Actions Run 236 passed:
- Unit tests
- Debug APK build
- Signed Release APK build with ephemeral CI certificate
- Release artifact upload

This evidence predates the latest HEAD and therefore does not certify the latest changes.

## Remaining release gates

1. Fresh GitHub Actions run for current HEAD.
2. User-facing invoice history/editor and return actions.
3. Real authenticated session/role enforcement for permissions.
4. Production report export/PDF/print pipeline.
5. Physical/authorized Android E2E test.
6. Visual, RTL, keyboard/IME, accessibility and performance QA.
7. Full backup → restore → integrity → application recovery cycle on Android.
8. Production Play keystore/signing configuration.

## Commercial release rule

Do not label the build as a final production release until all eight gates are evidenced. The CI test certificate is for validation only and must not be treated as the owner's production signing identity.
