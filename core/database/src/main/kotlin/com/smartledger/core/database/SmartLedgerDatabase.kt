package com.smartledger.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PersonEntity::class, OperationEntity::class, ProductEntity::class, StockMovementEntity::class, SaleEntity::class, SaleLineEntity::class], version = 2, exportSchema = true)
abstract class SmartLedgerDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun operationDao(): OperationDao
    abstract fun productDao(): ProductDao
    abstract fun commerceDao(): CommerceDao
}
