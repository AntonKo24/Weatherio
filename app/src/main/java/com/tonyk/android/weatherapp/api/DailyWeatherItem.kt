package com.tonyk.android.weatherapp.api

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DailyWeatherItem (
    val datetime : String,
    val tempmax : Double,
    val tempmin : Double,
    val hours : List<HourlyWeatherItem>,
    val conditions : String,
    val humidity : Double,
    val windspeed : Double,
    val pressure : Double,
    val icon : String
    )
