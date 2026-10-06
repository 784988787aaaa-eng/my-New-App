package com.smartledger.core.domain

data class Currency(
    val code: String,
    val symbol: String,
    val arabicName: String,
    val englishName: String,
    val fractionDigits: Int = 2
)

object SupportedCurrencies {
    val YER = Currency("YER", "ر.ي", "الريال اليمني", "Yemeni Rial", 2)
    val SAR = Currency("SAR", "ر.س", "الريال السعودي", "Saudi Riyal", 2)
    val USD = Currency("USD", "$", "الدولار الأمريكي", "US Dollar", 2)
    val EUR = Currency("EUR", "€", "اليورو", "Euro", 2)
    val AED = Currency("AED", "د.إ", "الدرهم الإماراتي", "UAE Dirham", 2)
    val all = listOf(YER, SAR, USD, EUR, AED)
    fun byCode(code: String) = all.firstOrNull { it.code == code } ?: YER
}