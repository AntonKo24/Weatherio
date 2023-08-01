package com.tonyk.android.weatherapp.api

import com.tonyk.android.weatherapp.util.Constants
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class WeatherInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val newUrl: HttpUrl = originalRequest.url.newBuilder()

            .addQueryParameter("key", Constants.WEATHER_API_KEY)
            .addQueryParameter("unitGroup", "metric")

            .build()
        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newRequest)
    }
}