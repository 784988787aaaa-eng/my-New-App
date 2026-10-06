package com.smartledger.app.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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

private data class QuickAction(val label: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String)

@Composable
fun DashboardScreen(onNavigate: (String) -> Unit = {}, viewModel: DashboardViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val currency by viewModel.currency.collectAsState(initial = SupportedCurrencies.YER)
    val actions = listOf(
        QuickAction(R.string.new_operation, Icons.Outlined.Add, "operation"),
        QuickAction(R.string.new_sale, Icons.Outlined.PointOfSale, "sale"),
        QuickAction(R.string.new_purchase, Icons.Outlined.ReceiptLong, "purchase"),
        QuickAction(R.string.add_person, Icons.Outlined.PersonAdd, "people"),
        QuickAction(R.string.view_inventory, Icons.Outlined.Inventory2, "inventory")
    )
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SmartLedgerDimens.Screen),
        verticalArrangement = Arrangement.spacedBy(SmartLedgerDimens.Section)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.dashboard_title), style = MaterialTheme.typography.headlineLarge)
                    Text(stringResource(R.string.app_tagline), color = SmartLedgerColors.TextSecondary)
                }
                IconButton(onClick = { onNavigate("more") }) { Icon(Icons.Outlined.Settings, stringResource(R.string.settings)) }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BalanceCard(Modifier.weight(1f), stringResource(R.string.total_receivable), MoneyFormatter.formatMinorUnits(state.receivable, currency), SmartLedgerColors.Success)
                BalanceCard(Modifier.weight(1f), stringResource(R.string.total_payable), MoneyFormatter.formatMinorUnits(state.payable, currency), SmartLedgerColors.Danger)
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(SmartLedgerDimens.Card), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text(stringResource(R.string.accounts), style = MaterialTheme.typography.titleMedium); Text(state.peopleCount.toString(), style = MaterialTheme.typography.headlineSmall) }
                    Text(currency.arabicName + " • " + currency.code, color = SmartLedgerColors.TextSecondary)
                }
            }
        }
        item {
            Text(stringResource(R.string.quick_actions), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(actions) { action ->
                    Card(onClick = { onNavigate(action.route) }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.width(112.dp)) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(action.icon, contentDescription = stringResource(action.label), tint = MaterialTheme.colorScheme.primary)
                            Text(stringResource(action.label), style = MaterialTheme.typography.labelLarge, maxLines = 2)
                        }
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(SmartLedgerDimens.Card)) {
                    Text(stringResource(R.string.recent_operations), style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(stringResource(R.string.no_operations), color = SmartLedgerColors.TextSecondary)
                    Text(stringResource(R.string.no_operations_hint), color = SmartLedgerColors.TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(modifier: Modifier, title: String, amount: String, tint: androidx.compose.ui.graphics.Color) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface)) {
        Column(Modifier.padding(SmartLedgerDimens.Card), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = tint)
            Text(title, color = SmartLedgerColors.TextSecondary)
            Text(amount, style = MaterialTheme.typography.titleLarge)
        }
    }
}
