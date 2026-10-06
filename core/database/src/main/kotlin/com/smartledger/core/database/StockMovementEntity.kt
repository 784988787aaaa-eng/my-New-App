package com.smartledger.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_movements",
    foreignKeys = [ForeignKey(entity = ProductEntity::class, parentColumns = ["id"], childColumns = ["productId"], onDelete = ForeignKey.RESTRICT)],
    indices = [Index("productId"), Index("createdAt"), Index("referenceId")]
)
data class StockMovementEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val quantityBaseUnits: Long,
    val kind: String,
    val referenceId: String,
    val createdAt: Long
)
