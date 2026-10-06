package com.smartledger.app.data

import android.content.Context
import androidx.room.Room
import com.smartledger.core.database.DatabaseMigrations
import com.smartledger.core.database.SmartLedgerDatabase

object AppDatabaseProvider {
    @Volatile private var instance: SmartLedgerDatabase? = null

    fun get(context: Context): SmartLedgerDatabase = instance ?: synchronized(this) {
        instance ?: Room.databaseBuilder(
            context.applicationContext,
            SmartLedgerDatabase::class.java,
            "smart_ledger.db"
        ).addMigrations(
            DatabaseMigrations.MIGRATION_1_2,
            DatabaseMigrations.MIGRATION_2_3,
            DatabaseMigrations.MIGRATION_3_4
        ).fallbackToDestructiveMigrationOnDowngrade().build().also { instance = it }
    }
}
