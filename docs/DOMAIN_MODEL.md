# Domain Model

## Status
PARTIAL

## Core semantics
- Person: party with account relationship.
- Operation: financial mutation with explicit semantic type.
- Balance: derived from canonical operation ledger.
- Product: stock-managed item with unit semantics.
- StockMovement: immutable movement source for stock quantity.
- Sale/Purchase: aggregate roots with lines and financial/stock effects.

## Financial rule
«لنا» means receivable from the counterparty.
«علينا» means payable to the counterparty.

## Money
Money is represented as integer minor units with explicit scale and decimal conversion.

## Critical invariant
If a mutation affects financial balance and stock, all required writes execute atomically.
