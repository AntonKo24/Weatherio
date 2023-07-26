package com.tonyk.android.weatherapp.ui



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
class WeatherDetailFromSearchFragment: BaseWeatherFragment() {

    private val searchWeatherViewModel : WeatherViewModel by activityViewModels()
    private val args: WeatherDetailFromSearchFragmentArgs by navArgs()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentTodayBinding {
        return FragmentTodayBinding.inflate(inflater, container, false)
    }
    override fun getWeatherViewModel(): WeatherViewModel {
        return searchWeatherViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchWeatherViewModel.initializeWeatherViewModel(LocationItem(args.location, args.address, 0))

        binding.checkButton.load(R.drawable.ic_check)
        binding.manageLocations.load(R.drawable.back)

        binding.checkForecastBtn.setOnClickListener {
            findNavController().navigate(TodayWeatherFragmentDirections.showForecast())
        }
        binding.manageLocations.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.checkButton.setOnClickListener {
            searchWeatherViewModel.addLocation(LocationItem(args.location, args.address, (searchWeatherViewModel.size+1)))
            findNavController().popBackStack()
        }
    }
}