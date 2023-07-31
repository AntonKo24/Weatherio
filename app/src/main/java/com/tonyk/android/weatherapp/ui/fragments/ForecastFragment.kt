package com.tonyk.android.weatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.tonyk.android.weatherapp.ui.adapters.ForecastWeatherAdapter
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

    private val forecastWeatherViewModel : WeatherViewModel by viewModels()
    private val args: ForecastFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentForecastBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forecastWeatherViewModel.setWeather(args.weather)
        binding.rcvForecast.layoutManager = LinearLayoutManager(context)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                forecastWeatherViewModel.weatherioItem.collect { it ->
                    binding.apply {
                        locationText.text = it.location.address
                        if (it.weather.days.isNotEmpty()) {
                            tmrwTemp.text = getString(
                                R.string.High_Low_temp,
                                WeatherConverter.formatData(it.weather.days[1].tempmax),
                                WeatherConverter.formatData(it.weather.days[1].tempmin))
                            tmrwCondText.text = it.weather.days[1].conditions
                            tmrwHumidity.text = getString(R.string.HumidityData, WeatherConverter.formatData(it.weather.days[1].humidity))
                            tmrwPressure.text = getString(R.string.PressureData, WeatherConverter.formatData(it.weather.days[1].pressure))
                            tmrwWindspeed.text = getString(R.string.WindspeedData, WeatherConverter.formatData(it.weather.days[1].windspeed))
                            tmrwPic.load(WeatherIconMapper.getIconResourceId(it.weather.days[1].icon))
                            rcvForecast.adapter = ForecastWeatherAdapter(it.weather.days)
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