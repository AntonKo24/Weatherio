package com.tonyk.android.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.TodayWeatherAdapter
import com.tonyk.android.weatherapp.databinding.FragmentTodayBinding
import com.tonyk.android.weatherapp.util.DateConverter
import com.tonyk.android.weatherapp.util.WeatherConverter
import com.tonyk.android.weatherapp.util.WeatherIconMapper
import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch


abstract class BaseWeatherFragment : Fragment() {


    private var _binding: FragmentTodayBinding? = null
    protected val binding: FragmentTodayBinding
        get() = checkNotNull(_binding)
    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentTodayBinding
    abstract fun getWeatherViewModel(): WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeWeatherData(getWeatherViewModel())
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun observeWeatherData(weatherViewModel: WeatherViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weather.collect { it ->
                    binding.apply {
                        locationTxt.text = it.location.address
                        currentConditions.text = it.weather.currentConditions.conditions
                        currentTempTxt.text = getString(
                            R.string.Temperature,
                            WeatherConverter.formatData(it.weather.currentConditions.temp)
                        )
                        currentWindspeedTxt.text = getString(
                            R.string.WindspeedData,
                            WeatherConverter.formatData(it.weather.currentConditions.windspeed)
                        )
                        currentHumidityTxt.text = getString(
                            R.string.HumidityData,
                            WeatherConverter.formatData(it.weather.currentConditions.humidity)
                        )
                        currentPressure.text = getString(
                            R.string.PressureData,
                            WeatherConverter.formatData(it.weather.currentConditions.pressure)
                        )
                        descriptionTxt.text = it.weather.description
                        todayWeatherPic.load(WeatherIconMapper.getIconResourceId(it.weather.currentConditions.icon))
                        if (it.weather.days.isNotEmpty()) {
                            rcvHourly.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            rcvHourly.adapter = TodayWeatherAdapter(weatherViewModel.hoursList)
                            todayDateTxt.text = DateConverter.formatDateToFull(it.weather.days[0].datetime)
                            hlTempTxt.text = getString(
                                R.string.High_Low_temp,
                                WeatherConverter.formatData(it.weather.days[0].tempmax),
                                WeatherConverter.formatData(it.weather.days[0].tempmin)
                            )
                        }
                    }
                }
            }
        }
    }
}



