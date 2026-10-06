package com.smartledger.app.ui.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import com.smartledger.app.R
import com.smartledger.app.data.CurrencyPreferences
import com.smartledger.core.domain.SupportedCurrencies
import com.smartledger.app.ui.theme.SmartLedgerColors
import com.smartledger.app.ui.theme.SmartLedgerDimens
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MoreViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = CurrencyPreferences(application)
    val currency = preferences.currency
    fun setCurrency(code: String) = viewModelScope.launch { preferences.setCurrency(code) }
}

@Composable
fun MoreScreen(viewModel: MoreViewModel = viewModel()) {
    val currency by viewModel.currency.collectAsState(initial = SupportedCurrencies.YER)
    var showCurrency by remember { mutableStateOf(false) }
    val settings = listOf(R.string.business_identity, R.string.backup_restore, R.string.security_privacy, R.string.user_permissions)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SmartLedgerDimens.Screen),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(stringResource(R.string.more), style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.currency_warning), color = SmartLedgerColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface),
                border = BorderStroke(1.dp, SmartLedgerColors.Border),
                onClick = { showCurrency = true }
            ) {
                Row(Modifier.fillMaxWidth().padding(SmartLedgerDimens.Card), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(stringResource(R.string.base_currency), style = MaterialTheme.typography.titleMedium)
                        Text(currency.arabicName + " (" + currency.code + ")", color = SmartLedgerColors.TextSecondary)
                    }
                    Icon(Icons.Outlined.CurrencyExchange, contentDescription = stringResource(R.string.choose_currency))
                }
            }
        }
        items(settings) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface),
                border = BorderStroke(1.dp, SmartLedgerColors.Border),
                onClick = { }
            ) {
                Row(Modifier.fillMaxWidth().padding(SmartLedgerDimens.Card)) {
                    Icon(
                        imageVector = when (item) {
                            R.string.security_privacy -> Icons.Outlined.Security
                            R.string.backup_restore -> Icons.Outlined.Storage
                            else -> Icons.Outlined.Settings
                        },
                        contentDescription = stringResource(item),
                        tint = SmartLedgerColors.Blue600
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(stringResource(item), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.coming_soon), color = SmartLedgerColors.TextSecondary)
                    }
                }
            }
        }
    }
    if (showCurrency) {
        AlertDialog(
            onDismissRequest = { showCurrency = false },
            title = { Text(stringResource(R.string.choose_currency)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SupportedCurrencies.all.forEach { option ->
                        TextButton(
                            onClick = { viewModel.setCurrency(option.code); showCurrency = false },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(option.arabicName + " — " + option.code + " (" + option.symbol + ")") }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showCurrency = false }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}
