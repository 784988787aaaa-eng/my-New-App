package com.smartledger.core.domain

data class Product(
    val id: String,
    val name: String,
    val sku: String?,
    val unitId: String,
    val cost: Money,
    val price: Money,
    val minimumStock: Long = 0
)

data class StockMovement(
    val productId: String,
    val quantityInBaseUnits: Long,
    val kind: StockMovementKind,
    val referenceId: String
)

enum class StockMovementKind { PURCHASE, SALE, RETURN_IN, RETURN_OUT, ADJUSTMENT }

object StockCalculator {
    fun quantity(movements: List<StockMovement>): Long =
        movements.sumOf { it.quantityInBaseUnits * if (it.kind in setOf(StockMovementKind.PURCHASE, StockMovementKind.RETURN_IN)) 1 else -1 }
}
