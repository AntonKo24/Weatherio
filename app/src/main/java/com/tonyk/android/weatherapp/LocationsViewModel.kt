package com.tonyk.android.weatherapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonyk.android.weatherapp.api.WeatherResponse
import com.tonyk.android.weatherapp.repositories.WeatherApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(private val weatherApiRepository: WeatherApiRepository) : ViewModel() {

    private val _locationsList: MutableStateFlow<List<WeatherResponse>> = MutableStateFlow(emptyList())
    val locationsList: StateFlow<List<WeatherResponse>> = _locationsList

    fun setQuery(location: String) {
        viewModelScope.launch {
            val updatedList = _locationsList.value + weatherApiRepository.fetchWeather(location)
            _locationsList.value = updatedList
        }
    }
}


