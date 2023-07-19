package com.tonyk.android.weatherapp.ui

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
import com.tonyk.android.weatherapp.ForecastWeatherAdapter
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.databinding.FragmentForecastBinding
import com.tonyk.android.weatherapp.util.WeatherConverter
import com.tonyk.android.weatherapp.util.WeatherIconMapper
import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForecastFragment: Fragment() {
    private var _binding: FragmentForecastBinding? = null
    private val binding
        get () = checkNotNull(_binding)

    private val weatherViewModel : WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentForecastBinding.inflate(inflater, container, false)

        return binding.root }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rcvForecast.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weather.collect { weather ->
                    binding.apply {
                        locationText.text = weather.resolvedAddress
                        if (weather.days.isNotEmpty()) {
                            tmrwTemp.text = getString(
                                R.string.High_Low_temp,
                                WeatherConverter.formatData(weather.days[1].tempmax),
                                WeatherConverter.formatData(weather.days[1].tempmin))
                            tmrwCondText.text = weather.days[1].conditions
                            tmrwHumidity.text = getString(R.string.HumidityData, WeatherConverter.formatData(weather.days[1].humidity))
                            tmrwPressure.text = getString(R.string.PressureData, WeatherConverter.formatData(weather.days[1].pressure))
                            tmrwWindspeed.text = getString(R.string.WindspeedData, WeatherConverter.formatData(weather.days[1].windspeed))
                            tmrwPic.load(WeatherIconMapper.getIconResourceId(weather.days[1].icon))

                            rcvForecast.adapter = ForecastWeatherAdapter(weather.days)
                        }
                    }

                }
            }
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}