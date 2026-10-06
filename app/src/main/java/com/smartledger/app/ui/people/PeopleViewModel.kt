package com.smartledger.app.ui.people

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.smartledger.app.data.PeopleRepository
import com.smartledger.core.database.DatabaseMigrations
import com.smartledger.core.database.PersonEntity
import com.smartledger.core.database.SmartLedgerDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PeopleViewModel(application: Application) : AndroidViewModel(application) {
    private val database = Room.databaseBuilder(
        application,
        SmartLedgerDatabase::class.java,
        "smart_ledger.db"
    ).addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3).build()
    private val repository = PeopleRepository(database.personDao())

    val people: StateFlow<List<PersonEntity>> = repository.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addPerson(name: String, phone: String?, note: String?) {
        if (name.isBlank()) return
        viewModelScope.launch { repository.add(name, phone, note) }
    }

    override fun onCleared() {
        database.close()
        super.onCleared()
    }
}
