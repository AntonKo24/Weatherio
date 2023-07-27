package com.tonyk.android.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.tonyk.android.weatherapp.util.LocationService
import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val weatherViewModel : WeatherViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherViewModel.getList()

        if (LocationService.isLocationPermissionGranted(this)) {
            if (LocationService.isGPSEnabled(this)) {
                LocationService.getLocationData(this) { coordinates, address ->
                    weatherViewModel.initializeWeatherViewModel(coordinates, address)
                }
            }
            else {
                weatherViewModel.loadLast()
                LocationService.showGPSAlertDialog(this)
            }
        } else {
            LocationService.requestLocationPermission(this) { success ->
                if (success) {
                    if (LocationService.isGPSEnabled(this)) {
                        LocationService.getLocationData(this) { coordinates, address ->
                            weatherViewModel.initializeWeatherViewModel(coordinates, address)
                        }
                    }
                    else {
                        weatherViewModel.loadLast()
                        LocationService.showGPSAlertDialog(this)
                    }
                } else {
                    weatherViewModel.loadLast()
                }
            }
        }
    }
}