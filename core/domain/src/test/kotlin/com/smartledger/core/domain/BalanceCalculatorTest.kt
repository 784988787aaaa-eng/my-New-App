package com.smartledger.core.domain

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class BalanceCalculatorTest {
    @Test fun calculatesReceivableAndPayable() {
        val a = Money.fromDecimal(BigDecimal("100.00")); val b = Money.fromDecimal(BigDecimal("35.50"))
        val result = BalanceCalculator.calculate(listOf(FinancialDirection.RECEIVABLE to a, FinancialDirection.PAYABLE to b))
        assertEquals(a, result.receivable); assertEquals(b, result.payable); assertEquals(Money.fromDecimal(BigDecimal("64.50")), result.net)
    }
}
