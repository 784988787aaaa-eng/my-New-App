package com.smartledger.app.ui.more

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.database.sqlite.SQLiteDatabase
import android.os.Process
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartledger.app.R
import com.smartledger.app.data.CurrencyPreferences
import com.smartledger.core.backup.BackupIntegrity
import com.smartledger.core.backup.BackupNaming
import com.smartledger.core.backup.BackupWriter
import com.smartledger.core.backup.BackupRestore
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
                if (database.exists()) {
                    SQLiteDatabase.openDatabase(database.absolutePath, null, SQLiteDatabase.OPEN_READWRITE).use { it.execSQL("PRAGMA wal_checkpoint(FULL)") }
                }
                val folder = File(context.getExternalFilesDir(null), "backups")
                val now = LocalDateTime.now()
                val output = File(folder, BackupNaming.fileName(now))
                val manifest = "{\"formatVersion\":1,\"createdAt\":\"$now\",\"appVersion\":\"0.2.0\"}"
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
    fun restoreBackup(uri: Uri, resolver: ContentResolver) {
        viewModelScope.launch {
            runCatching {
                val context = getApplication<Application>()
                val database = context.getDatabasePath("smart_ledger.db")
                if (database.exists()) {
                    SQLiteDatabase.openDatabase(database.absolutePath, null, SQLiteDatabase.OPEN_READWRITE).use { it.execSQL("PRAGMA wal_checkpoint(FULL)") }
                    val safety = File(context.cacheDir, BackupNaming.fileName())
                    BackupWriter.write(safety, database, "{\"formatVersion\":1,\"type\":\"pre_restore\"}")
                }
                val selected = File(context.cacheDir, "selected_restore.zip")
                resolver.openInputStream(uri).use { input -> requireNotNull(input) { "تعذر قراءة ملف النسخة" }.copyTo(selected.outputStream()) }
                val extracted = BackupRestore.extractDatabase(selected, database)
                val check = SQLiteDatabase.openDatabase(extracted.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
                val healthy = check.rawQuery("PRAGMA integrity_check", null).use { cursor -> cursor.moveToFirst() && cursor.getString(0) == "ok" }
                check.close()
                require(healthy) { "ملف قاعدة البيانات المستعاد غير سليم" }
                BackupRestore.replaceDatabase(extracted, database)
                selected.delete()
            }.onSuccess {
                message = getApplication<Application>().getString(R.string.restore_success)
                Process.killProcess(Process.myPid())
            }.onFailure {
                message = getApplication<Application>().getString(R.string.restore_failed) + ": " + (it.message ?: "")
            }
        }
    }
    }
}

@Composable
fun MoreScreen(onOpenBusinessManagement: () -> Unit = {}, viewModel: MoreViewModel = viewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val restoreLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let { viewModel.restoreBackup(it, context.contentResolver) } }
    val currency by viewModel.currency.collectAsState(initial = SupportedCurrencies.YER)
    var showCurrency by remember { mutableStateOf(false) }
    var selectedSetting by remember { mutableStateOf<Int?>(null) }
    val settings = listOf(R.string.business_identity, R.string.backup_restore, R.string.security_privacy, R.string.user_permissions, R.string.business_management)

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
                    Button(onClick = { viewModel.createBackup() }) { Text(stringResource(R.string.create_backup)) }
                    OutlinedButton(onClick = { restoreLauncher.launch(arrayOf("application/zip", "application/octet-stream")) }) { Text(stringResource(R.string.restore_backup)) }
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
