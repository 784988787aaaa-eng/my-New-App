# Data Model

## Status
MISSING

Room schema is intentionally not declared as production-ready yet.

Planned entities:
Users, Roles, Permissions, UserRoles, RolePermissions, Books, Groups, People, Operations, OperationLines, Products, Units, StockMovements, Sales, SaleLines, Purchases, PurchaseLines, Expenses, ExpenseCategories, Employees, MessageTemplates, BusinessIdentity, Settings, RecycleBin, AuditLogs, BackupMetadata.

Rules:
- foreign keys where relationships are real
- indexes for search and time filters
- migrations for every schema change
- transaction boundaries around multi-aggregate mutations
