package com.example.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    fun formatDate(timestamp: Long): String {
        if (timestamp <= 0L) return "N/A"
        return dateFormat.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        if (timestamp <= 0L) return "N/A"
        return dateTimeFormat.format(Date(timestamp))
    }

    fun getDaysDifference(timestamp: Long): Long {
        val diff = timestamp - System.currentTimeMillis()
        return diff / (24 * 60 * 60 * 1000)
    }
}

object CurrencyUtils {
    fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("bn", "BD"))
        formatter.maximumFractionDigits = 2
        return "৳ ${String.format(Locale.US, "%.2f", amount)}"
    }
}
