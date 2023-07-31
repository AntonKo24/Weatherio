package com.tonyk.android.weatherapp.api

import com.tonyk.android.weatherapp.model.WeatherResponse


interface WeatherApiRepository {
    suspend fun fetchWeather(location: String) : WeatherResponse
}