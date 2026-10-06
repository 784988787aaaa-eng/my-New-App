package com.smartledger.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.smartledger.core.domain.Currency
import com.smartledger.core.domain.SupportedCurrencies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.smartLedgerDataStore by preferencesDataStore(name = "smart_ledger_preferences")

class CurrencyPreferences(private val context: Context) {
    private val currencyKey = stringPreferencesKey("base_currency")
    val currency: Flow<Currency> = context.smartLedgerDataStore.data.map {
        SupportedCurrencies.byCode(it[currencyKey] ?: "YER")
    }
    suspend fun setCurrency(code: String) {
        context.smartLedgerDataStore.edit { it[currencyKey] = code }
    }
}