package com.tonyk.android.weatherapp.repositories

import android.util.Log
import com.tonyk.android.weatherapp.api.WeatherApi
import com.tonyk.android.weatherapp.api.WeatherResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherApiRepositoryImpl @Inject constructor(private val weatherApi: WeatherApi) :
    WeatherApiRepository {

    override suspend fun fetchWeather(location: String) : WeatherResponse {
        Log.d("debbsasd", "111")
      return  weatherApi.fetchWeather(location)
    }


}