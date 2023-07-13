package com.tonyk.android.weatherapp.api

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DailyWeatherItem (
    val datetime : String,
    val tempmax : String,
    val tempmin : String,
    val hours : List<HourlyWeatherItem>,
    val conditions : String,
    val humidity : String,
    val windspeed : String,
    val precipprob : String
    )
