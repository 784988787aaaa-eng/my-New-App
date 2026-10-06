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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smartledger.app.R
import com.smartledger.app.ui.theme.SmartLedgerColors
import com.smartledger.app.ui.theme.SmartLedgerDimens

@Composable
fun PeopleScreen() {
    var query by remember { mutableStateOf("") }
    val sample = listOf(
        stringResource(R.string.people_sample_1),
        stringResource(R.string.people_sample_2),
        stringResource(R.string.people_sample_3)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = SmartLedgerDimens.Screen,
            end = SmartLedgerDimens.Screen,
            top = SmartLedgerDimens.Screen,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(stringResource(R.string.accounts), style = MaterialTheme.typography.headlineLarge)
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                placeholder = { Text(stringResource(R.string.search_people)) },
                trailingIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.PersonAdd, contentDescription = stringResource(R.string.add_person))
                    }
                }
            )
        }
        items(sample.filter { it.contains(query, ignoreCase = true) }) { name ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface),
                border = BorderStroke(1.dp, SmartLedgerColors.Border)
            ) {
                Column(Modifier.padding(SmartLedgerDimens.Card)) {
                    Text(name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(R.string.balance_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SmartLedgerColors.TextSecondary
                    )
                }
            }
        }
    }
}
