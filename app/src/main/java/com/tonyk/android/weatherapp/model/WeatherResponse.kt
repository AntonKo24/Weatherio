package com.tonyk.android.weatherapp.model

import com.squareup.moshi.JsonClass
import com.tonyk.android.weatherapp.model.CurrentConditionsItem
import com.tonyk.android.weatherapp.model.DailyForecastItem


@JsonClass(generateAdapter = true)
data class WeatherResponse (
    val resolvedAddress : String,
    val days : List<DailyForecastItem>,
    val currentConditions : CurrentConditionsItem,
    val description : String
)
