package com.tonyk.android.weatherapp.api

import retrofit2.http.GET

private const val API_KEY = "TK6K4VCW7447EXSHF7S768U6F"


interface WeatherApi {
    @GET(
        "VisualCrossingWebServices/rest/services/timeline/London%2CUK?unitGroup=us&key=$API_KEY"

    )

    suspend fun fetchWeather(): WeatherResponse

}