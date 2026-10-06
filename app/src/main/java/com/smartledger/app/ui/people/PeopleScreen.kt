package com.smartledger.app.ui.people

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.smartledger.app.R
import com.smartledger.app.ui.theme.SmartLedgerColors
import com.smartledger.app.ui.theme.SmartLedgerDimens

@Composable
fun PeopleScreen(viewModel: PeopleViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    val people by viewModel.people.collectAsState()
    val filtered = people.filter { it.name.contains(query, ignoreCase = true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SmartLedgerDimens.Screen),
        verticalArrangement = Arrangement.spacedBy(SmartLedgerDimens.Card)
    ) {
        item { Text(stringResource(R.string.accounts), style = MaterialTheme.typography.headlineLarge) }
        item {
            OutlinedTextField(
                value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                placeholder = { Text(stringResource(R.string.search_people)) },
                trailingIcon = { IconButton(onClick = { showAdd = true }) { Icon(Icons.Outlined.PersonAdd, contentDescription = stringResource(R.string.add_person)) } }
            )
        }
        if (filtered.isEmpty()) item { Text(stringResource(R.string.no_people), color = SmartLedgerColors.TextSecondary) }
        items(filtered, key = { it.id }) { person ->
            Card(colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface), border = BorderStroke(SmartLedgerDimens.Border, SmartLedgerColors.Border)) {
                Column(Modifier.padding(SmartLedgerDimens.Card)) {
                    Text(person.name, style = MaterialTheme.typography.titleMedium)
                    person.phone?.let { Text(it, color = SmartLedgerColors.TextSecondary) }
                    person.note?.let { Text(it, color = SmartLedgerColors.TextSecondary) }
                }
            }
        }
    }
    if (showAdd) AddPersonDialog(
        onDismiss = { showAdd = false },
        onSave = { name, phone, note -> viewModel.addPerson(name, phone, note); showAdd = false }
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
        dismissButton = { Button(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
