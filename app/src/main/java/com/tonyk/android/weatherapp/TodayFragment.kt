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

        binding.rcvHourly.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weather.collect { items ->

                    binding.apply {
                        locationTxt.text = items.address
                        currentConditions.text = items.currentConditions.conditions
                        currentTempTxt.text = getString(R.string.Temperature, WeatherConverter.formatData(items.currentConditions.temp))
                        currentWindspeedTxt.text = getString(R.string.WindspeedData,WeatherConverter.formatData(items.currentConditions.windspeed))
                        currentHumidityTxt.text = getString(R.string.HumidityData, WeatherConverter.formatData(items.currentConditions.humidity))
                        currentPrecipprob.text = getString(R.string.PressureData, WeatherConverter.formatData(items.currentConditions.pressure))
                        descriptionTxt.text = items.description
                        todayWeatherPic.load(WeatherIconMapper.getIconResourceId(items.currentConditions.icon))
                        if (items.days.isNotEmpty()) {
                            rcvHourly.adapter = TodayWeatherAdapter(items.days[0].hours)
                            todayDateTxt.text = DateConverter.formatDate(items.days[0].datetime)
                            hlTempTxt.text = getString(R.string.High_Low_temp,
                                WeatherConverter.formatData(items.days[0].tempmax),
                                WeatherConverter.formatData(items.days[0].tempmin))
                        }
                    }
                }
            }
        }
        binding.checkForecastBtn.setOnClickListener {
            findNavController().navigate(TodayFragmentDirections.showForecast())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}