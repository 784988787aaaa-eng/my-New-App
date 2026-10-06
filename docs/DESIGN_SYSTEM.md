# Smart Ledger Design System

## Status
PARTIAL — Foundation tokens and first Dashboard surface are implemented; component library and visual evidence remain in progress.

## Visual direction
Premium business software: calm, dense enough for daily work, generous enough to scan quickly.

### Semantic colors
- Background: light neutral
- Surface: white
- Primary: professional blue
- Brand anchor: deep navy
- Success: green = لنا / success
- Danger: red = علينا / danger
- Warning: orange
- Info: blue-gray
- Text: primary / secondary
- Border: neutral

All semantic values live in `SmartLedgerColors`.

### Geometry
Base rhythm: 4dp multiples.
- Screen padding: 20dp
- Section gap: 24dp
- Card padding: 16dp
- Small radius: 12dp
- Card radius: 16dp
- Touch target baseline: 48dp
- Icon baseline: 24dp

All shared values live in `SmartLedgerDimens`.

### Typography
Central hierarchy:
- Display
- H1 / H2 / H3
- Section
- Body / Body Small
- Label
- Numeric

Arabic readability takes priority over decorative typography.

### Components
Planned shared components:
- SmartLedgerTopBar
- SmartLedgerCard
- MoneyMetric
- PrimaryAction
- SecondaryAction
- SmartLedgerTextField
- EmptyState
- ErrorState
- LoadingState
- PermissionState
- OperationRow
- PersonBalanceRow
- ProductStockRow
- StatusBadge

### Rules
1. No screen-level hex colors.
2. No arbitrary dimensions without documented reason.
3. No UI strings in Kotlin.
4. No color-only semantic indication.
5. Touch targets remain at least 48dp.
6. Components stay stateless where practical.
7. Components never access Room or repositories directly.
8. Accessibility semantics are part of component contracts.

## Dashboard baseline
The first screen establishes:
- Header hierarchy
- Receivable / payable semantic cards
- Quick actions
- Daily summary
- Empty state
- Single primary floating action

## Next visual work
- Navigation shell
- First-launch identity
- People list/detail
- Operation form with keyboard/IME
- Inventory
- Sale/purchase flows
- Statements and reports
