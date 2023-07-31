package com.tonyk.android.weatherapp.viewmodel

import androidx.lifecycle.viewModelScope
import com.tonyk.android.weatherapp.api.WeatherApiRepository
import com.tonyk.android.weatherapp.database.LocationsDatabaseRepository
import com.tonyk.android.weatherapp.model.Location
import com.tonyk.android.weatherapp.model.Weatherio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel@Inject constructor(private val weatherApiRepository: WeatherApiRepository,
                                               private val locationsDatabase : LocationsDatabaseRepository
) : BaseViewModel()  {

    fun addLocation(location : Location) {
        viewModelScope.launch {
            locationsDatabase.addLocation(location)
        }
    }
    fun getSearchResult(coordinates: String, address: String) {
        viewModelScope.launch {
            try {
                val weather = weatherApiRepository.fetchWeather(coordinates)
                setWeather(Weatherio(weather, Location(coordinates, address, -1)))
            } catch (ex: Exception) {
                _errorState.emit("Failed to fetch weather data: ${ex.message}")
            }
        }
    }
}