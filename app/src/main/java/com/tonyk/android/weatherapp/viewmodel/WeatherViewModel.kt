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
import com.tonyk.android.weatherapp.database.LocationRepository
import com.tonyk.android.weatherapp.repositories.WeatherApiRepository
import com.tonyk.android.weatherapp.util.LocationService

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
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

    private val _locations: MutableStateFlow<List<LocationItem>> = MutableStateFlow(emptyList())
    val locations: StateFlow<List<LocationItem>>
        get() = _locations.asStateFlow()

    init {
        viewModelScope.launch {
            locationRepository.getLocations().collect {
               it.forEach { val weather = weatherApiRepository.fetchWeather(it.coordinates)
                   val location = it
                   _locationsList.value = _locationsList.value + WeatherioItem(weather,location)  }

            }

        }
    }

    private suspend fun addLocation(location: LocationItem) {
        locationRepository.addLocation(location) }






    fun initializeWeatherViewModel(location : LocationItem) {
        viewModelScope.launch {
            try {
                val weather = weatherApiRepository.fetchWeather(location.coordinates)
                _weather.value = WeatherioItem(weather, location)
                processHourlyForecast(weather)
            }
            catch (ex: Exception) {
                Log.d("Exception", "$ex")
            }
        }
    }

    private fun processHourlyForecast(weatherData: WeatherResponse) {
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
        initializeWeatherViewModel(LocationItem("London", "London"))
    }
    private fun loadCurrent(activity: FragmentActivity){
        LocationService.getLocationData(activity) { it ->
            initializeWeatherViewModel(it)
        }
    }
    fun startFragment(activity: FragmentActivity) {
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

    fun setQuery(location: LocationItem) {
        viewModelScope.launch {
            try {
                addLocation(location)
                val newLocation = WeatherioItem(weatherApiRepository.fetchWeather(location.coordinates), location)
                _locationsList.value = _locationsList.value + newLocation
            } catch (e: Exception) {
                _errorState.emit("$e")
            }
        }
    }





}
