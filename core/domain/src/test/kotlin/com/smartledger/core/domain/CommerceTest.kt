package com.smartledger.core.domain

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class CommerceTest {
    @Test fun saleTotalAndOutstandingAreExact() {
        val sale = Sale("s1", null, listOf(
            SaleLine("p1", 2, Money.fromDecimal(BigDecimal("12.50"))),
            SaleLine("p2", 1, Money.fromDecimal(BigDecimal("5.00")))
        ), Money.fromDecimal(BigDecimal("10.00")))
        assertEquals(Money.fromDecimal(BigDecimal("30.00")), sale.total())
        assertEquals(Money.fromDecimal(BigDecimal("20.00")), sale.outstanding())
    }

    @Test fun stockUsesMovementSemantics() {
        val movements = listOf(
            StockMovement("p", 10, StockMovementKind.PURCHASE, "x"),
            StockMovement("p", 3, StockMovementKind.SALE, "y")
        )
        assertEquals(7, StockCalculator.quantity(movements))
    }
}
