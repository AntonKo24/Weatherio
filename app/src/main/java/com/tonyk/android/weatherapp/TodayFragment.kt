package com.tonyk.android.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.tonyk.android.weatherapp.databinding.FragmentTodayBinding
import com.tonyk.android.weatherapp.util.DateConverter
import com.tonyk.android.weatherapp.util.WeatherConverter
import com.tonyk.android.weatherapp.util.WeatherIconMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TodayFragment: Fragment() {
    private var _binding: FragmentTodayBinding? = null
    private val binding
        get () = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val weatherViewModel : WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentTodayBinding.inflate(inflater, container, false)

        return binding.root }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rcvHourly.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            checkForecastBtn.setOnClickListener {
                findNavController().navigate(TodayFragmentDirections.showForecast())
            }
            manageLocations.setOnClickListener {
                findNavController().navigate(TodayFragmentDirections.manageLocations())
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weather.collect { weather ->

                    binding.apply {
                        locationTxt.text = weather.resolvedAddress
                        currentConditions.text = weather.currentConditions.conditions
                        currentTempTxt.text = getString(R.string.Temperature, WeatherConverter.formatData(weather.currentConditions.temp))
                        currentWindspeedTxt.text = getString(R.string.WindspeedData,WeatherConverter.formatData(weather.currentConditions.windspeed))
                        currentHumidityTxt.text = getString(R.string.HumidityData, WeatherConverter.formatData(weather.currentConditions.humidity))
                        currentPressure.text = getString(R.string.PressureData, WeatherConverter.formatData(weather.currentConditions.pressure))
                        descriptionTxt.text = weather.description
                        todayWeatherPic.load(WeatherIconMapper.getIconResourceId(weather.currentConditions.icon))
                        if (weather.days.isNotEmpty()) {
                            rcvHourly.adapter = TodayWeatherAdapter(weatherViewModel.hoursList)
                            todayDateTxt.text = DateConverter.formatDate(weather.days[0].datetime)
                            hlTempTxt.text = getString(R.string.High_Low_temp,
                                WeatherConverter.formatData(weather.days[0].tempmax),
                                WeatherConverter.formatData(weather.days[0].tempmin))
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}