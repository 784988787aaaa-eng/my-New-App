package com.smartledger.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE archived = 0 ORDER BY name COLLATE NOCASE")
    fun observeProducts(): Flow<List<ProductEntity>>

    @Insert suspend fun insert(product: ProductEntity)

    @Insert suspend fun insertMovement(movement: StockMovementEntity)

    @Query("SELECT COALESCE(SUM(CASE WHEN kind IN ('PURCHASE','RETURN_IN') THEN quantityBaseUnits ELSE -quantityBaseUnits END),0) FROM stock_movements WHERE productId = :productId")
    suspend fun stock(productId: String): Long
}
