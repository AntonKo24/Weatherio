package com.tonyk.android.weatherapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.util.LocationService
import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val weatherViewModel : WeatherViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherViewModel.getSavedLocations()

        if (LocationService.isLocationPermissionGranted(this)) {
            loadData()
        } else {
            LocationService.requestLocationPermission(this) { success ->
                if (success) {
                    loadData()
                } else {
                    weatherViewModel.loadLast()
                }
            }
        }
    }
    private fun loadData() {
        if (LocationService.isGPSEnabled(this)) {
            LocationService.getLocationData(this) { coordinates, address ->
                weatherViewModel.initializeWeatherViewModel(coordinates, address)
            }
        }
        else {
            weatherViewModel.loadLast()
            LocationService.showGPSAlertDialog(this)
        }
    }
}