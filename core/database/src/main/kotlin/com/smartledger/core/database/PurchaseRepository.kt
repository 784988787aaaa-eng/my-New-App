package com.smartledger.core.database
import androidx.room.withTransaction
import com.smartledger.core.domain.PurchaseReceipt
import com.smartledger.core.domain.FinancialDirection
import java.util.UUID
class PurchaseRepository(private val db:SmartLedgerDatabase){
 suspend fun recordPurchase(p:PurchaseReceipt,createdAt:Long){
  require(p.lines.isNotEmpty()); require(p.paid.minorUnits in 0..p.total().minorUnits)
  db.withTransaction{
   db.purchaseDao().insertPurchase(PurchaseEntity(p.id,p.supplierId,p.total().minorUnits,p.paid.minorUnits,createdAt))
   db.purchaseDao().insertLines(p.lines.mapIndexed{i,l->PurchaseLineEntity(p.id,i,l.productId,l.quantity,l.unitCost.minorUnits)})
   p.lines.forEach{l->db.productDao().insertMovement(StockMovementEntity(UUID.randomUUID().toString(),l.productId,l.quantity,"PURCHASE",p.id,createdAt))}
   val supplierId = p.supplierId
   if(supplierId != null && p.outstanding().minorUnits > 0) db.operationDao().insert(OperationEntity(UUID.randomUUID().toString(),supplierId,FinancialDirection.PAYABLE.name,p.outstanding().minorUnits,"مبلغ مستحق للمورد " + p.id,createdAt))
  }
 }
 suspend fun returnPurchase(id:String,createdAt:Long){
  db.withTransaction{
   val purchase=db.purchaseDao().findPurchase(id) ?: error("Purchase not found")
   require(db.businessDao().countAudit("PURCHASE_RETURN", id, "UPDATE")==0) { "تم إرجاع الفاتورة مسبقاً" }
   db.purchaseDao().purchaseLines(id).forEach{ line->
    db.productDao().insertMovement(StockMovementEntity(UUID.randomUUID().toString(),line.productId,-line.quantity,"RETURN_OUT",id,createdAt))
   }
   val outstanding=purchase.totalMinorUnits-purchase.paidMinorUnits
   if(purchase.supplierId!=null && outstanding>0) db.operationDao().insert(OperationEntity(UUID.randomUUID().toString(),purchase.supplierId,FinancialDirection.PAYABLE.name,-outstanding,"مرتجع شراء "+id,createdAt))
   db.businessDao().insertAudit(AuditLogEntity(UUID.randomUUID().toString(),"UPDATE","PURCHASE_RETURN",id,null,createdAt,"returned=true"))
  }
 }

}
