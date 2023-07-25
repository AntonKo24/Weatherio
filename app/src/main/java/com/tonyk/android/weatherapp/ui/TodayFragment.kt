package com.tonyk.android.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope

import androidx.navigation.fragment.findNavController

import com.tonyk.android.weatherapp.databinding.FragmentTodayBinding
import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TodayFragment : BaseFragment() {

    private val todayWeatherViewModel: WeatherViewModel by activityViewModels()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentTodayBinding {
        return FragmentTodayBinding.inflate(inflater, container, false)
    }
    override fun getWeatherViewModel(): WeatherViewModel {
        return todayWeatherViewModel
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkButton.setOnClickListener {
            lifecycleScope.launch {
                binding.checkButton.visibility = View.GONE
                todayWeatherViewModel.loadCurrent(requireActivity())
                delay(3000)
                binding.checkButton.visibility = View.VISIBLE
            }

        }

        binding.checkForecastBtn.setOnClickListener {
            findNavController().navigate(TodayFragmentDirections.showForecast())
        }
        binding.manageLocations.setOnClickListener {
            findNavController().navigate(TodayFragmentDirections.manageLocations())
        }
    }
}




