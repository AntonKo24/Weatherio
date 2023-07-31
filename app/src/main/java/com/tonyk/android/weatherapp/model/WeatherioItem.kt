package com.tonyk.android.weatherapp.model


import java.io.Serializable

data class WeatherioItem (
    val weather : WeatherResponse,
    val location : LocationItem
        ) : Serializable