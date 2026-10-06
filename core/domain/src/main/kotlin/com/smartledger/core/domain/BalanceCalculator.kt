package com.smartledger.core.domain

object BalanceCalculator {
    fun calculate(entries: List<Pair<FinancialDirection, Money>>): Balance {
        var receivable = Money.zero(); var payable = Money.zero()
        entries.forEach { (direction, amount) ->
            require(amount.minorUnits >= 0) { "Amount must not be negative" }
            if (direction == FinancialDirection.RECEIVABLE) receivable += amount else payable += amount
        }
        return Balance(receivable, payable)
    }
}
