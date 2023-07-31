package com.tonyk.android.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import com.tonyk.android.weatherapp.model.CurrentConditions
import com.tonyk.android.weatherapp.model.HourlyForecast
import com.tonyk.android.weatherapp.model.Location
import com.tonyk.android.weatherapp.model.WeatherResponse
import com.tonyk.android.weatherapp.model.Weatherio
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalTime

open class BaseViewModel : ViewModel() {

    private val _weatherio: MutableStateFlow<Weatherio> = MutableStateFlow(Weatherio(WeatherResponse("", emptyList(), CurrentConditions("", 0.0, 0.0, 0.0, "", 0.0, ""), ""), Location("", "", 0)))
    val weatherio: StateFlow<Weatherio> = _weatherio

    private val _hoursList = mutableListOf<HourlyForecast>()
    val hoursList: List<HourlyForecast> get() = _hoursList

    protected val _errorState: MutableSharedFlow<String> = MutableSharedFlow()
    val errorState: SharedFlow<String> = _errorState

    private fun processHourlyForecast(weatherData: WeatherResponse) {
        _hoursList.clear()
        val hoursToAdd = mutableListOf<HourlyForecast>()
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

    fun setWeather(weather: Weatherio) {
        processHourlyForecast(weather.weather)
        _weatherio.value = weather
    }
}