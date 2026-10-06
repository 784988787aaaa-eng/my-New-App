package com.smartledger.app.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartledger.app.data.CurrencyPreferences
import com.smartledger.app.data.AppDatabaseProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardState(val receivable: Long, val payable: Long, val peopleCount: Int)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabaseProvider.get(application)

    val state = combine(
        db.operationDao().totalReceivable(),
        db.operationDao().totalPayable(),
        db.personDao().observePeople()
    ) { receivable, payable, people -> DashboardState(receivable, payable, people.size) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardState(0, 0, 0))

    val currency = CurrencyPreferences(application).currency


}
