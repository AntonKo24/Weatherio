package com.tonyk.android.weatherapp.ui



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.databinding.FragmentTodayBinding

import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchPageWeatherFragment: BaseWeatherFragment() {

    private val searchWeatherViewModel : WeatherViewModel by viewModels()
    private val args: WeatherDetailFromSearchFragmentArgs by navArgs()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentTodayBinding {
        return FragmentTodayBinding.inflate(inflater, container, false)
    }
    override fun getWeatherViewModel(): WeatherViewModel {
        return searchWeatherViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchWeatherViewModel.initializeWeatherViewModel(args.location, args.address)

        binding.checkButton.load(R.drawable.ic_check)
        binding.manageLocations.load(R.drawable.back)

        binding.checkForecastBtn.setOnClickListener {
            findNavController().navigate(WeatherDetailFromSearchFragmentDirections.searchForecast(searchWeatherViewModel.weather.value))
        }
        binding.manageLocations.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.checkButton.setOnClickListener {
            searchWeatherViewModel.addLocation(args.location, args.address)
            findNavController().popBackStack()
        }
    }
}