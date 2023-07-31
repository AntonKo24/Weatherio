package com.tonyk.android.weatherapp.model


import java.io.Serializable

data class Weatherio (
    val weather : WeatherResponse,
    val location : Location
        ) : Serializable