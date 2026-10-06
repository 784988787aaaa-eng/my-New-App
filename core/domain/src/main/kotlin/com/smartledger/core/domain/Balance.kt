package com.smartledger.core.domain

data class Balance(val receivable: Money, val payable: Money) {
    val net: Money get() = receivable - payable
    companion object { fun zero() = Balance(Money.zero(), Money.zero()) }
}
