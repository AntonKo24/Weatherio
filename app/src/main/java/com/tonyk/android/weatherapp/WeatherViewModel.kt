package com.tonyk.android.weatherapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonyk.android.weatherapp.api.CurrentWeatherItem
import com.tonyk.android.weatherapp.api.WeatherResponse
import com.tonyk.android.weatherapp.repositories.WeatherApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherApiRepository: WeatherApiRepository) : ViewModel() {

    private val _weather: MutableStateFlow<WeatherResponse> = MutableStateFlow(WeatherResponse("", emptyList(), CurrentWeatherItem("", "","", "", "", ""), ""))
    val weather: StateFlow<WeatherResponse>
        get() = _weather.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val items = weatherApiRepository.fetchWeather()
                _weather.value = items
                Log.d("yoyoyo", "Items received: $items")

            } catch (ex: Exception) {
                Log.e("yoyoyo", "Failed to fetch gallery items", ex)
            }
        }
    }
}