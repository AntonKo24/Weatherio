package com.tonyk.android.weatherapp.api

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CurrentWeatherItem (
    val temp: String,
    val windspeed : String,
    val humidity : String,
    val conditions : String,
    val precipprob : String,
    val icon : String
)