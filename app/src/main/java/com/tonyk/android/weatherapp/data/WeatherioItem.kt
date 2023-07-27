package com.tonyk.android.weatherapp.data


import com.tonyk.android.weatherapp.api.WeatherResponse
import java.io.Serializable


data class WeatherioItem (
    val weather : WeatherResponse,
    val location : LocationItem
        ) : Serializable