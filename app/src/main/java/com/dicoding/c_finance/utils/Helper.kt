package com.dicoding.c_finance.utils

import java.text.DecimalFormat
import java.util.Locale
import java.text.SimpleDateFormat

data class UserInputFields(
    val username: String,
    val password: String,
    val phone: String,
    val nama: String
)

fun customDateFormat(sqlDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    val parsedDate = inputFormat.parse(sqlDate)
    return parsedDate?.let { outputFormat.format(it) } ?: "Invalid Date"
}

fun customCurrencyFormat(amount: Double, currencySymbol: String = "Rp"): String {
    val formatter = DecimalFormat("#,###.##")
    return "$currencySymbol ${formatter.format(amount)}"
}




