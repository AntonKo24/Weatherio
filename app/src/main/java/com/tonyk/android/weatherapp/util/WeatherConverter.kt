package com.tonyk.android.weatherapp.util

object WeatherConverter {
    fun formatData(weatherData: Double): String {
        val rounded = weatherData.toInt()
        return "$rounded"
    }
}

