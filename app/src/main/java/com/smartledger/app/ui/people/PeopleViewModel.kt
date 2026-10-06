package com.smartledger.app.ui.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartledger.app.data.PeopleRepository
import com.smartledger.core.database.PersonEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PeopleViewModel @Inject constructor(private val repository: PeopleRepository) : ViewModel() {
    val people: StateFlow<List<PersonEntity>> = repository.observe().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun addPerson(name: String, phone: String?, note: String?) {
        if (name.isBlank()) return
        viewModelScope.launch { repository.add(name, phone, note) }
    }
}
