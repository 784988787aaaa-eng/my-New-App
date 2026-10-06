package com.smartledger.core.database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao interface PurchaseDao { @Insert suspend fun insertPurchase(value:PurchaseEntity); @Insert suspend fun insertLines(values:List<PurchaseLineEntity>)
 @Query("SELECT * FROM purchases ORDER BY createdAt DESC") fun observeAll(): kotlinx.coroutines.flow.Flow<List<PurchaseEntity>>
 @Query("SELECT * FROM purchases WHERE id = :id LIMIT 1") suspend fun findPurchase(id: String): PurchaseEntity?
 @Query("SELECT * FROM purchase_lines WHERE purchaseId = :id ORDER BY lineNo") suspend fun purchaseLines(id: String): List<PurchaseLineEntity>
 @Query("SELECT COUNT(*) FROM purchases") fun purchasesCount(): kotlinx.coroutines.flow.Flow<Int>
 @Query("SELECT COALESCE(SUM(totalMinorUnits),0) FROM purchases") fun purchasesTotal(): kotlinx.coroutines.flow.Flow<Long> }
