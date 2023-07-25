package com.tonyk.android.weatherapp.repositories

import com.tonyk.android.weatherapp.api.WeatherResponse


interface WeatherApiRepository {
    suspend fun fetchWeather(location: String) : WeatherResponse

}