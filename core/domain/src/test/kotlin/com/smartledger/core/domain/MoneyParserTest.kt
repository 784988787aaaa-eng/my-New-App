package com.smartledger.core.domain

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyParserTest {
    @Test fun normalizesArabicAndGroupedDigits() {
        val expected = Money.fromDecimal(BigDecimal("1000"))
        assertEquals(expected, MoneyParser.parse("١٬٠٠٠"))
        assertEquals(expected, MoneyParser.parse("1,000"))
        assertEquals(expected, MoneyParser.parse("1000"))
    }
}
