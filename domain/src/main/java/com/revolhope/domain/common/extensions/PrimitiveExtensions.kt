package com.revolhope.domain.common.extensions

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

const val EMPTY_STRING = ""
const val CURRENCY_SYMBOL = "€"
const val CURRENCY_FORMAT = "#,##0.00$CURRENCY_SYMBOL"
const val DATE_FORMAT = "dd/MM/yyyy"
val LOCALE_ES = Locale("es", "ES")

inline val Float.priceFormat: String?
    get() =
        try {
            DecimalFormat(CURRENCY_FORMAT).format(toDouble())
        } catch (e: Exception) {
            null
        }

inline val String.priceValue: Float? get() = replace(CURRENCY_SYMBOL, EMPTY_STRING).toFloatOrNull()

fun Long.toDateFormat(locale: Locale = LOCALE_ES): String? =
    try {
        SimpleDateFormat(DATE_FORMAT, locale).format(this)
    } catch (e: Exception) {
        null
    }
