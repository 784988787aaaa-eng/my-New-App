package com.smartledger.app.di

import android.content.Context
import androidx.room.Room
import com.smartledger.core.database.SmartLedgerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton fun database(@ApplicationContext context: Context): SmartLedgerDatabase = Room.databaseBuilder(context, SmartLedgerDatabase::class.java, "smart_ledger.db").addMigrations(com.smartledger.core.database.DatabaseMigrations.MIGRATION_1_2, com.smartledger.core.database.DatabaseMigrations.MIGRATION_2_3)
            .build()
    @Provides fun personDao(db: SmartLedgerDatabase) = db.personDao()
    @Provides fun operationDao(db: SmartLedgerDatabase) = db.operationDao()
}
