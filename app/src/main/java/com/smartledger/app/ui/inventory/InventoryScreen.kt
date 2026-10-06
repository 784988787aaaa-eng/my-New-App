package com.smartledger.app.ui.inventory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartledger.app.R
import com.smartledger.core.domain.MoneyParser
import com.smartledger.app.ui.theme.SmartLedgerColors
import com.smartledger.app.ui.theme.SmartLedgerDimens

@Composable
fun InventoryScreen(viewModel: InventoryViewModel = viewModel()) {
    val products by viewModel.products.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SmartLedgerDimens.Screen),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.view_inventory), style = MaterialTheme.typography.headlineLarge)
                IconButton(onClick = { showAdd = true }) { Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.add_product)) }
            }
        }
        if (products.isEmpty()) item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface)) {
                Column(Modifier.padding(SmartLedgerDimens.Card)) {
                    Icon(Icons.Outlined.Inventory2, contentDescription = null)
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.no_products), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.add_product), color = SmartLedgerColors.TextSecondary)
                }
            }
        }
        items(products, key = { it.id }) { product ->
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface),
                border = BorderStroke(1.dp, SmartLedgerColors.Border)
            ) {
                Column(Modifier.padding(SmartLedgerDimens.Card), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(product.name, style = MaterialTheme.typography.titleMedium)
                    product.sku?.let { Text(it, color = SmartLedgerColors.TextSecondary) }
                    Text("سعر البيع: " + product.priceMinorUnits.toString() + " " + stringResource(R.string.currency_yer), color = SmartLedgerColors.TextSecondary)
                }
            }
        }
    }
    if (showAdd) AddProductDialog(
        onDismiss = { showAdd = false },
        onSave = { name, sku, price, cost, minimum ->
            viewModel.addProduct(name, sku, MoneyParser.parse(price).minorUnits, MoneyParser.parse(cost).minorUnits, minimum.toLongOrNull() ?: 0)
            showAdd = false
        }
    )
}

@Composable
private fun AddProductDialog(onDismiss: () -> Unit, onSave: (String,String,String,String,String)->Unit) {
    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var minimum by remember { mutableStateOf("0") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_product)) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(SmartLedgerDimens.FormGap)) {
            OutlinedTextField(name, { name = it }, label = { Text(stringResource(R.string.product_name)) }, singleLine = true)
            OutlinedTextField(sku, { sku = it }, label = { Text(stringResource(R.string.product_sku)) }, singleLine = true)
            OutlinedTextField(price, { price = it }, label = { Text(stringResource(R.string.product_price)) }, singleLine = true)
            OutlinedTextField(cost, { cost = it }, label = { Text(stringResource(R.string.product_cost)) }, singleLine = true)
            OutlinedTextField(minimum, { minimum = it }, label = { Text(stringResource(R.string.product_quantity)) }, singleLine = true)
        }},
        confirmButton = { Button(onClick = { onSave(name, sku, price, cost, minimum) }, enabled = name.isNotBlank() && price.isNotBlank() && cost.isNotBlank()) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
