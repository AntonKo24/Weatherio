package com.tonyk.android.weatherapp.viewmodel

import android.util.Log

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonyk.android.weatherapp.api.CurrentWeatherItem
import com.tonyk.android.weatherapp.api.HourlyWeatherItem
import com.tonyk.android.weatherapp.api.WeatherResponse
import com.tonyk.android.weatherapp.data.LocationItem
import com.tonyk.android.weatherapp.data.WeatherioItem
import com.tonyk.android.weatherapp.database.LocationsDatabaseRepository
import com.tonyk.android.weatherapp.database.LocationsDatabaseRepositoryImpl
import com.tonyk.android.weatherapp.repositories.WeatherApiRepository
import com.tonyk.android.weatherapp.util.LocationService


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherApiRepository: WeatherApiRepository,
                                           private val locationsDB : LocationsDatabaseRepository

) : ViewModel() {

    private val _weather: MutableStateFlow<WeatherioItem> = MutableStateFlow(WeatherioItem(WeatherResponse("", emptyList(), CurrentWeatherItem("", 0.0, 0.0, 0.0, "", 0.0, ""), ""), LocationItem("", "", 0)))
    val weather: StateFlow<WeatherioItem> = _weather

    private val _hoursList = mutableListOf<HourlyWeatherItem>()
    val hoursList: List<HourlyWeatherItem> get() = _hoursList

    private val _locationsList: MutableStateFlow<List<WeatherioItem>> = MutableStateFlow(emptyList())
    val locationsList: StateFlow<List<WeatherioItem>> = _locationsList

    private val _errorState: MutableSharedFlow<String> = MutableSharedFlow()
    val errorState: SharedFlow<String> = _errorState





     private fun getList() {
        viewModelScope.launch {
            locationsDB.getLocations().collect { newLocations ->
                val existingLocations = _locationsList.value.map { it.location.coordinates }.toSet()
                newLocations.forEach { newLocationItem ->
                    if (!existingLocations.contains(newLocationItem.coordinates)) {
                        val weather = weatherApiRepository.fetchWeather(newLocationItem.coordinates)
                        _locationsList.value = _locationsList.value + WeatherioItem(weather, newLocationItem)
                    }
                }
            }
        }
    }


    fun updateLocations(dataList: List<WeatherioItem>) {
        viewModelScope.launch {
            _locationsList.value = dataList
            val locationList = dataList?.map { it.location }
            locationList?.let { list ->
                val new = list.mapIndexed { index, locationItem ->
                    locationItem.copy(position = index)
                }
                new.forEach { locationItem ->
                        locationsDB.updateLocation(locationItem)
                }
            }
        }
    }

    fun setWeather(weather : WeatherioItem) {
        processHourlyForecast(weather.weather)
        _weather.value = weather
    }


    fun addLocation(coordinates : String, address: String) {
        viewModelScope.launch {
            locationsDB.addLocation(LocationItem(coordinates, address, locationsList.value.size))
        }
    }

    fun deleteLocation(location: LocationItem) {
        viewModelScope.launch {
            locationsDB.deleteLocation(location)
            _locationsList.value = _locationsList.value.filter { it.location != location }
        }
    }

    fun initializeWeatherViewModel(coordinates: String, address: String) {
        viewModelScope.launch {
            try {
                val weather = weatherApiRepository.fetchWeather(coordinates)
                setWeather(WeatherioItem(weather, LocationItem(coordinates, address, -1)))
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
        if (LocationService.isGPSEnabled(activity)) {
            LocationService.getLocationData(activity) { coordinates, address ->
                initializeWeatherViewModel(coordinates, address)
            }
        }
        else {
            loadLast()
            LocationService.showGPSAlertDialog(activity)
        }
    }

    fun startFragment(activity: FragmentActivity) {
        getList()
        if (LocationService.isLocationPermissionGranted(activity)) {
            loadCurrent(activity)
        } else {
            LocationService.requestLocationPermission(activity) { success ->
                if (success) {
                    loadCurrent(activity)
                } else {
                    loadLast()
                }
            }
        }
    }
}
