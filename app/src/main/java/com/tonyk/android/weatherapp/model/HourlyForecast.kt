package com.tonyk.android.weatherapp.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class HourlyForecast (
        val datetime : String,
        val temp : Double,
        val icon : String
        )