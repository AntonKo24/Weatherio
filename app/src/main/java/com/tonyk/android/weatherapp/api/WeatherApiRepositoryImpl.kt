package com.tonyk.android.weatherapp.api

import com.tonyk.android.weatherapp.api.WeatherApi
import com.tonyk.android.weatherapp.api.WeatherApiRepository
import com.tonyk.android.weatherapp.model.WeatherResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherApiRepositoryImpl @Inject constructor(private val weatherApi: WeatherApi) :
    WeatherApiRepository {

    override suspend fun fetchWeather(location: String) : WeatherResponse {
      return  weatherApi.fetchWeather(location)
    }

}