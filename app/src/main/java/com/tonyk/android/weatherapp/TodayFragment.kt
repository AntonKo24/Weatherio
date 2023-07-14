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
import com.tonyk.android.weatherapp.util.WeatherIconMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat

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

        val rcView = binding.rcvHourly
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rcView.layoutManager = layoutManager


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weather.collect { items ->

                    binding.apply {
                        locationTxt.text = items.address
                        currentConditions.text = items.currentConditions.conditions

                        currentTempTxt.text = items.currentConditions.temp
                        currentWindspeedTxt.text = items.currentConditions.windspeed
                        currentHumidityTxt.text = items.currentConditions.humidity
                        currentPrecipprob.text = items.currentConditions.precipprob
                        descriptionTxt.text = items.description

                        val iconName = items.currentConditions.icon
                        val iconResourceId = WeatherIconMapper.getIconResourceId(iconName)
                        todayWeatherPic.load(iconResourceId)

                        if (items.days.isNotEmpty()) {
                            rcvHourly.adapter = TodayWeatherAdapter(items.days[0].hours)
                            todayDateTxt.text = items.days[0].datetime
                            hlTempTxt.text = "H: ${items.days[0].tempmax} L: ${items.days[0].tempmin}"
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