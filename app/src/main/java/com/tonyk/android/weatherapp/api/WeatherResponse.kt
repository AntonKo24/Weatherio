package com.tonyk.android.weatherapp.api

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class WeatherResponse (
    val address : String,
    val days : List<DailyWeatherItem>,
    val currentConditions : CurrentWeatherItem,
    val description : String
)