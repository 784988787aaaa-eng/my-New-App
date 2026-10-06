package com.smartledger.app.ui.people

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.smartledger.app.data.PeopleRepository
import com.smartledger.app.data.CurrencyPreferences
import com.smartledger.core.database.DatabaseMigrations
import com.smartledger.core.database.PersonEntity
import com.smartledger.core.database.SmartLedgerDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import com.smartledger.core.database.PersonBalanceRow
import com.smartledger.core.database.OperationRepository
import com.smartledger.core.domain.FinancialDirection
import com.smartledger.core.domain.MoneyParser
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.room.withTransaction
import java.util.UUID

class PeopleViewModel(application: Application) : AndroidViewModel(application) {
    private val database = Room.databaseBuilder(
        application,
        SmartLedgerDatabase::class.java,
        "smart_ledger.db"
    ).addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3).build()
    private val repository = PeopleRepository(database.personDao())
    private val operationRepository = OperationRepository(database)

    val people: StateFlow<List<PersonEntity>> = repository.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val currency = CurrencyPreferences(application).currency

    val balances: StateFlow<List<PersonBalanceRow>> = database.personDao().observeBalances()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deletePerson(personId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                database.withTransaction {
                    val person = database.personDao().findById(personId) ?: return@withTransaction
                    database.personDao().archive(personId)
                    val payload = listOf(person.name, person.phone.orEmpty(), person.note.orEmpty()).joinToString("\u001F")
                    database.businessDao().recycle(RecycleBinEntity(UUID.randomUUID().toString(), "PERSON", personId, payload, System.currentTimeMillis()))
                    database.businessDao().insertAudit(AuditLogEntity(UUID.randomUUID().toString(), "DELETE", "PERSON", personId, null, System.currentTimeMillis(), null))
                }
            }.onSuccess { onDone() }
        }
    }

    fun loadStatement(personId: String, onLoaded: (List<com.smartledger.core.database.DirectionAmount>) -> Unit) {
        viewModelScope.launch { onLoaded(database.operationDao().entries(personId)) }
    }

    fun addPayment(personId: String, direction: FinancialDirection, amount: String, note: String?, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                operationRepository.payment(personId, direction, MoneyParser.parse(amount), note?.ifBlank { null }, System.currentTimeMillis())
            }.onSuccess { onDone() }
        }
    }

    fun addOperation(personId: String, direction: FinancialDirection, amount: String, note: String?, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                operationRepository.add(personId, direction, MoneyParser.parse(amount), note?.ifBlank { null }, System.currentTimeMillis())
            }.onSuccess { onDone() }
        }
    }

    fun addPerson(name: String, phone: String?, note: String?) {
        if (name.isBlank()) return
        viewModelScope.launch { repository.add(name, phone, note) }
    }

    override fun onCleared() {
        database.close()
        super.onCleared()
    }
}
