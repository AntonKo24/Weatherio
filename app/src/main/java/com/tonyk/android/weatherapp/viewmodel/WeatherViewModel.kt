package com.tonyk.android.weatherapp.viewmodel

import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonyk.android.weatherapp.api.CurrentWeatherItem
import com.tonyk.android.weatherapp.api.HourlyWeatherItem
import com.tonyk.android.weatherapp.api.WeatherResponse
import com.tonyk.android.weatherapp.data.LocationItem
import com.tonyk.android.weatherapp.data.WeatherioItem
import com.tonyk.android.weatherapp.database.LocationRepository
import com.tonyk.android.weatherapp.repositories.WeatherApiRepository
import com.tonyk.android.weatherapp.util.LocationService


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherApiRepository: WeatherApiRepository

) : ViewModel() {

    private val _weather: MutableStateFlow<WeatherioItem> = MutableStateFlow(WeatherioItem(WeatherResponse("", emptyList(), CurrentWeatherItem("", 0.0, 0.0, 0.0, "", 0.0, ""), ""), LocationItem("", "")))
    val weather: StateFlow<WeatherioItem> = _weather

    private val _hoursList = mutableListOf<HourlyWeatherItem>()
    val hoursList: List<HourlyWeatherItem> get() = _hoursList

    private val _locationsList: MutableStateFlow<List<WeatherioItem>> = MutableStateFlow(emptyList())
    val locationsList: StateFlow<List<WeatherioItem>> = _locationsList

    private val _errorState: MutableSharedFlow<String> = MutableSharedFlow()
    val errorState: SharedFlow<String> = _errorState

    private val locationRepository = LocationRepository.get()



    private fun getList() {
        viewModelScope.launch {
            locationRepository.getLocations().collect { newLocations ->

                val existingLocations = _locationsList.value.map { it.location }.toSet()
                newLocations.forEach { newLocationItem ->
                    if (!existingLocations.contains(newLocationItem)) {
                        val weather = weatherApiRepository.fetchWeather(newLocationItem.coordinates)
                        _locationsList.value = _locationsList.value + WeatherioItem(weather, newLocationItem)
                    }
                }
            }
        }
    }

    fun setWeather(weather : WeatherioItem) {
        processHourlyForecast(weather.weather)
        _weather.value = weather
    }


    fun addLocation(location: LocationItem) {
        viewModelScope.launch {
            locationRepository.addLocation(location)
        }
    }

    fun deleteLocation(location: LocationItem) {
        viewModelScope.launch {
            locationRepository.deleteLocation(location)
            _locationsList.value = _locationsList.value.filter { it.location != location }
        }
    }

    fun initializeWeatherViewModel(location : LocationItem) {
        viewModelScope.launch {
            try {
                val weather = weatherApiRepository.fetchWeather(location.coordinates)
                setWeather(WeatherioItem(weather, location))
            }
            catch (ex: Exception) {
                Log.d("Exception", "$ex")
            }
        }
    }

    private fun processHourlyForecast(weatherData: WeatherResponse) {
        _hoursList.clear()
        val hoursToAdd = mutableListOf<HourlyWeatherItem>()
        var remainingHours = 24
        var dayIndex = 0
        var hourIndex = weatherData.days[0].hours.indexOfFirst {
            LocalTime.parse(it.datetime) >= LocalTime.parse(weatherData.currentConditions.datetime)
        }
        if (hourIndex == -1) {
            hourIndex = 0
        }
        while (remainingHours > 0 && dayIndex < weatherData.days.size) {
            val dayHours = weatherData.days[dayIndex].hours
            val hoursToCopy = minOf(remainingHours, dayHours.size - hourIndex)
            hoursToAdd.addAll(dayHours.subList(hourIndex, hourIndex + hoursToCopy))
            remainingHours -= hoursToCopy
            dayIndex++
            hourIndex = 0
        }
        _hoursList.addAll(hoursToAdd)
    }


    private fun loadLast() {
        viewModelScope.launch {
            _locationsList.filter { it.isNotEmpty() }.first().let { locations ->
                    setWeather(locations.first())
            }
        }
    }

     fun loadCurrent(activity: FragmentActivity){
        LocationService.getLocationData(activity) {
            initializeWeatherViewModel(it)
        }
    }

    fun startFragment(activity: FragmentActivity) {
        getList()
        if (LocationService.isLocationPermissionGranted(activity)) {
            if (LocationService.isGPSEnabled(activity)) {
                loadCurrent(activity)
            } else {
                loadLast()
                LocationService.showGPSAlertDialog(activity)
            }
        } else {
            LocationService.requestLocationPermission(activity) { success ->
                if (success) {
                    if (LocationService.isGPSEnabled(activity)) {
                        loadCurrent(activity)
                    } else {
                        loadLast()
                        LocationService.showGPSAlertDialog(activity)
                    }
                } else {
                    loadLast()
                }
            }
        }
    }


}
