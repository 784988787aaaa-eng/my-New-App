package com.smartledger.core.database
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName="purchases") data class PurchaseEntity(@PrimaryKey val id:String,val supplierId:String?,val totalMinorUnits:Long,val paidMinorUnits:Long,val createdAt:Long)
@Entity(tableName="purchase_lines",primaryKeys=["purchaseId","lineNo"]) data class PurchaseLineEntity(val purchaseId:String,val lineNo:Int,val productId:String,val quantity:Long,val unitCostMinorUnits:Long)
