package com.smartledger.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PersonEntity::class, OperationEntity::class, ProductEntity::class, StockMovementEntity::class, SaleEntity::class, SaleLineEntity::class, PurchaseEntity::class, PurchaseLineEntity::class, ExpenseEntity::class, EmployeeEntity::class, AuditLogEntity::class, RecycleBinEntity::class, UserEntity::class], version = 3, exportSchema = true)
abstract class SmartLedgerDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun operationDao(): OperationDao
    abstract fun productDao(): ProductDao
    abstract fun commerceDao(): CommerceDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun businessDao(): BusinessDao
    abstract fun userDao(): UserDao\n    abstract fun businessDao(): BusinessDao
}
