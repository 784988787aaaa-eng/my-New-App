package com.smartledger.app.ui.operations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.smartledger.core.database.DatabaseMigrations
import com.smartledger.core.database.OperationRepository
import com.smartledger.core.database.PersonEntity
import com.smartledger.core.database.SmartLedgerDatabase
import com.smartledger.core.domain.FinancialDirection
import com.smartledger.core.domain.MoneyParser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OperationViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, SmartLedgerDatabase::class.java, "smart_ledger.db")
        .addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3, DatabaseMigrations.MIGRATION_3_4).build()
    private val repository = OperationRepository(db)
    val people: StateFlow<List<PersonEntity>> = db.personDao().observePeople()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(personId: String, direction: FinancialDirection, amount: String, note: String, onDone: () -> Unit) {
        viewModelScope.launch {
            runCatching { repository.add(personId, direction, MoneyParser.parse(amount), note.ifBlank { null }, System.currentTimeMillis()) }
                .onSuccess { onDone() }
        }
    }

    override fun onCleared() { db.close(); super.onCleared() }
}
