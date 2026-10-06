package com.smartledger.core.domain

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object MoneyFormatter {
    fun formatMinorUnits(minorUnits: Long, currency: Currency): String {
        val value = BigDecimal.valueOf(minorUnits, currency.fractionDigits)
        val symbols = DecimalFormatSymbols(Locale("ar", "YE")).apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }
        val pattern = if (currency.fractionDigits == 0) "#,##0" else "#,##0." + "0".repeat(currency.fractionDigits)
        return DecimalFormat(pattern, symbols).format(value) + " " + currency.symbol
    }
}