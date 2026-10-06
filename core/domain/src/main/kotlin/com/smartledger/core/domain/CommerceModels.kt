package com.smartledger.core.domain

data class SaleLine(val productId: String, val quantity: Long, val unitPrice: Money) {
    init { require(quantity > 0) }
    fun total(): Money = unitPrice * quantity
}

data class PurchaseLine(val productId: String, val quantity: Long, val unitCost: Money) {
    init { require(quantity > 0) }
    fun total(): Money = unitCost * quantity
}

data class Sale(val id: String, val personId: String?, val lines: List<SaleLine>, val paid: Money) {
    fun total(): Money = lines.fold(Money.zero()) { acc, line -> acc + line.total() }
    fun outstanding(): Money = total() - paid
}

data class Purchase(val id: String, val supplierId: String?, val lines: List<PurchaseLine>, val paid: Money) {
    fun total(): Money = lines.fold(Money.zero()) { acc, line -> acc + line.total() }
    fun outstanding(): Money = total() - paid
}
