package com.arjun.headout.data.local.preferences

import com.arjun.headout.data.model.Currency

object CurrencyHelper {
    val currencies = listOf(
        Currency("Indian Rupee", "INR", "₹"),
        Currency("United States Dollar", "USD", "$"),
        Currency("Euro", "EUR", "€"),
        Currency("British Pound", "GBP", "£"),
        Currency("Japanese Yen", "JPY", "¥"),
        Currency("Chinese Yuan Renminbi", "CNY", "¥"),
        Currency("Canadian Dollar", "CAD", "CA$"),
        Currency("Australian Dollar", "AUD", "AU$"),
        Currency("Swiss Franc", "CHF", "CHF"),
        Currency("Singapore Dollar", "SGD", "S$"),
        Currency("South Korean Won", "KRW", "₩"),
    )

    fun getCurrencyByCode(code: String): Currency? {
        return currencies.find { it.code == code }
    }
}