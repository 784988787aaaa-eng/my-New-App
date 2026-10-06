package com.smartledger.app.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartledger.core.database.ProductEntity
import com.smartledger.app.data.AppDatabaseProvider
import com.smartledger.core.database.SmartLedgerDatabase
import java.util.UUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabaseProvider.get(application)

    val products: StateFlow<List<ProductEntity>> = db.productDao().observeProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addProduct(name: String, sku: String?, unit: String, secondaryUnit: String?, factor: Long, priceMinor: Long, costMinor: Long, minimumStock: Long) = viewModelScope.launch {
        db.productDao().insert(ProductEntity(UUID.randomUUID().toString(), name.trim(), sku?.trim()?.ifBlank { null }, unit.trim().ifBlank { "قطعة" }, secondaryUnit?.trim()?.ifBlank { null }, factor.coerceAtLeast(1), costMinor, priceMinor, minimumStock))
    }


}
