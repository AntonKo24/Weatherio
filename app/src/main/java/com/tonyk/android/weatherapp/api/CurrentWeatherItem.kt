package com.tonyk.android.weatherapp.api

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CurrentWeatherItem (
    val datetime : String,
    val temp: Double,
    val windspeed : Double,
    val humidity : Double,
    val conditions : String,
    val pressure : Double,
    val icon : String
)