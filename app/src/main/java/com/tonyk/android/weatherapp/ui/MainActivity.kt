package com.tonyk.android.weatherapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.util.LocationService
import com.tonyk.android.weatherapp.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedViewModel.getSavedLocations()
        if (LocationService.isLocationPermissionGranted(this)) {
            loadData()
        } else {
            LocationService.requestLocationPermission(this) { success ->
                if (success) {
                    loadData()
                } else {
                    sharedViewModel.loadLast()
                }
            }
        }
    }

    private fun loadData() {
        if (LocationService.isGPSEnabled(this)) {
            LocationService.getLocationData(this) { coordinates, address ->
                sharedViewModel.loadWeatherResult(coordinates, address)
            }
        }
        else {
            sharedViewModel.loadLast()
            LocationService.showGPSAlertDialog(this)
        }
    }
}