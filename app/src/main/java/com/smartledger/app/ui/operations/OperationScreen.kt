package com.smartledger.app.ui.operations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartledger.app.R
import com.smartledger.core.domain.FinancialDirection
import com.smartledger.app.ui.theme.SmartLedgerDimens
import com.smartledger.app.ui.theme.SmartLedgerColors

@Composable
fun OperationScreen(onSaved: () -> Unit, viewModel: OperationViewModel = viewModel()) {
    val people by viewModel.people.collectAsState()
    var selectedId by remember { mutableStateOf<String?>(null) }
    var direction by remember { mutableStateOf(FinancialDirection.RECEIVABLE) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(SmartLedgerDimens.Screen),
        verticalArrangement = Arrangement.spacedBy(SmartLedgerDimens.FormGap)
    ) {
        item {
            Text(stringResource(R.string.new_operation), style = MaterialTheme.typography.headlineLarge)
            Text(stringResource(R.string.operation_hint), color = SmartLedgerColors.TextSecondary)
        }
        item {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = people.firstOrNull { it.id == selectedId }?.name.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.select_account)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    people.forEach { person ->
                        DropdownMenuItem(text = { Text(person.name) }, onClick = { selectedId = person.id; expanded = false })
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(SmartLedgerDimens.FormGap)) {
                FilterChip(selected = direction == FinancialDirection.RECEIVABLE, onClick = { direction = FinancialDirection.RECEIVABLE }, label = { Text(stringResource(R.string.total_receivable)) })
                FilterChip(selected = direction == FinancialDirection.PAYABLE, onClick = { direction = FinancialDirection.PAYABLE }, label = { Text(stringResource(R.string.total_payable)) })
            }
        }
        item { OutlinedTextField(amount, { amount = it }, modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.operation_amount)) }, singleLine = true) }
        item { OutlinedTextField(note, { note = it }, modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.operation_note)) }, minLines = 2) }
        item {
            Button(
                onClick = { selectedId?.let { viewModel.add(it, direction, amount, note, onSaved) } },
                enabled = selectedId != null && amount.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null); Spacer(Modifier.width(SmartLedgerDimens.FormGap)); Text(stringResource(R.string.save)) }
        }
    }
}
