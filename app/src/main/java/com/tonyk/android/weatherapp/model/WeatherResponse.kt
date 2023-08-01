package com.tonyk.android.weatherapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse (
    val resolvedAddress : String,
    val days : List<DailyForecast>,
    val currentConditions : CurrentConditions,
    val description : String
)
