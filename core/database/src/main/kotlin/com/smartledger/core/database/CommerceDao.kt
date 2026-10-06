package com.smartledger.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CommerceDao {
    @Insert suspend fun insertSale(sale: SaleEntity)
    @Insert suspend fun insertSaleLines(lines: List<SaleLineEntity>)
    @Query("SELECT * FROM sales ORDER BY createdAt DESC") suspend fun sales(): List<SaleEntity>
    @Query("SELECT * FROM sales WHERE id = :id LIMIT 1") suspend fun findSale(id: String): SaleEntity?
    @Query("DELETE FROM sale_lines WHERE saleId = :saleId") suspend fun deleteLines(saleId: String)
    @Query("DELETE FROM sales WHERE id = :id") suspend fun deleteSale(id: String)
    @Query("UPDATE sales SET totalMinorUnits = :total, paidMinorUnits = :paid WHERE id = :id") suspend fun updateSale(id: String, total: Long, paid: Long)
    @Query("SELECT * FROM sale_lines WHERE saleId = :saleId ORDER BY lineNo") suspend fun saleLines(saleId: String): List<SaleLineEntity>
    @Query("SELECT COUNT(*) FROM sales") fun salesCount(): kotlinx.coroutines.flow.Flow<Int>
    @Query("SELECT COALESCE(SUM(totalMinorUnits),0) FROM sales") fun salesTotal(): kotlinx.coroutines.flow.Flow<Long>
}

@androidx.room.Entity(tableName = "sales")
data class SaleEntity(
    @androidx.room.PrimaryKey val id: String,
    val personId: String?,
    val totalMinorUnits: Long,
    val paidMinorUnits: Long,
    val createdAt: Long
)

@androidx.room.Entity(tableName = "sale_lines", primaryKeys = ["saleId","lineNo"])
data class SaleLineEntity(
    val saleId: String,
    val lineNo: Int,
    val productId: String,
    val quantity: Long,
    val unitPriceMinorUnits: Long
)
