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
import com.tonyk.android.weatherapp.databinding.FragmentForecastBinding
import com.tonyk.android.weatherapp.util.WeatherConverter
import com.tonyk.android.weatherapp.util.WeatherIconMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForecastFragment: Fragment() {
    private var _binding: FragmentForecastBinding? = null
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
            FragmentForecastBinding.inflate(inflater, container, false)

        return binding.root }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val layoutManager = LinearLayoutManager(context)
        binding.rcvForecast.layoutManager = layoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weather.collect { items ->

                    binding.apply {
                        locationText.text = items.address
                        if (items.days.isNotEmpty()) {
                            tmrwTemp.text = getString(R.string.High_Low_temp,
                                WeatherConverter.formatData(items.days[1].tempmax),
                                WeatherConverter.formatData(items.days[1].tempmin))
                            tmrwCondText.text = items.days[1].conditions
                            tmrwHumidity.text = getString(R.string.HumidityData, WeatherConverter.formatData(items.days[1].humidity))
                            tmrwPrecipProb.text = getString(R.string.PressureData, WeatherConverter.formatData(items.days[1].pressure))
                            tmrwWindspeed.text = getString(R.string.WindspeedData, WeatherConverter.formatData(items.days[1].windspeed))
                            tmrwPic.load(WeatherIconMapper.getIconResourceId(items.days[1].icon))

                            rcvForecast.adapter = ForecastWeatherAdapter(items.days)
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