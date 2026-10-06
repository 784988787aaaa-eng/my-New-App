package com.smartledger.core.domain

data class Expense(val id: String, val category: String, val amount: Money, val note: String?, val createdAt: Long) {
    init { require(amount.minorUnits > 0) }
}
data class Employee(val id: String, val name: String, val phone: String?, val active: Boolean = true)
