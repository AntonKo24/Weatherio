package com.tonyk.android.weatherapp

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.tonyk.android.weatherapp.util.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, Constants.PLACES_API_KEY)

    }
}