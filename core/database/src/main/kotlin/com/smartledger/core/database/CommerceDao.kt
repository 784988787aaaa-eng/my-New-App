package com.smartledger.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CommerceDao {
    @Insert suspend fun insertSale(sale: SaleEntity)
    @Insert suspend fun insertSaleLines(lines: List<SaleLineEntity>)
    @Query("SELECT * FROM sales ORDER BY createdAt DESC") suspend fun sales(): List<SaleEntity>
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
