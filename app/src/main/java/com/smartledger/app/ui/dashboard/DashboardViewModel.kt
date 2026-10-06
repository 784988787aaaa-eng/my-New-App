package com.smartledger.app.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.smartledger.app.data.CurrencyPreferences
import com.smartledger.core.database.DatabaseMigrations
import com.smartledger.core.database.SmartLedgerDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardState(val receivable: Long, val payable: Long, val peopleCount: Int)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, SmartLedgerDatabase::class.java, "smart_ledger.db")
        .addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3).build()
    val state = combine(
        db.operationDao().totalReceivable(),
        db.operationDao().totalPayable(),
        db.personDao().observePeople()
    ) { receivable, payable, people -> DashboardState(receivable, payable, people.size) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardState(0, 0, 0))
    val currency = CurrencyPreferences(application).currency
}
    override fun onCleared() { db.close(); super.onCleared() }
