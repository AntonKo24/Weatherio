package com.tonyk.android.weatherapp.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DailyForecastItem (
    val datetime : String,
    val tempmax : Double,
    val tempmin : Double,
    val hours : List<HourlyForecastItem>,
    val conditions : String,
    val humidity : Double,
    val windspeed : Double,
    val pressure : Double,
    val icon : String
    )
