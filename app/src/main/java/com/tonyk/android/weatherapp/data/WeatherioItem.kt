package com.tonyk.android.weatherapp.data

import com.tonyk.android.weatherapp.api.WeatherResponse




data class WeatherioItem (
    val weather : WeatherResponse,
    val address : String
        )