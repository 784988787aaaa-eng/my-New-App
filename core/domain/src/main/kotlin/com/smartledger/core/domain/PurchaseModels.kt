package com.smartledger.core.domain

data class PurchaseReceipt(val id: String, val supplierId: String?, val lines: List<PurchaseLine>, val paid: Money) {
    fun total(): Money = lines.fold(Money.zero()) { acc, line -> acc + line.total() }
    fun outstanding(): Money = total() - paid
}
