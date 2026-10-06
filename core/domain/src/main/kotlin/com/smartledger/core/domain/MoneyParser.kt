package com.smartledger.core.domain

import java.math.BigDecimal
import java.text.DecimalFormatSymbols

object MoneyParser {
    fun parse(input: String, scale: Int = 2): Money {
        val normalized = input.trim()
            .replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5")
            .replace("٦", "6").replace("٧", "7").replace("٨", "8").replace("٩", "9").replace("٠", "0")
            .replace("٬", "").replace(",", "")
            .replace(DecimalFormatSymbols.getInstance().groupingSeparator.toString(), "")
        return Money.fromDecimal(BigDecimal(normalized), scale)
    }
}
