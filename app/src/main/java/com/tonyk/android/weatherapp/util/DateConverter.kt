package com.tonyk.android.weatherapp.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateConverter {
    fun formatDate(dateString: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())
        val date = LocalDate.parse(dateString, inputFormat)
        return outputFormat.format(date)
    }

    fun formatDateToFull(dateString: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = DateTimeFormatter.ofPattern("EEEE dd MMMM", Locale.getDefault())
        val date = LocalDate.parse(dateString, inputFormat)
        return outputFormat.format(date)
    }

    fun formatTime(timeString: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = inputFormat.parse(timeString)
        return outputFormat.format(time)
    }

}