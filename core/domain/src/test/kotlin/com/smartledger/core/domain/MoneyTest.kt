package com.smartledger.core.domain

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyTest {
    @Test
    fun additionPreservesMinorUnits() {
        val first = Money.fromDecimal(BigDecimal("10.25"))
        val second = Money.fromDecimal(BigDecimal("4.75"))

        assertEquals(BigDecimal("15.00"), (first + second).toDecimal())
    }

    @Test
    fun subtractionPreservesMinorUnits() {
        val first = Money.fromDecimal(BigDecimal("10.00"))
        val second = Money.fromDecimal(BigDecimal("3.25"))

        assertEquals(BigDecimal("6.75"), (first - second).toDecimal())
    }
}
