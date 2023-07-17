package com.tonyk.android.weatherapp.api

import retrofit2.http.GET
import retrofit2.http.Path


interface WeatherApi {
    @GET("VisualCrossingWebServices/rest/services/timeline/{location}")
    suspend fun fetchWeather(@Path("location") location: String): WeatherResponse
}


