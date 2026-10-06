package com.smartledger.core.domain

import java.math.BigDecimal
import java.math.RoundingMode

@JvmInline
value class Money private constructor(val minorUnits: Long) {
    companion object {
        fun fromDecimal(value: BigDecimal, scale: Int = 2): Money =
            Money(value.setScale(scale, RoundingMode.HALF_UP).movePointRight(scale).longValueExact())

        fun zero(): Money = Money(0)
    }

    fun toDecimal(scale: Int = 2): BigDecimal =
        BigDecimal.valueOf(minorUnits, scale)

    operator fun plus(other: Money): Money = Money(minorUnits + other.minorUnits)
    operator fun minus(other: Money): Money = Money(minorUnits - other.minorUnits)
    operator fun times(multiplier: Long): Money = Money(minorUnits * multiplier)
}
