package com.smartledger.app.ui.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartledger.app.R
import com.smartledger.app.data.CurrencyPreferences
import com.smartledger.app.ui.theme.SmartLedgerColors
import com.smartledger.app.ui.theme.SmartLedgerDimens
import com.smartledger.core.database.SmartLedgerDatabase
import com.smartledger.core.database.DatabaseMigrations
import com.smartledger.core.domain.MoneyFormatter
import com.smartledger.core.domain.SupportedCurrencies
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ReportsState(val receivable: Long, val payable: Long, val people: Int, val products: Int, val sales: Int, val purchases: Int, val salesTotal: Long, val purchasesTotal: Long)
private data class CoreReportState(val receivable: Long, val payable: Long, val people: Int, val products: Int)
private data class CommerceReportState(val sales: Int, val purchases: Int, val salesTotal: Long, val purchasesTotal: Long)

class ReportsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, SmartLedgerDatabase::class.java, "smart_ledger.db")
        .addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3).build()
    private val core = combine(
        db.operationDao().totalReceivable(),
        db.operationDao().totalPayable()
    ) { r, p -> r to p }
    private val counts = combine(
        db.personDao().observePeople(),
        db.productDao().observeProducts()
    ) { people, products -> people.size to products.size }
    private val coreState = combine(core, counts) { money, countsValue ->
        CoreReportState(money.first, money.second, countsValue.first, countsValue.second)
    }
    private val commerceState = combine(
        db.commerceDao().salesCount(),
        db.purchaseDao().purchasesCount()
    ) { sales, purchases -> sales to purchases }
    private val commerceTotals = combine(
        db.commerceDao().salesTotal(),
        db.purchaseDao().purchasesTotal()
    ) { salesTotal, purchasesTotal -> salesTotal to purchasesTotal }
    private val commerce = combine(commerceState, commerceTotals) { countsValue, totals ->
        CommerceReportState(countsValue.first, countsValue.second, totals.first, totals.second)
    }
    val state = combine(coreState, commerce) { coreValue, commerceValue ->
        ReportsState(coreValue.receivable, coreValue.payable, coreValue.people, coreValue.products, commerceValue.sales, commerceValue.purchases, commerceValue.salesTotal, commerceValue.purchasesTotal)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReportsState(0, 0, 0, 0, 0, 0, 0, 0))
    val currency = CurrencyPreferences(application).currency
    override fun onCleared() { db.close(); super.onCleared() }
}

@Composable
fun ReportsScreen(viewModel: ReportsViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val currency by viewModel.currency.collectAsState(initial = SupportedCurrencies.YER)
    var selectedReport by remember { mutableStateOf<Int?>(null) }
    val reports = listOf(R.string.report_people, R.string.report_sales, R.string.report_purchases, R.string.report_inventory, R.string.report_expenses)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SmartLedgerDimens.Screen),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text(stringResource(R.string.reports), style = MaterialTheme.typography.headlineLarge) }
        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface)) {
                Column(Modifier.padding(SmartLedgerDimens.Card)) {
                    Text(stringResource(R.string.report_summary), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.total_receivable) + ": " + MoneyFormatter.formatMinorUnits(state.receivable, currency))
                    Text(stringResource(R.string.total_payable) + ": " + MoneyFormatter.formatMinorUnits(state.payable, currency))
                }
            }
        }
        items(reports) { report ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface),
                border = BorderStroke(1.dp, SmartLedgerColors.Border),
                onClick = { selectedReport = report }
            ) {
                Row(Modifier.fillMaxWidth().padding(SmartLedgerDimens.Card)) {
                    Icon(Icons.Outlined.Assessment, contentDescription = stringResource(report), tint = SmartLedgerColors.Blue600)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(stringResource(report), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.tap_to_preview), color = SmartLedgerColors.TextSecondary)
                    }
                }
            }
        }
    }
    selectedReport?.let { report ->
        AlertDialog(
            onDismissRequest = { selectedReport = null },
            title = { Text(stringResource(report)) },
            text = {
                Text(
                    when (report) {
                        R.string.report_people -> stringResource(R.string.report_people_detail, state.people)
                        R.string.report_sales -> stringResource(R.string.report_sales_detail, state.sales, MoneyFormatter.formatMinorUnits(state.salesTotal, currency))
                        R.string.report_purchases -> stringResource(R.string.report_purchases_detail, state.purchases, MoneyFormatter.formatMinorUnits(state.purchasesTotal, currency))
                        R.string.report_inventory -> stringResource(R.string.report_inventory_detail, state.products)
                        else -> stringResource(R.string.report_financial_detail, MoneyFormatter.formatMinorUnits(state.receivable, currency), MoneyFormatter.formatMinorUnits(state.payable, currency))
                    }
                )
            },
            confirmButton = { TextButton(onClick = { selectedReport = null }) { Text(stringResource(R.string.close)) } }
        )
    }
}
