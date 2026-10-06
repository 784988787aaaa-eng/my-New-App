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
    var selectedPersonId by remember { mutableStateOf<String?>(null) }\n    var statementPersonId by remember { mutableStateOf<String?>(null) }\n    var statementEntries by remember { mutableStateOf<List<com.smartledger.core.database.DirectionAmount>>(emptyList()) }
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
                Text(stringResource(R.string.accounts), style = MaterialTheme.typography.headlineLarge)
                FilledTonalButton(onClick = { showAdd = true }) {
                    Icon(Icons.Outlined.PersonAdd, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.add_person))
                }
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
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
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        BalancePill(Modifier.weight(1f), stringResource(R.string.total_receivable), MoneyFormatter.formatMinorUnits(balance?.receivable ?: 0, currency), SmartLedgerColors.SuccessContainer, SmartLedgerColors.Success)
                        BalancePill(Modifier.weight(1f), stringResource(R.string.total_payable), MoneyFormatter.formatMinorUnits(balance?.payable ?: 0, currency), SmartLedgerColors.DangerContainer, SmartLedgerColors.Danger)
                    }
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

    selectedPersonId?.let { id ->
        people.firstOrNull { it.id == id }?.let { person ->
            AccountActionsDialog(
                personName = person.name,
                onDismiss = { selectedPersonId = null },
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
    onSave: (FinancialDirection, String, String, Boolean) -> Unit
) {
    var direction by remember { mutableStateOf(FinancialDirection.RECEIVABLE) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(personName) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(SmartLedgerDimens.FormGap)) {
                Text(stringResource(R.string.account_action_hint), color = SmartLedgerColors.TextSecondary)\n                OutlinedButton(onClick = onStatement, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.account_statement)) }\n                OutlinedButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.delete_account)) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(!payment, { payment = false }, label = { Text(stringResource(R.string.register_new_debt)) })
                    FilterChip(payment, { payment = true }, label = { Text(stringResource(R.string.register_payment)) })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(direction == FinancialDirection.RECEIVABLE, { direction = FinancialDirection.RECEIVABLE }, label = { Text(stringResource(R.string.register_receivable)) })
                    FilterChip(direction == FinancialDirection.PAYABLE, { direction = FinancialDirection.PAYABLE }, label = { Text(stringResource(R.string.register_payable)) })
                }
                OutlinedTextField(amount, { amount = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.operation_amount)) }, singleLine = true)
                OutlinedTextField(note, { note = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.operation_note)) }, minLines = 2)
            }
        },
        confirmButton = {
            Button(onClick = { onSave(direction, amount, note, payment) }, enabled = amount.isNotBlank()) { Text(stringResource(R.string.save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}

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
