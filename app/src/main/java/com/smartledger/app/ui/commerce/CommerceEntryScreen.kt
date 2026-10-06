package com.smartledger.app.ui.commerce

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.smartledger.app.R
import com.smartledger.core.database.*
import com.smartledger.core.domain.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class CommerceMode { SALE, PURCHASE }
data class DraftLine(val productId: String, val quantity: Long)

class CommerceEntryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, SmartLedgerDatabase::class.java, "smart_ledger.db")
        .addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3, DatabaseMigrations.MIGRATION_3_4).build()
    val products = db.productDao().observeProducts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val people = db.personDao().observePeople().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun save(mode: CommerceMode, lines: List<DraftLine>, personId: String?, paid: Long, onDone: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            runCatching {
                require(lines.isNotEmpty()) { "يجب إضافة صنف واحد على الأقل" }
                if (mode == CommerceMode.SALE) {
                    val saleLines = lines.map { draft ->
                        val p = products.value.first { it.id == draft.productId }
                        require(draft.quantity > 0)
                        SaleLine(p.id, draft.quantity, Money.fromMinorUnits(p.priceMinorUnits))
                    }
                    val total = saleLines.sumOf { it.unitPrice.minorUnits * it.quantity }
                    require(paid in 0..total)
                    CommerceRepository(db).recordSale(
                        Sale(UUID.randomUUID().toString(), personId, saleLines, Money.fromDecimal(java.math.BigDecimal.valueOf(paid, 2))),
                        System.currentTimeMillis()
                    )
                } else {
                    val purchaseLines = lines.map { draft ->
                        val p = products.value.first { it.id == draft.productId }
                        require(draft.quantity > 0)
                        PurchaseLine(p.id, draft.quantity, Money.fromMinorUnits(p.costMinorUnits))
                    }
                    val total = purchaseLines.sumOf { it.unitCost.minorUnits * it.quantity }
                    require(paid in 0..total)
                    PurchaseRepository(db).recordPurchase(
                        PurchaseReceipt(UUID.randomUUID().toString(), personId, purchaseLines, Money.fromDecimal(java.math.BigDecimal.valueOf(paid, 2))),
                        System.currentTimeMillis()
                    )
                }
            }.onSuccess { onDone() }.onFailure { onError(it.message ?: "تعذر حفظ الفاتورة") }
        }
    }
    override fun onCleared() { db.close(); super.onCleared() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommerceEntryScreen(mode: CommerceMode, onSaved: () -> Unit, viewModel: CommerceEntryViewModel = viewModel()) {
    val products by viewModel.products.collectAsState()
    val people by viewModel.people.collectAsState()
    var personId by remember { mutableStateOf<String?>(null) }
    var productId by remember { mutableStateOf<String?>(null) }
    var quantity by remember { mutableStateOf("1") }
    var paid by remember { mutableStateOf("") }
    var productExpanded by remember { mutableStateOf(false) }
    var personExpanded by remember { mutableStateOf(false) }
    var lines by remember { mutableStateOf<List<DraftLine>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val total = lines.sumOf { line ->
        val p = products.firstOrNull { it.id == line.productId } ?: return@sumOf 0L
        (if (mode == CommerceMode.SALE) p.priceMinorUnits else p.costMinorUnits) * line.quantity
    }
    val paidValue = paid.trim().takeIf { it.isNotBlank() }?.let { runCatching { MoneyParser.parse(it).minorUnits }.getOrDefault(0L) } ?: 0L

    LazyColumn(modifier = Modifier.fillMaxSize().imePadding(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(if (mode == CommerceMode.SALE) stringResource(R.string.new_sale) else stringResource(R.string.new_purchase), style = MaterialTheme.typography.headlineLarge) }
        item {
            ExposedDropdownMenuBox(productExpanded, { productExpanded = !productExpanded }) {
                OutlinedTextField(products.firstOrNull { it.id == productId }?.name.orEmpty(), {}, Modifier.fillMaxWidth().menuAnchor(), readOnly = true, label = { Text(stringResource(R.string.product_name)) })
                ExposedDropdownMenu(productExpanded, { productExpanded = false }) {
                    products.forEach { p -> DropdownMenuItem(text = { Text(p.name) }, onClick = { productId = p.id; productExpanded = false }) }
                }
            }
        }
        item { OutlinedTextField(quantity, { quantity = it.filter(Char::isDigit) }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.quantity)) }, singleLine = true) }
        item {
            Button(onClick = {
                productId?.let { id ->
                    val q = quantity.trim().takeIf { it.isNotBlank() }?.let { runCatching { MoneyParser.parse(it, 0).minorUnits }.getOrDefault(0L) } ?: 0L
                    if (q > 0L) { lines = lines + DraftLine(id, q); productId = null; quantity = "1" }
                }
            }, enabled = productId != null && (quantity.toLongOrNull() ?: 0L) > 0L, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.add_invoice_line))
            }
        }
        items(lines.indices.toList()) { index ->
            val line = lines[index]
            val p = products.firstOrNull { it.id == line.productId }
            ListItem(
                headlineContent = { Text(p?.name.orEmpty()) },
                supportingContent = { Text(line.quantity.toString() + " × " + (p?.let { if (mode == CommerceMode.SALE) it.priceMinorUnits else it.costMinorUnits } ?: 0L).toString()) },
                trailingContent = { TextButton(onClick = { lines = lines.toMutableList().also { it.removeAt(index) } }) { Text(stringResource(R.string.remove)) } }
            )
        }
        item {
            ExposedDropdownMenuBox(personExpanded, { personExpanded = !personExpanded }) {
                OutlinedTextField(people.firstOrNull { it.id == personId }?.name.orEmpty(), {}, Modifier.fillMaxWidth().menuAnchor(), readOnly = true, label = { Text(if (mode == CommerceMode.SALE) stringResource(R.string.select_customer_optional) else stringResource(R.string.select_supplier_optional)) })
                ExposedDropdownMenu(personExpanded, { personExpanded = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.cash_transaction)) }, onClick = { personId = null; personExpanded = false })
                    people.forEach { p -> DropdownMenuItem(text = { Text(p.name) }, onClick = { personId = p.id; personExpanded = false }) }
                }
            }
        }
        item { OutlinedTextField(paid, { paid = it.filter(Char::isDigit) }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.paid_amount)) }, singleLine = true) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.invoice_total), style = MaterialTheme.typography.titleMedium)
                    Text(MoneyFormatter.formatMinorUnits(total, SupportedCurrencies.YER), style = MaterialTheme.typography.headlineSmall)
                    Text(stringResource(R.string.outstanding_amount) + ": " + MoneyFormatter.formatMinorUnits((total - paidValue).coerceAtLeast(0L), SupportedCurrencies.YER))
                }
            }
        }
        error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        item {
            Button(onClick = { viewModel.save(mode, lines, personId, paidValue, onSaved) { error = it } }, enabled = lines.isNotEmpty() && total > 0L && paidValue in 0L..total, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
