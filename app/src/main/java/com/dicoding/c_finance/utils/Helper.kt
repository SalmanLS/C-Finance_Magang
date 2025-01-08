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

fun formatTimestamp(inputTimestamp: String, inputFormat: String, outputFormat: String): String {
    return try {
        val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
        val outputDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())

        val date = inputDateFormat.parse(inputTimestamp)
        outputDateFormat.format(date!!)
    } catch (e: Exception) {
        e.printStackTrace()
        "Invalid date"
    }
}




