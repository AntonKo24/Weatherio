package com.tonyk.android.weatherapp.util

import com.tonyk.android.weatherapp.R

object WeatherIconMapper {
    private val iconMapping = mapOf(
        "snow" to R.drawable.snow,
        "rain" to R.drawable.rain,
        "fog" to R.drawable.fog,
        "wind" to R.drawable.wind,
        "cloudy" to R.drawable.cloudy,
        "partly-cloudy-day" to R.drawable.partly_cloudy_day,
        "partly-cloudy-night" to R.drawable.partly_cloudy_night,
        "clear-day" to R.drawable.clear_day,
        "clear-night" to R.drawable.clear_night
    )

    fun getIconResourceId(iconName: String): Int {
        return iconMapping[iconName] ?: R.drawable.clear_day
    }
}