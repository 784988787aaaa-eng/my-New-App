# Architecture

## Status
PARTIAL

## Target
Layered modular Android architecture:

Presentation
→ ViewModel / UI State / Events
→ Use Case
→ Repository contract
→ Data source
→ Room / Files

## Boundaries
- UI cannot access Room.
- Domain cannot depend on Android UI.
- Repository contracts live with Domain.
- Data implements Domain contracts.
- Components are stateless where practical.
- Financial calculations have one canonical implementation.

## Initial modules
- app
- core:domain

Planned next:
- core:common
- core:database
- core:security
- core:localization
- core:design-system
- core:testing
- feature modules
- backup
