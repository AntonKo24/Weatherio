package com.tonyk.android.weatherapp.ui



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
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.TodayWeatherAdapter
import com.tonyk.android.weatherapp.data.LocationItem
import com.tonyk.android.weatherapp.databinding.FragmentTodayBinding
import com.tonyk.android.weatherapp.util.DateConverter

import com.tonyk.android.weatherapp.util.WeatherConverter
import com.tonyk.android.weatherapp.util.WeatherIconMapper
import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchDetailsFragment: Fragment() {
    private var _binding: FragmentTodayBinding? = null
    private val binding
        get () = checkNotNull(_binding)

    private val weatherViewModel : WeatherViewModel by viewModels()
    private val args: SearchDetailsFragmentArgs by navArgs()


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

        weatherViewModel.initializeWeatherViewModel(LocationItem(args.location, args.address))

        binding.rcvHourly.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)



        binding.checkForecastBtn.setOnClickListener {
            findNavController().navigate(TodayFragmentDirections.showForecast())
        }
        binding.manageLocations.setOnClickListener {
            findNavController().popBackStack()
        }


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
                            rcvHourly.adapter = TodayWeatherAdapter(weatherViewModel.hoursList)
                            todayDateTxt.text =
                                DateConverter.formatDate(it.weather.days[0].datetime)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}