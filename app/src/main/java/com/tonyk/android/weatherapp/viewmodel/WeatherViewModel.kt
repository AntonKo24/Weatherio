package com.tonyk.android.weatherapp.viewmodel

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonyk.android.weatherapp.api.CurrentWeatherItem
import com.tonyk.android.weatherapp.api.HourlyWeatherItem
import com.tonyk.android.weatherapp.api.WeatherResponse
import com.tonyk.android.weatherapp.repositories.WeatherApiRepository
import com.tonyk.android.weatherapp.util.LocationService

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherApiRepository: WeatherApiRepository

) : ViewModel() {

    private val _weather: MutableStateFlow<WeatherResponse> = MutableStateFlow(WeatherResponse("", emptyList(), CurrentWeatherItem("", 0.0, 0.0, 0.0, "", 0.0, ""), ""))
    val weather: StateFlow<WeatherResponse> = _weather.asStateFlow()

    private val _hoursList = mutableListOf<HourlyWeatherItem>()
    val hoursList: List<HourlyWeatherItem> get() = _hoursList

    private fun initializeWeatherViewModel(location : String) {
        viewModelScope.launch {
            try {
                val items = weatherApiRepository.fetchWeather(location)
                _weather.value = items
                processHourlyForecast(items)
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
        initializeWeatherViewModel("london")
    }
    private fun loadCurrent(activity: FragmentActivity){
        LocationService.getLocation(activity) { coordinates ->
            initializeWeatherViewModel(coordinates)
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
}