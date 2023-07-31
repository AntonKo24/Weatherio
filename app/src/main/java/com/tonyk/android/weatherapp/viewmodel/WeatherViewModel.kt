package com.tonyk.android.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonyk.android.weatherapp.model.CurrentConditions
import com.tonyk.android.weatherapp.model.HourlyForecast
import com.tonyk.android.weatherapp.model.WeatherResponse
import com.tonyk.android.weatherapp.model.Location
import com.tonyk.android.weatherapp.model.Weatherio
import com.tonyk.android.weatherapp.database.LocationsDatabaseRepository
import com.tonyk.android.weatherapp.api.WeatherApiRepository
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
                                           private val locationsDatabase : LocationsDatabaseRepository
) : ViewModel() {

    private val _weatherio: MutableStateFlow<Weatherio> = MutableStateFlow(Weatherio(
        WeatherResponse("", emptyList(), CurrentConditions("", 0.0, 0.0, 0.0, "", 0.0, ""), ""), Location("", "", 0)
    ))
    val weatherio: StateFlow<Weatherio> = _weatherio

    private val _hoursList = mutableListOf<HourlyForecast>()
    val hoursList: List<HourlyForecast> get() = _hoursList

    protected val _errorState: MutableSharedFlow<String> = MutableSharedFlow()
    val errorState: SharedFlow<String> = _errorState


    private val _locationsList: MutableStateFlow<List<Weatherio>> = MutableStateFlow(emptyList())
    val locationsList: StateFlow<List<Weatherio>> = _locationsList

    fun getSavedLocations() {
        viewModelScope.launch {
            try {
                locationsDatabase.getLocations().collect { newLocations ->
                    val existingLocations = _locationsList.value.map { it.location.coordinates }.toSet()
                    newLocations.forEach { newLocationItem ->
                        if (!existingLocations.contains(newLocationItem.coordinates)) {
                            val weather = weatherApiRepository.fetchWeather(newLocationItem.coordinates)
                            _locationsList.value = _locationsList.value + Weatherio(weather, newLocationItem)
                        }
                    }
                }
            } catch (ex: Exception) {
                _errorState.emit("Failed to get saved locations: ${ex.message}")
            }
        }
    }

    fun updateViewModelList(dataList: List<Weatherio>) {
        _locationsList.value = dataList
    }

    fun updateLocationsPosition(dataList: List<Weatherio>) {
        viewModelScope.launch {
            val locationList = dataList?.map { it.location }
            locationList?.let { list ->
                val new = list.mapIndexed { index, locationItem ->
                    locationItem.copy(position = index)
                }
                new.forEach { locationItem ->
                        locationsDatabase.updateLocation(locationItem)
                }
            }
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            locationsDatabase.deleteLocation(location)
            _locationsList.value = _locationsList.value.filter { it.location != location }
        }
    }
    fun loadLast() {
        viewModelScope.launch {
            try {
                _locationsList.filter { it.isNotEmpty() }.first().let { locations ->
                    setWeather(locations.first())
                }
            } catch (ex: Exception) {
                _errorState.emit("Failed to load last location: ${ex.message}")
            }
        }
    }
    fun initializeWeatherViewModel(coordinates: String, address: String) {
        viewModelScope.launch {
            try {
                val weather = weatherApiRepository.fetchWeather(coordinates)
                setWeather(Weatherio(weather, Location(coordinates, address, -1)))
            } catch (ex: Exception) {
                _errorState.emit("Failed to fetch weather data: ${ex.message}")
            }
        }
    }
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
    fun addLocation(location : Location) {
        viewModelScope.launch {
            locationsDatabase.addLocation(location)
        }
    }
}
