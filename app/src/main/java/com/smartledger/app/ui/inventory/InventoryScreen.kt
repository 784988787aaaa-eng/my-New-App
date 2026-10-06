package com.smartledger.app.ui.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartledger.app.R
import com.smartledger.app.ui.theme.SmartLedgerColors
import com.smartledger.app.ui.theme.SmartLedgerDimens
import com.smartledger.core.domain.MoneyFormatter
import com.smartledger.core.domain.MoneyParser
import com.smartledger.core.domain.SupportedCurrencies

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(viewModel: InventoryViewModel = viewModel()) {
    val products by viewModel.products.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var queryOpen by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val filtered = products.filter { query.isBlank() || it.name.contains(query, true) || it.sku.orEmpty().contains(query, true) }

    Scaffold(floatingActionButton = { FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Outlined.Add, stringResource(R.string.add_product)) } }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(SmartLedgerDimens.Screen), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.view_inventory), style = MaterialTheme.typography.headlineLarge)
                    IconButton(onClick = { queryOpen = !queryOpen; if (!queryOpen) query = "" }) {
                        Icon(if (queryOpen) Icons.Outlined.Close else Icons.Outlined.Search, stringResource(R.string.search_people))
                    }
                }
            }
            if (queryOpen) item {
                OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text(stringResource(R.string.search_inventory)) }, leadingIcon = { Icon(Icons.Outlined.Search, null) })
            }
            if (filtered.isEmpty()) item {
                Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(SmartLedgerDimens.Card)) { Icon(Icons.Outlined.Inventory2, null); Text(stringResource(R.string.no_products), style = MaterialTheme.typography.titleMedium) } }
            }
            items(filtered, key = { it.id }) { product ->
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SmartLedgerColors.Surface), border = androidx.compose.foundation.BorderStroke(1.dp, SmartLedgerColors.Border)) {
                    ListItem(
                        headlineContent = { Text(product.name, maxLines = 1) },
                        supportingContent = {
                            Column {
                                product.sku?.let { Text(it, color = SmartLedgerColors.TextSecondary) }
                                Text(product.unitId + product.secondaryUnitId?.let { " • 1 $it = " + product.conversionFactor + " " + product.unitId }.orEmpty(), color = SmartLedgerColors.TextSecondary)
                            }
                        },
                        trailingContent = {
                            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                                Text(MoneyFormatter.formatMinorUnits(product.priceMinorUnits, SupportedCurrencies.YER), style = MaterialTheme.typography.titleMedium)
                                Text(MoneyFormatter.formatMinorUnits(product.costMinorUnits, SupportedCurrencies.YER), style = MaterialTheme.typography.bodyMedium, color = SmartLedgerColors.TextSecondary)
                            }
                        }
                    )
                }
            }
        }
    }
    if (showAdd) AddProductSheet(onDismiss = { showAdd = false }) { name, sku, unit, secondary, factor, price, cost, minimum ->
        viewModel.addProduct(name, sku, unit, secondary, factor, MoneyParser.parse(price).minorUnits, MoneyParser.parse(cost).minorUnits, minimum.toLongOrNull() ?: 0L)
        showAdd = false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductSheet(onDismiss: () -> Unit, onSave: (String,String,String,String?,Long,String,String,String)->Unit) {
    var name by remember { mutableStateOf("") }; var sku by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }; var secondary by remember { mutableStateOf("") }
    var factor by remember { mutableStateOf("1") }; var price by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }; var minimum by remember { mutableStateOf("0") }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding().imePadding().padding(horizontal = 16.dp, vertical = 8.dp).verticalScroll(androidx.compose.foundation.rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.add_product), style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.product_name)) }, singleLine = true)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(unit, { unit = it }, Modifier.weight(1f), label = { Text(stringResource(R.string.primary_unit)) }, singleLine = true)
                OutlinedTextField(secondary, { secondary = it }, Modifier.weight(1f), label = { Text(stringResource(R.string.secondary_unit_optional)) }, singleLine = true)
            }
            if (secondary.isNotBlank()) OutlinedTextField(factor, { factor = latinDigits(it).filter(Char::isDigit).take(8) }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.unit_conversion)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            OutlinedTextField(sku, { sku = it }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.product_sku)) }, singleLine = true)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(cost, { cost = latinDigits(it) }, Modifier.weight(1f), label = { Text(stringResource(R.string.product_cost)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(price, { price = latinDigits(it) }, Modifier.weight(1f), label = { Text(stringResource(R.string.product_price)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }
            OutlinedTextField(minimum, { minimum = latinDigits(it).filter(Char::isDigit) }, Modifier.fillMaxWidth(), label = { Text(stringResource(R.string.minimum_stock)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            Button(onClick = { onSave(name, sku, unit, secondary.ifBlank { null }, (factor.toLongOrNull() ?: 1L).coerceAtLeast(1), price, cost, minimum) }, enabled = name.isNotBlank() && price.isNotBlank() && cost.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Save, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.save))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
private fun latinDigits(value: String): String = value
    .replace("٠","0").replace("١","1").replace("٢","2").replace("٣","3").replace("٤","4")
    .replace("٥","5").replace("٦","6").replace("٧","7").replace("٨","8").replace("٩","9")