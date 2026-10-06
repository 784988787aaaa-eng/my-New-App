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
import com.smartledger.core.domain.MoneyFormatter
import com.smartledger.core.domain.SupportedCurrencies

@Composable
fun PeopleScreen(viewModel: PeopleViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    var selectedPersonId by remember { mutableStateOf<String?>(null) }
    val people by viewModel.people.collectAsState()
    val balances by viewModel.balances.collectAsState()
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
                value = query, onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
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
                        BalancePill(stringResource(R.string.total_receivable), MoneyFormatter.formatMinorUnits(balance?.receivable ?: 0, SupportedCurrencies.YER), SmartLedgerColors.SuccessContainer, SmartLedgerColors.Success)
                        BalancePill(stringResource(R.string.total_payable), MoneyFormatter.formatMinorUnits(balance?.payable ?: 0, SupportedCurrencies.YER), SmartLedgerColors.DangerContainer, SmartLedgerColors.Danger)
                    }
                }
            }
        }
    }

    if (showAdd) AddPersonDialog(
        onDismiss = { showAdd = false },
        onSave = { name, phone, note -> viewModel.addPerson(name, phone, note); showAdd = false }
    )

    selectedPersonId?.let { id ->
        val person = people.firstOrNull { it.id == id }
        if (person != null) AccountActionsDialog(
            personName = person.name,
            onDismiss = { selectedPersonId = null },
            onOperation = { direction ->
                selectedPersonId = null
                viewModel.addOperation(id, direction, onSaved = {})
            }
        )
    }
}

@Composable
private fun BalancePill(title: String, amount: String, container: androidx.compose.ui.graphics.Color, content: androidx.compose.ui.graphics.Color) {
    Surface(color = container, shape = MaterialTheme.shapes.medium, modifier = Modifier.weight(1f)) {
        Column(Modifier.padding(10.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = content)
            Text(amount, style = MaterialTheme.typography.titleSmall, color = content)
        }
    }
}

@Composable
private fun AccountActionsDialog(personName: String, onDismiss: () -> Unit, onOperation: (com.smartledger.core.domain.FinancialDirection) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(personName) },
        text = { Text(stringResource(R.string.account_action_hint)) },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onOperation(com.smartledger.core.domain.FinancialDirection.RECEIVABLE) }) { Text(stringResource(R.string.register_receivable)) }
                Button(onClick = { onOperation(com.smartledger.core.domain.FinancialDirection.PAYABLE) }) { Text(stringResource(R.string.register_payable)) }
            }
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
        text = { Column(verticalArrangement = Arrangement.spacedBy(SmartLedgerDimens.FormGap)) {
            OutlinedTextField(name, { name = it }, modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.person_name)) }, singleLine = true)
            OutlinedTextField(phone, { phone = it }, modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.person_phone)) }, singleLine = true)
            OutlinedTextField(note, { note = it }, modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.person_note)) }, minLines = 2)
        }},
        confirmButton = { Button(onClick = { onSave(name, phone, note) }, enabled = name.isNotBlank()) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
