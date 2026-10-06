package com.smartledger.core.database
import androidx.room.withTransaction
import com.smartledger.core.domain.PurchaseReceipt
import java.util.UUID
class PurchaseRepository(private val db:SmartLedgerDatabase){
 suspend fun recordPurchase(p:PurchaseReceipt,createdAt:Long){
  require(p.lines.isNotEmpty()); require(p.paid.minorUnits in 0..p.total().minorUnits)
  db.withTransaction{
   db.purchaseDao().insertPurchase(PurchaseEntity(p.id,p.supplierId,p.total().minorUnits,p.paid.minorUnits,createdAt))
   db.purchaseDao().insertLines(p.lines.mapIndexed{i,l->PurchaseLineEntity(p.id,i,l.productId,l.quantity,l.unitCost.minorUnits)})
   p.lines.forEach{l->db.productDao().insertMovement(StockMovementEntity(UUID.randomUUID().toString(),l.productId,l.quantity,"PURCHASE",p.id,createdAt))}
  }
 }
}
