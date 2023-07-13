package com.tonyk.android.weatherapp.repositories

import com.tonyk.android.weatherapp.api.WeatherApi
import com.tonyk.android.weatherapp.api.WeatherResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherApiRepositoryImpl @Inject constructor(private val weatherApi: WeatherApi) :
    WeatherApiRepository {

    override suspend fun fetchWeather() : WeatherResponse =
        weatherApi.fetchWeather()

}