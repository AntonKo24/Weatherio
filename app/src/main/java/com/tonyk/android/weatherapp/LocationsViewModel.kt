package com.tonyk.android.weatherapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonyk.android.weatherapp.api.WeatherResponse
import com.tonyk.android.weatherapp.repositories.WeatherApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(private val weatherApiRepository: WeatherApiRepository) : ViewModel() {

    private val _locationsList: MutableStateFlow<List<WeatherResponse>> = MutableStateFlow(emptyList())
    val locationsList: StateFlow<List<WeatherResponse>> = _locationsList

    private val _errorState: MutableSharedFlow<String> = MutableSharedFlow()
    val errorState: SharedFlow<String> = _errorState


    fun setQuery(location: String) {
        viewModelScope.launch {
            try {
                _locationsList.value =
                    _locationsList.value + weatherApiRepository.fetchWeather(location)
            } catch (e: Exception) {
                _errorState.emit( "$e")
            }
        }
    }
}


