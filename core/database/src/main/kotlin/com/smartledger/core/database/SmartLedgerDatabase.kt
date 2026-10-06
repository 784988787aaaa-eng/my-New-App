package com.smartledger.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PersonEntity::class, OperationEntity::class], version = 1, exportSchema = true)
abstract class SmartLedgerDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun operationDao(): OperationDao
}
