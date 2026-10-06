@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.smartledger.app.ui.people

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartledger.app.R
import com.smartledger.app.ui.theme.SmartLedgerColors
import com.smartledger.app.ui.theme.SmartLedgerDimens
import com.smartledger.core.domain.FinancialDirection
import com.smartledger.core.domain.MoneyFormatter
import com.smartledger.core.domain.SupportedCurrencies

@Composable
fun PeopleScreen(viewModel: PeopleViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    var selectedPersonId by remember { mutableStateOf<String?>(null) }
    var statementPersonId by remember { mutableStateOf<String?>(null) }
    var statementEntries by remember { mutableStateOf<List<com.smartledger.core.database.DirectionAmount>>(emptyList()) }
    var searchOpen by remember { mutableStateOf(false) }
    val people by viewModel.people.collectAsState()
    val balances by viewModel.balances.collectAsState()
    val currency by viewModel.currency.collectAsState(initial = SupportedCurrencies.YER)
    val balanceMap = remember(balances) { balances.associateBy { it.personId } }
    val filtered = people.filter { it.name.contains(query, ignoreCase = true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SmartLedgerDimens.Screen),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(stringResource(R.string.accounts), style = MaterialTheme.typography.headlineLarge)
                    IconButton(onClick = { searchOpen = !searchOpen }) { Icon(Icons.Outlined.Search, stringResource(R.string.search_people)) }
                }
                FilledTonalButton(onClick = { showAdd = true }) {
                    Icon(Icons.Outlined.PersonAdd, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.add_person))
                }
            }
        }
        if (searchOpen) item {
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Search, null) },
                trailingIcon = { IconButton(onClick = { query = ""; searchOpen = false }) { Icon(Icons.Outlined.Close, null) } },
                placeholder = { Text(stringResource(R.string.search_people)) }
            )
        }
        if (filtered.isEmpty()) item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface)) {
                Column(Modifier.padding(SmartLedgerDimens.Card)) {
                    Text(stringResource(R.string.no_people), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.no_people_hint), color = SmartLedgerColors.TextSecondary)
                }
            }
        }
        items(filtered, key = { it.id }) { person ->
            val balance = balanceMap[person.id]
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface),
                border = BorderStroke(1.dp, SmartLedgerColors.Border),
                onClick = { selectedPersonId = person.id }
            ) {
                Column(Modifier.padding(SmartLedgerDimens.Card), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(person.name, style = MaterialTheme.typography.titleMedium)
                            person.phone?.let { Text(it, color = SmartLedgerColors.TextSecondary) }
                        }
                        Icon(Icons.Outlined.ChevronLeft, contentDescription = stringResource(R.string.open_account))
                    }
                    val receivable = balance?.receivable ?: 0L
                    val payable = balance?.payable ?: 0L
                    val isReceivable = receivable > 0L
                    val isPayable = payable > 0L
                    val amount = if (isReceivable) receivable else if (isPayable) payable else 0L
                    val title = if (isReceivable) stringResource(R.string.smart_receivable) else if (isPayable) stringResource(R.string.smart_payable) else stringResource(R.string.balance_neutral)
                    val tint = if (isReceivable) SmartLedgerColors.Success else if (isPayable) SmartLedgerColors.Danger else SmartLedgerColors.TextSecondary
                    val container = if (isReceivable) SmartLedgerColors.SuccessContainer else if (isPayable) SmartLedgerColors.DangerContainer else SmartLedgerColors.SurfaceMuted
                    BalancePill(Modifier.fillMaxWidth(), title, MoneyFormatter.formatMinorUnits(amount, currency), container, tint)
                }
            }
        }
    }

    if (showAdd) {
        AddPersonDialog(
            onDismiss = { showAdd = false },
            onSave = { name, phone, note ->
                viewModel.addPerson(name, phone, note)
                showAdd = false
            }
        )
    }

    statementPersonId?.let { id ->
        people.firstOrNull { it.id == id }?.let { person ->
            StatementDialog(person.name, statementEntries, currency) { statementPersonId = null }
        }
    }

    selectedPersonId?.let { id ->
        people.firstOrNull { it.id == id }?.let { person ->
            AccountActionsDialog(
                personName = person.name,
                onDismiss = { selectedPersonId = null },
                onStatement = { viewModel.loadStatement(id) { statementEntries = it }; statementPersonId = id },
                onDelete = { viewModel.deletePerson(id) { selectedPersonId = null } },
                onSave = { direction, amount, note, payment ->
                    if (payment) viewModel.addPayment(id, direction, amount, note) { selectedPersonId = null }
                    else viewModel.addOperation(id, direction, amount, note) { selectedPersonId = null }
                }
            )
        }
    }
}

@Composable
private fun BalancePill(modifier: Modifier, title: String, amount: String, container: androidx.compose.ui.graphics.Color, content: androidx.compose.ui.graphics.Color) {
    Surface(color = container, shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Column(Modifier.padding(10.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = content)
            Text(amount, style = MaterialTheme.typography.titleSmall, color = content)
        }
    }
}

@Composable
private fun AccountActionsDialog(
    personName: String,
    onDismiss: () -> Unit,
    onStatement: () -> Unit,
    onDelete: () -> Unit,
    onSave: (FinancialDirection, String, String, Boolean) -> Unit
) {
    var direction by remember { mutableStateOf(FinancialDirection.RECEIVABLE) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            Modifier.fillMaxWidth().navigationBarsPadding().imePadding().padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SmartLedgerDimens.FormGap)
        ) {
            Text(personName, style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.account_action_hint), color = SmartLedgerColors.TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onStatement, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.account_statement)) }
                OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.delete_account)) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                FilterChip(!payment, { payment = false }, label = { Text(stringResource(R.string.register_new_debt)) })
                FilterChip(payment, { payment = true }, label = { Text(stringResource(R.string.register_payment)) })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                FilterChip(direction == FinancialDirection.RECEIVABLE, { direction = FinancialDirection.RECEIVABLE }, label = { Text(stringResource(R.string.register_receivable)) })
                FilterChip(direction == FinancialDirection.PAYABLE, { direction = FinancialDirection.PAYABLE }, label = { Text(stringResource(R.string.register_payable)) })
            }
            OutlinedTextField(amount, { amount = latinDigits(it) }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.operation_amount)) }, singleLine = true)
            OutlinedTextField(note, { note = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.operation_note)) }, minLines = 2)
            Button(
                onClick = { onSave(direction, amount, note, payment) },
                enabled = amount.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Icon(Icons.Outlined.Save, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.save)) }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.cancel)) }
            Spacer(Modifier.height(8.dp))
        }
    }
}
private fun latinDigits(value: String): String = value
    .replace("٠","0").replace("١","1").replace("٢","2").replace("٣","3").replace("٤","4")
    .replace("٥","5").replace("٦","6").replace("٧","7").replace("٨","8").replace("٩","9")

@Composable
private fun AddPersonDialog(onDismiss: () -> Unit, onSave: (String, String?, String?) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_person)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(SmartLedgerDimens.FormGap)) {
                OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.person_name)) }, singleLine = true)
                OutlinedTextField(phone, { phone = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.person_phone)) }, singleLine = true)
                OutlinedTextField(note, { note = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.person_note)) }, minLines = 2)
            }
        },
        confirmButton = { Button(onClick = { onSave(name, phone, note) }, enabled = name.isNotBlank()) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}



@Composable
private fun StatementDialog(
    personName: String,
    entries: List<com.smartledger.core.database.DirectionAmount>,
    currency: com.smartledger.core.domain.Currency,
    onDismiss: () -> Unit
) {
    var receivable by remember { mutableLongStateOf(0L) }
    var payable by remember { mutableLongStateOf(0L) }
    val rows = entries.map { entry ->
        if (entry.direction == FinancialDirection.RECEIVABLE.name) receivable += entry.amountMinorUnits
        if (entry.direction == FinancialDirection.PAYABLE.name) payable += entry.amountMinorUnits
        Triple(entry.direction, entry.amountMinorUnits, receivable - payable)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("كشف حساب — $personName") },
        text = {
            Column(Modifier.fillMaxWidth().heightIn(max = 480.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (rows.isEmpty()) Text("لا توجد حركات مسجلة لهذا الحساب.", color = SmartLedgerColors.TextSecondary)
                rows.forEach { row ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(10.dp)) {
                            Text(if (row.first == FinancialDirection.RECEIVABLE.name) "لنا" else "علينا", style = MaterialTheme.typography.labelLarge)
                            Text(MoneyFormatter.formatMinorUnits(row.second, currency))
                            Text("الرصيد الجاري: " + MoneyFormatter.formatMinorUnits(row.third, currency), color = SmartLedgerColors.TextSecondary)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) } }
    )
}