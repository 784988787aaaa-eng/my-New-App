package com.smartledger.core.database

import androidx.room.withTransaction
import com.smartledger.core.domain.FinancialDirection
import com.smartledger.core.domain.Money
import java.util.UUID

class OperationRepository(private val db: SmartLedgerDatabase) {
    suspend fun add(personId: String, direction: FinancialDirection, amount: Money, note: String?, createdAt: Long) {
        require(amount.minorUnits > 0) { "Operation amount must be positive" }
        db.withTransaction {
            val id = UUID.randomUUID().toString()
            db.operationDao().insert(OperationEntity(id, personId, direction.name, amount.minorUnits, note, createdAt))
            db.businessDao().insertAudit(AuditLogEntity(UUID.randomUUID().toString(), "CREATE", "OPERATION", id, null, createdAt, note))
        }
    }
    suspend fun payment(personId: String, direction: FinancialDirection, amount: Money, note: String?, createdAt: Long) {
        require(amount.minorUnits > 0) { "Payment amount must be positive" }
        db.withTransaction {
            val current = db.operationDao().balance(personId, direction.name)
            require(amount.minorUnits <= current) { "Payment exceeds outstanding balance" }
            val id = UUID.randomUUID().toString()
            db.operationDao().insert(OperationEntity(id, personId, direction.name, -amount.minorUnits, note, createdAt))
            db.businessDao().insertAudit(AuditLogEntity(UUID.randomUUID().toString(), "CREATE", "PAYMENT", id, null, createdAt, "direction=" + direction.name))
        }
    }
}
