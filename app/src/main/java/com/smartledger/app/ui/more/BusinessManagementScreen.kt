package com.smartledger.app.ui.more

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.smartledger.core.database.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class BusinessManagementViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, SmartLedgerDatabase::class.java, "smart_ledger.db")
        .addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3).build()
    val expenses = db.businessDao().observeExpenses().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val employees = db.businessDao().observeEmployees().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val audit = db.businessDao().observeAudit().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val recycle = db.businessDao().observeRecycle().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addExpense(category: String, amount: Long, note: String?) {
        if (category.isBlank() || amount <= 0) return
        viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            db.businessDao().insertExpense(ExpenseEntity(id, category.trim(), amount, note?.ifBlank { null }, System.currentTimeMillis()))
            db.businessDao().insertAudit(AuditLogEntity(UUID.randomUUID().toString(), "CREATE", "EXPENSE", id, null, System.currentTimeMillis(), category))
        }
    }
    fun addEmployee(name: String, phone: String?) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            db.businessDao().insertEmployee(EmployeeEntity(id, name.trim(), phone?.ifBlank { null }, true))
            db.businessDao().insertAudit(AuditLogEntity(UUID.randomUUID().toString(), "CREATE", "EMPLOYEE", id, null, System.currentTimeMillis(), name))
        }
    }
    fun restorePerson(item: RecycleBinEntity) {
        if (item.entityType != "PERSON") return
        viewModelScope.launch {
            db.personDao().restore(item.entityId)
            db.businessDao().purgeRecycle(item.id)
            db.businessDao().insertAudit(AuditLogEntity(UUID.randomUUID().toString(), "RESTORE", "PERSON", item.entityId, null, System.currentTimeMillis(), null))
        }
    }
    override fun onCleared() { db.close(); super.onCleared() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessManagementScreen(viewModel: BusinessManagementViewModel = viewModel()) {
    val expenses by viewModel.expenses.collectAsState()
    val employees by viewModel.employees.collectAsState()
    val audit by viewModel.audit.collectAsState()
    val recycle by viewModel.recycle.collectAsState()
    var tab by remember { mutableStateOf(0) }
    var showExpense by remember { mutableStateOf(false) }
    var showEmployee by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = tab) {
            listOf("النفقات", "الموظفون", "التدقيق", "المحذوفات").forEachIndexed { index, title ->
                Tab(selected = tab == index, onClick = { tab = index }, text = { Text(title) })
            }
        }
        when (tab) {
            0 -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Button(onClick = { showExpense = true }, modifier = Modifier.fillMaxWidth()) { Text("إضافة نفقة") } }
                items(expenses, key = { it.id }) { e -> ListItem(headlineContent = { Text(e.category) }, supportingContent = { Text(e.note.orEmpty()) }, trailingContent = { Text(e.amountMinorUnits.toString()) }) }
            }
            1 -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Button(onClick = { showEmployee = true }, modifier = Modifier.fillMaxWidth()) { Text("إضافة موظف") } }
                items(employees, key = { it.id }) { e -> ListItem(headlineContent = { Text(e.name) }, supportingContent = { Text(e.phone.orEmpty()) }) }
            }
            2 -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(audit, key = { it.id }) { e -> ListItem(headlineContent = { Text(e.action + " · " + e.entityType) }, supportingContent = { Text(e.entityId.orEmpty() + " · " + (e.metadata.orEmpty())) }) }
            }
            else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(recycle, key = { it.id }) { e ->
                    ListItem(headlineContent = { Text(e.entityType) }, supportingContent = { Text(e.entityId) }, trailingContent = {
                        if (e.entityType == "PERSON") TextButton(onClick = { viewModel.restorePerson(e) }) { Text("استعادة") }
                    })
                }
            }
        }
    }
    if (showExpense) {
        var category by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showExpense = false }, title = { Text("إضافة نفقة") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(category, { category = it }, label = { Text("التصنيف") })
                OutlinedTextField(amount, { amount = it.filter(Char::isDigit) }, label = { Text("المبلغ") })
                OutlinedTextField(note, { note = it }, label = { Text("ملاحظة") })
            }
        }, confirmButton = { Button(onClick = { viewModel.addExpense(category, amount.toLongOrNull() ?: 0, note); showExpense = false }, enabled = category.isNotBlank() && (amount.toLongOrNull() ?: 0) > 0) { Text("حفظ") } }, dismissButton = { TextButton(onClick = { showExpense = false }) { Text("إلغاء") } })
    }
    if (showEmployee) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showEmployee = false }, title = { Text("إضافة موظف") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("الاسم") })
                OutlinedTextField(phone, { phone = it }, label = { Text("الهاتف") })
            }
        }, confirmButton = { Button(onClick = { viewModel.addEmployee(name, phone); showEmployee = false }, enabled = name.isNotBlank()) { Text("حفظ") } }, dismissButton = { TextButton(onClick = { showEmployee = false }) { Text("إلغاء") } })
    }
}
