package com.smartledger.app.ui.reports

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
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smartledger.app.R
import com.smartledger.app.ui.theme.SmartLedgerColors
import com.smartledger.app.ui.theme.SmartLedgerDimens

@Composable
fun ReportsScreen() {
    val reports = listOf(
        R.string.report_people,
        R.string.report_sales,
        R.string.report_purchases,
        R.string.report_inventory,
        R.string.report_expenses
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SmartLedgerDimens.Screen),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(stringResource(R.string.reports), style = MaterialTheme.typography.headlineLarge)
        }
        items(reports) { report ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface),
                border = BorderStroke(1.dp, SmartLedgerColors.Border)
            ) {
                Column(Modifier.padding(SmartLedgerDimens.Card)) {
                    Icon(Icons.Outlined.Assessment, contentDescription = stringResource(report), tint = SmartLedgerColors.Blue600)
                    Text(stringResource(report), style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(R.string.report_preview_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SmartLedgerColors.TextSecondary
                    )
                }
            }
        }
    }
}
