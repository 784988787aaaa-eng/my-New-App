# Architecture Decision Records

## ADR-001 — Offline-first local source
Context: core business actions must work without internet.
Decision: local persistence is authoritative for offline business flows.
Trade-off: cloud sync is deferred and must be isolated if introduced.

## ADR-002 — Central semantic tokens
Context: random colors/dimensions create visual drift.
Decision: design values are centralized in SmartLedgerColors and SmartLedgerDimens.
Trade-off: exceptional values require documentation.

## ADR-003 — Navigation shell
Context: too many permanent tabs increase cognitive load.
Decision: five work areas in one Navigation Compose shell.
Trade-off: secondary functions live under More/feature flows.
