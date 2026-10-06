package com.smartledger.core.database

import androidx.room.withTransaction
import com.smartledger.core.domain.Sale
import java.util.UUID

class CommerceRepository(private val db: SmartLedgerDatabase) {
    suspend fun recordSale(sale: Sale, createdAt: Long) {
        require(sale.lines.isNotEmpty())
        require(sale.paid.minorUnits in 0..sale.total().minorUnits)
        db.withTransaction {
            db.commerceDao().insertSale(SaleEntity(sale.id, sale.personId, sale.total().minorUnits, sale.paid.minorUnits, createdAt))
            db.commerceDao().insertSaleLines(sale.lines.mapIndexed { i, line -> SaleLineEntity(sale.id, i, line.productId, line.quantity, line.unitPrice.minorUnits) })
            sale.lines.forEach { line ->
                db.productDao().insertMovement(StockMovementEntity(UUID.randomUUID().toString(), line.productId, line.quantity, "SALE", sale.id, createdAt))
            }
        }
    }
}
