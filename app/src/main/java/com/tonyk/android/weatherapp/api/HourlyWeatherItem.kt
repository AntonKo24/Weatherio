package com.tonyk.android.weatherapp.api

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class HourlyWeatherItem (
        val datetime : String,
        val temp : String,
        val icon : String
        )