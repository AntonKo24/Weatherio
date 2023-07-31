package com.tonyk.android.weatherapp.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CurrentConditions (
    val datetime : String,
    val temp: Double,
    val windspeed : Double,
    val humidity : Double,
    val conditions : String,
    val pressure : Double,
    val icon : String
)