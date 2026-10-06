package com.smartledger.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "products", indices = [Index("name"), Index("sku")])
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val sku: String?,
    val unitId: String,
    val costMinorUnits: Long,
    val priceMinorUnits: Long,
    val minimumStock: Long,
    val archived: Boolean = false
)
