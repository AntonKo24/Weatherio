package com.tonyk.android.weatherapp.ui

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.tonyk.android.weatherapp.R
import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import com.tonyk.android.weatherapp.TodayWeatherAdapter
import com.tonyk.android.weatherapp.databinding.FragmentTodayBinding
import com.tonyk.android.weatherapp.util.DateConverter
import com.tonyk.android.weatherapp.util.LocationUtils
import com.tonyk.android.weatherapp.util.WeatherConverter
import com.tonyk.android.weatherapp.util.WeatherIconMapper
import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel
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



        if (LocationUtils.isLocationPermissionGranted(requireContext())) {
            if (LocationUtils.isGPSEnabled(requireContext())) {
                weatherViewModel.retrieveLocation()
            } else {
                Toast.makeText(context, "GPS is not turned on", Toast.LENGTH_LONG).show()
            }
        } else {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    if (LocationUtils.isGPSEnabled(requireContext())) {
                        weatherViewModel.retrieveLocation()
                    } else {
                        Toast.makeText(context, "GPS is not turned on", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "LOCATION PERMISSION IS NOT GRANTED", Toast.LENGTH_LONG).show()
                }
            }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }




        binding.rcvHourly.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

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
                        currentTempTxt.text = getString(R.string.Temperature, WeatherConverter.formatData(weather.currentConditions.temp))
                        currentWindspeedTxt.text = getString(R.string.WindspeedData,WeatherConverter.formatData(weather.currentConditions.windspeed))
                        currentHumidityTxt.text = getString(R.string.HumidityData, WeatherConverter.formatData(weather.currentConditions.humidity))
                        currentPressure.text = getString(R.string.PressureData, WeatherConverter.formatData(weather.currentConditions.pressure))
                        descriptionTxt.text = weather.description
                        todayWeatherPic.load(WeatherIconMapper.getIconResourceId(weather.currentConditions.icon))
                        if (weather.days.isNotEmpty()) {
                            rcvHourly.adapter = TodayWeatherAdapter(weatherViewModel.hoursList)
                            todayDateTxt.text = DateConverter.formatDate(weather.days[0].datetime)
                            hlTempTxt.text = getString(
                                R.string.High_Low_temp,
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