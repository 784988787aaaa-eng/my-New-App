package com.smartledger.core.database

import androidx.room.withTransaction
import com.smartledger.core.domain.Sale
import com.smartledger.core.domain.FinancialDirection
import java.util.UUID

class CommerceRepository(private val db: SmartLedgerDatabase) {
    suspend fun editSale(id: String, lines: List<com.smartledger.core.domain.SaleLine>, paid: com.smartledger.core.domain.Money, createdAt: Long) {
        require(lines.isNotEmpty())
        db.withTransaction {
            val existing = db.commerceDao().findSale(id) ?: error("Sale not found")
            require(paid.minorUnits in 0..lines.sumOf { it.unitPrice.minorUnits * it.quantity })
            db.commerceDao().deleteLines(id)
            db.commerceDao().insertSaleLines(lines.mapIndexed { i, line -> SaleLineEntity(id, i, line.productId, line.quantity, line.unitPrice.minorUnits) })
            val total = lines.sumOf { it.unitPrice.minorUnits * it.quantity }
            db.commerceDao().updateSale(id, total, paid.minorUnits)
            db.businessDao().insertAudit(AuditLogEntity(UUID.randomUUID().toString(), "UPDATE", "SALE", id, null, createdAt, "lines=" + lines.size))
        }
    }

    suspend fun returnSale(id: String, createdAt: Long) {
        db.withTransaction {
            val sale = db.commerceDao().findSale(id) ?: error("Sale not found")
            val lines = db.commerceDao().saleLines(id)
            lines.forEach { line ->
                db.productDao().insertMovement(StockMovementEntity(UUID.randomUUID().toString(), line.productId, line.quantity, "RETURN_IN", id, createdAt))
            }
            if (sale.personId != null && sale.totalMinorUnits > sale.paidMinorUnits) {
                db.operationDao().insert(OperationEntity(UUID.randomUUID().toString(), sale.personId, FinancialDirection.RECEIVABLE.name, -(sale.totalMinorUnits - sale.paidMinorUnits), "مرتجع البيع " + id, createdAt))
            }
            db.businessDao().insertAudit(AuditLogEntity(UUID.randomUUID().toString(), "UPDATE", "SALE_RETURN", id, null, createdAt, "returned=true"))
        }
    }

    suspend fun recordSale(sale: Sale, createdAt: Long) {
        require(sale.lines.isNotEmpty())
        require(sale.paid.minorUnits in 0..sale.total().minorUnits)
        db.withTransaction {
            db.commerceDao().insertSale(SaleEntity(sale.id, sale.personId, sale.total().minorUnits, sale.paid.minorUnits, createdAt))
            db.commerceDao().insertSaleLines(sale.lines.mapIndexed { i, line -> SaleLineEntity(sale.id, i, line.productId, line.quantity, line.unitPrice.minorUnits) })
            sale.lines.forEach { line ->
                db.productDao().insertMovement(StockMovementEntity(UUID.randomUUID().toString(), line.productId, line.quantity, "SALE", sale.id, createdAt))
            }
            val customerId = sale.personId
            if (customerId != null && sale.outstanding().minorUnits > 0) {
                db.operationDao().insert(OperationEntity(UUID.randomUUID().toString(), customerId, FinancialDirection.RECEIVABLE.name, sale.outstanding().minorUnits, "مبلغ مستحق من البيع " + sale.id, createdAt))
            }
            db.businessDao().insertAudit(AuditLogEntity(UUID.randomUUID().toString(), "CREATE", "SALE", sale.id, null, createdAt, "lines=" + sale.lines.size))
        }
    }
}
