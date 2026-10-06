package com.smartledger.app.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.smartledger.core.database.DatabaseMigrations
import com.smartledger.core.database.ProductEntity
import com.smartledger.core.database.SmartLedgerDatabase
import java.util.UUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, SmartLedgerDatabase::class.java, "smart_ledger.db")
        .addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3).build()

    val products: StateFlow<List<ProductEntity>> = db.productDao().observeProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addProduct(name: String, sku: String?, priceMinor: Long, costMinor: Long, minimumStock: Long) = viewModelScope.launch {
        db.productDao().insert(ProductEntity(UUID.randomUUID().toString(), name.trim(), sku?.trim()?.ifBlank { null }, "piece", costMinor, priceMinor, minimumStock))
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}
