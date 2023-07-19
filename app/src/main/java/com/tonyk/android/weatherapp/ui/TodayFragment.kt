package com.tonyk.android.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.TodayWeatherAdapter
import com.tonyk.android.weatherapp.databinding.FragmentTodayBinding
import com.tonyk.android.weatherapp.util.DateConverter
import com.tonyk.android.weatherapp.util.Permissions
import com.tonyk.android.weatherapp.util.Permissions.requestLocationPermission

import com.tonyk.android.weatherapp.util.Permissions.retrieveGPSLocation
import com.tonyk.android.weatherapp.util.Permissions.showToast
import com.tonyk.android.weatherapp.util.WeatherConverter
import com.tonyk.android.weatherapp.util.WeatherIconMapper
import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TodayFragment: Fragment() {
    private var _binding: FragmentTodayBinding? = null
    private val binding
        get () = checkNotNull(_binding)

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

        weatherViewModel.startedFrag(requireActivity())

        binding.rcvHourly.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            binding.apply {

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
                            currentTempTxt.text = getString(
                                R.string.Temperature,
                                WeatherConverter.formatData(weather.currentConditions.temp)
                            )
                            currentWindspeedTxt.text = getString(
                                R.string.WindspeedData,
                                WeatherConverter.formatData(weather.currentConditions.windspeed)
                            )
                            currentHumidityTxt.text = getString(
                                R.string.HumidityData,
                                WeatherConverter.formatData(weather.currentConditions.humidity)
                            )
                            currentPressure.text = getString(
                                R.string.PressureData,
                                WeatherConverter.formatData(weather.currentConditions.pressure)
                            )
                            descriptionTxt.text = weather.description
                            todayWeatherPic.load(WeatherIconMapper.getIconResourceId(weather.currentConditions.icon))
                            if (weather.days.isNotEmpty()) {
                                rcvHourly.adapter = TodayWeatherAdapter(weatherViewModel.hoursList)
                                todayDateTxt.text =
                                    DateConverter.formatDate(weather.days[0].datetime)
                                hlTempTxt.text = getString(
                                    R.string.High_Low_temp,
                                    WeatherConverter.formatData(weather.days[0].tempmax),
                                    WeatherConverter.formatData(weather.days[0].tempmin)
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