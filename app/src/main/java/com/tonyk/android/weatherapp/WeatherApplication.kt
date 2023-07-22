package com.tonyk.android.weatherapp

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.tonyk.android.weatherapp.database.LocationRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, "AIzaSyDrFYzrroCsJ8MiMP4rlg-0PU1lfKTIXkM")

        LocationRepository.initialize(this)

    }
}