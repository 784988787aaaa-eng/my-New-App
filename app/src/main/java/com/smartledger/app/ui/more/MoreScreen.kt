package com.smartledger.app.ui.more

import android.app.Application
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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartledger.app.R
import com.smartledger.app.data.CurrencyPreferences
import com.smartledger.core.backup.BackupIntegrity
import com.smartledger.core.backup.BackupNaming
import com.smartledger.core.backup.BackupWriter
import com.smartledger.core.domain.SupportedCurrencies
import com.smartledger.app.ui.theme.SmartLedgerColors
import com.smartledger.app.ui.theme.SmartLedgerDimens
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime

class MoreViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = CurrencyPreferences(application)
    val currency = preferences.currency
    var backupPath by mutableStateOf<String?>(null)
        private set
    var message by mutableStateOf<String?>(null)
        private set

    fun setCurrency(code: String) = viewModelScope.launch { preferences.setCurrency(code) }

    fun createBackup() {
        viewModelScope.launch {
            runCatching {
                val context = getApplication<Application>()
                val database = context.getDatabasePath("smart_ledger.db")
                val folder = File(context.getExternalFilesDir(null), "backups")
                val now = LocalDateTime.now()
                val output = File(folder, BackupNaming.fileName(now))
                val manifest = "{\"formatVersion\":1,\"createdAt\":\"$now\",\"appVersion\":\"${com.smartledger.app.BuildConfig.VERSION_NAME}\"}"
                BackupWriter.write(output, database, manifest)
                require(BackupIntegrity.validateArchive(output))
                output.absolutePath
            }.onSuccess {
                backupPath = it
                message = getApplication<Application>().getString(R.string.backup_created)
            }.onFailure {
                message = it.message ?: getApplication<Application>().getString(R.string.backup_failed)
            }
        }
    }
}

@Composable
fun MoreScreen(viewModel: MoreViewModel = viewModel()) {
    val currency by viewModel.currency.collectAsState(initial = SupportedCurrencies.YER)
    var showCurrency by remember { mutableStateOf(false) }
    var selectedSetting by remember { mutableStateOf<Int?>(null) }
    val settings = listOf(R.string.business_identity, R.string.backup_restore, R.string.security_privacy, R.string.user_permissions)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SmartLedgerDimens.Screen),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(stringResource(R.string.more), style = MaterialTheme.typography.headlineLarge)
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
                onClick = { selectedSetting = item }
            ) {
                Row(Modifier.fillMaxWidth().padding(SmartLedgerDimens.Card)) {
                    Icon(
                        imageVector = when (item) {
                            R.string.security_privacy -> Icons.Outlined.Security
                            R.string.backup_restore -> Icons.Outlined.Storage
                            R.string.user_permissions -> Icons.Outlined.People
                            else -> Icons.Outlined.Business
                        },
                        contentDescription = stringResource(item),
                        tint = SmartLedgerColors.Blue600
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(stringResource(item), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.tap_to_open), color = SmartLedgerColors.TextSecondary)
                    }
                }
            }
        }
        viewModel.message?.let { msg -> item { Text(msg, color = SmartLedgerColors.Success) } }
        viewModel.backupPath?.let { path ->
            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.SuccessContainer)) {
                    Column(Modifier.padding(SmartLedgerDimens.Card)) {
                        Text(stringResource(R.string.backup_ready), style = MaterialTheme.typography.titleMedium)
                        Text(path, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    if (showCurrency) AlertDialog(
        onDismissRequest = { showCurrency = false },
        title = { Text(stringResource(R.string.choose_currency)) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SupportedCurrencies.all.forEach { option ->
                TextButton(onClick = { viewModel.setCurrency(option.code); showCurrency = false }, modifier = Modifier.fillMaxWidth()) {
                    Text(option.arabicName + " — " + option.code + " (" + option.symbol + ")")
                }
            }
        }},
        confirmButton = { TextButton(onClick = { showCurrency = false }) { Text(stringResource(R.string.cancel)) } }
    )

    selectedSetting?.let { item ->
        AlertDialog(
            onDismissRequest = { selectedSetting = null },
            title = { Text(stringResource(item)) },
            text = {
                if (item == R.string.backup_restore) Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.backup_restore_detail))
                    Button(onClick = { viewModel.createBackup(); selectedSetting = null }) { Text(stringResource(R.string.create_backup)) }
                } else Text(when (item) {
                    R.string.business_identity -> stringResource(R.string.business_identity_detail)
                    R.string.security_privacy -> stringResource(R.string.security_privacy_detail)
                    else -> stringResource(R.string.user_permissions_detail)
                })
            },
            confirmButton = { TextButton(onClick = { selectedSetting = null }) { Text(stringResource(R.string.close)) } }
        )
    }
}
