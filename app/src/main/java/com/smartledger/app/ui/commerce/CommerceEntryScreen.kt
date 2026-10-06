package com.smartledger.app.ui.commerce

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.smartledger.app.R
import com.smartledger.core.database.*
import com.smartledger.core.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class CommerceMode { SALE, PURCHASE }

class CommerceEntryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, SmartLedgerDatabase::class.java, "smart_ledger.db")
        .addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3).build()
    val products = db.productDao().observeProducts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val people = db.personDao().observePeople().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun save(mode: CommerceMode, product: ProductEntity, personId: String?, quantity: Long, paid: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            val price = if (mode == CommerceMode.SALE) product.priceMinorUnits else product.costMinorUnits
            val total = price * quantity
            if (total <= 0 || paid !in 0..total) return@launch
            runCatching {
                if (mode == CommerceMode.SALE) {
                    CommerceRepository(db).recordSale(
                        Sale(UUID.randomUUID().toString(), personId, listOf(SaleLine(product.id, quantity, Money.fromDecimal(java.math.BigDecimal.valueOf(price, 2)))), Money.fromDecimal(java.math.BigDecimal.valueOf(paid, 2))),
                        System.currentTimeMillis()
                    )
                } else {
                    PurchaseRepository(db).recordPurchase(
                        PurchaseReceipt(UUID.randomUUID().toString(), personId, listOf(PurchaseLine(product.id, quantity, Money.fromDecimal(java.math.BigDecimal.valueOf(price, 2)))), Money.fromDecimal(java.math.BigDecimal.valueOf(paid, 2))),
                        System.currentTimeMillis()
                    )
                }
            }.onSuccess { onDone() }
        }
    }

    override fun onCleared() { db.close(); super.onCleared() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommerceEntryScreen(mode: CommerceMode, onSaved: () -> Unit, viewModel: CommerceEntryViewModel = viewModel()) {
    val products by viewModel.products.collectAsState()
    val people by viewModel.people.collectAsState()
    var productId by remember { mutableStateOf<String?>(null) }
    var personId by remember { mutableStateOf<String?>(null) }
    var quantity by remember { mutableStateOf("1") }
    var paid by remember { mutableStateOf("") }
    var productExpanded by remember { mutableStateOf(false) }
    var personExpanded by remember { mutableStateOf(false) }
    val product = products.firstOrNull { it.id == productId }
    val unitPrice = product?.let { if (mode == CommerceMode.SALE) it.priceMinorUnits else it.costMinorUnits } ?: 0
    val total = unitPrice * (quantity.toLongOrNull() ?: 0)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text(if (mode == CommerceMode.SALE) stringResource(R.string.new_sale) else stringResource(R.string.new_purchase), style = MaterialTheme.typography.headlineLarge) }
        item {
            ExposedDropdownMenuBox(productExpanded, { productExpanded = !productExpanded }) {
                OutlinedTextField(product?.name.orEmpty(), {}, Modifier.fillMaxWidth().menuAnchor(), readOnly = true, label = { Text(stringResource(R.string.product_name)) })
                ExposedDropdownMenu(productExpanded, { productExpanded = false }) {
                    products.forEach { p -> DropdownMenuItem(text = { Text(p.name) }, onClick = { productId = p.id; productExpanded = false }) }
                }
            }
        }
        item {
            ExposedDropdownMenuBox(personExpanded, { personExpanded = !personExpanded }) {
                OutlinedTextField(people.firstOrNull { it.id == personId }?.name.orEmpty(), {}, Modifier.fillMaxWidth().menuAnchor(), readOnly = true, label = { Text(if (mode == CommerceMode.SALE) stringResource(R.string.select_customer_optional) else stringResource(R.string.select_supplier_optional)) })
                ExposedDropdownMenu(personExpanded, { personExpanded = false }) {
                    Text(stringResource(R.string.cash_transaction), modifier = Modifier.padding(16.dp))
                    people.forEach { p -> DropdownMenuItem(text = { Text(p.name) }, onClick = { personId = p.id; personExpanded = false }) }
                }
            }
        }
        item { OutlinedTextField(quantity, { quantity = it.filter(Char::isDigit) }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.quantity)) }, singleLine = true) }
        item { OutlinedTextField(paid, { paid = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.paid_amount)) }, singleLine = true) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.invoice_total), style = MaterialTheme.typography.titleMedium)
                    Text(total.toString() + " " + stringResource(R.string.currency_yer), style = MaterialTheme.typography.headlineSmall)
                    Text(stringResource(R.string.outstanding_amount) + ": " + (total - (paid.toLongOrNull() ?: 0)).coerceAtLeast(0), color = MaterialTheme.colorScheme.error)
                }
            }
        }
        item {
            Button(
                onClick = { product?.let { viewModel.save(mode, it, personId, quantity.toLongOrNull() ?: 0, paid.toLongOrNull() ?: 0, onSaved) } },
                enabled = product != null && (quantity.toLongOrNull() ?: 0) > 0 && (paid.toLongOrNull() ?: 0) in 0..total,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.save)) }
        }
    }
}
