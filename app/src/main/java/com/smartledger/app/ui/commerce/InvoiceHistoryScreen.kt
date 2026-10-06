@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.smartledger.app.ui.commerce

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartledger.app.data.AppDatabaseProvider
import com.smartledger.core.database.*
import com.smartledger.core.domain.Money
import com.smartledger.core.domain.SaleLine
import com.smartledger.core.domain.MoneyFormatter
import com.smartledger.core.domain.SupportedCurrencies
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InvoiceHistoryViewModel(application: android.app.Application) : AndroidViewModel(application) {
    private val db = AppDatabaseProvider.get(application)
    private val salesRepository = CommerceRepository(db)
    private val purchaseRepository = PurchaseRepository(db)
    private val _sales = MutableStateFlow<List<SaleEntity>>(emptyList())
    val sales: StateFlow<List<SaleEntity>> = _sales.asStateFlow()
    private val _purchases = MutableStateFlow<List<PurchaseEntity>>(emptyList())
    val purchases: StateFlow<List<PurchaseEntity>> = _purchases.asStateFlow()
    var message by mutableStateOf<String?>(null); private set
    init { refresh() }
    fun refresh() = viewModelScope.launch {
        _sales.value = db.commerceDao().sales()
        _purchases.value = db.purchaseDao().observeAll().first()
    }
    fun returnSale(id: String) = viewModelScope.launch {
        runCatching { salesRepository.returnSale(id, System.currentTimeMillis()) }
            .onSuccess { message = "تم إرجاع الفاتورة"; refresh() }.onFailure { message = it.message }
    }
    fun returnPurchase(id: String) = viewModelScope.launch {
        runCatching { purchaseRepository.returnPurchase(id, System.currentTimeMillis()) }
            .onSuccess { message = "تم إرجاع فاتورة الشراء"; refresh() }.onFailure { message = it.message }
    }
    fun editSalePaid(id: String, paid: Long) = viewModelScope.launch {
        runCatching {
            val lines = db.commerceDao().saleLines(id).map { SaleLine(it.productId, it.quantity, Money.fromMinorUnits(it.unitPriceMinorUnits)) }
            salesRepository.editSale(id, lines, Money.fromMinorUnits(paid), System.currentTimeMillis())
        }.onSuccess { message = "تم تعديل المدفوع وإعادة احتساب الرصيد"; refresh() }.onFailure { message = it.message }
    }
}

@Composable
fun InvoiceHistoryScreen(viewModel: InvoiceHistoryViewModel = viewModel()) {
    val sales by viewModel.sales.collectAsState()
    val purchases by viewModel.purchases.collectAsState()
    var tab by remember { mutableStateOf(0) }
    var editSale by remember { mutableStateOf<SaleEntity?>(null) }
    var paidText by remember { mutableStateOf("") }
    val currency = SupportedCurrencies.YER
    Column(Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = tab) {
            Tab(tab == 0, { tab = 0 }, text = { Text("المبيعات") })
            Tab(tab == 1, { tab = 1 }, text = { Text("المشتريات") })
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (tab == 0) items(sales, key = { it.id }) { sale ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text("فاتورة بيع · " + sale.id.take(8), style = MaterialTheme.typography.titleMedium)
                        Text("الإجمالي: " + MoneyFormatter.formatMinorUnits(sale.totalMinorUnits, currency))
                        Text("المدفوع: " + MoneyFormatter.formatMinorUnits(sale.paidMinorUnits, currency))
                        Text("المتبقي: " + MoneyFormatter.formatMinorUnits((sale.totalMinorUnits - sale.paidMinorUnits).coerceAtLeast(0), currency))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { editSale = sale; paidText = sale.paidMinorUnits.toString() }) { Text("تعديل المدفوع") }
                            TextButton(onClick = { viewModel.returnSale(sale.id) }) { Text("مرتجع") }
                        }
                    }
                }
            } else items(purchases, key = { it.id }) { purchase ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text("فاتورة شراء · " + purchase.id.take(8), style = MaterialTheme.typography.titleMedium)
                        Text("الإجمالي: " + MoneyFormatter.formatMinorUnits(purchase.totalMinorUnits, currency))
                        Text("المدفوع: " + MoneyFormatter.formatMinorUnits(purchase.paidMinorUnits, currency))
                        Text("المتبقي: " + MoneyFormatter.formatMinorUnits((purchase.totalMinorUnits - purchase.paidMinorUnits).coerceAtLeast(0), currency))
                        TextButton(onClick = { viewModel.returnPurchase(purchase.id) }) { Text("مرتجع") }
                    }
                }
            }
        }
        viewModel.message?.let { Text(it, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.primary) }
    }
    editSale?.let { selected ->
        AlertDialog(
            onDismissRequest = { editSale = null },
            title = { Text("تعديل المدفوع") },
            text = { OutlinedTextField(paidText, { paidText = it.filter(Char::isDigit) }, label = { Text("المبلغ المدفوع") }, singleLine = true) },
            confirmButton = { Button(onClick = { viewModel.editSalePaid(selected.id, paidText.toLongOrNull() ?: 0L); editSale = null }, enabled = (paidText.toLongOrNull() ?: -1) in 0..selected.totalMinorUnits) { Text("حفظ") } },
            dismissButton = { TextButton(onClick = { editSale = null }) { Text("إلغاء") } }
        )
    }
}
