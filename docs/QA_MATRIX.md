# QA Matrix

| Area | Status | Evidence |
|---|---|---|
| RTL | PARTIAL | RTL composition root |
| Design System | PARTIAL | theme tokens |
| Dashboard | PARTIAL | live Room totals + actionable quick actions |
| Navigation | PARTIAL | dashboard/accounts/inventory/operations/sale/purchase/reports/settings routes |
| Money | PARTIAL | unit tests |
| Database | PARTIAL | Room v3 + migrations |
| Auth | PARTIAL | Role/permission policy + Keystore |
| Balance | PARTIAL | Money + BalanceCalculator + live Room aggregates |
| Inventory | PARTIAL | Product + stock movement + transaction path |
| Sales/Purchases | PARTIAL | atomic stock + balance posting + live entry screens; sale edit/return domain hardening |
| Backup/Restore | PARTIAL | backup + staged restore implementation; full-cycle device test pending |
| Accessibility | NEEDS_VERIFICATION | device required |
| Performance | NEEDS_VERIFICATION | device required |
| Build | NEEDS_VERIFICATION | Run 236 passed; fresh CI required for HEAD cbb0eee3 |
