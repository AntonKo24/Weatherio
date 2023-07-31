package com.tonyk.android.weatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.databinding.FragmentWeatherBinding
import com.tonyk.android.weatherapp.model.Location
import com.tonyk.android.weatherapp.viewmodel.SearchResultViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchPageWeatherFragment: BaseWeatherScreenFragment() {

    private val searchWeatherViewModel : SearchResultViewModel by viewModels()
    private val args: SearchPageWeatherFragmentArgs by navArgs()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentWeatherBinding {
        return FragmentWeatherBinding.inflate(inflater, container, false)
    }
    override fun getWeatherViewModel(): SearchResultViewModel {
        return searchWeatherViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeErrorState(getWeatherViewModel())

        searchWeatherViewModel.getSearchResult(args.coordinates, args.address)

        binding.checkButton.load(R.drawable.ic_add)
        binding.manageLocations.load(R.drawable.back)

        binding.checkForecastBtn.setOnClickListener {
            if (searchWeatherViewModel.weatherio.value.weather.days.isNotEmpty()) {
            findNavController().navigate(
                SearchPageWeatherFragmentDirections.searchForecast(
                    searchWeatherViewModel.weatherio.value
                )
            ) }
        }
        binding.manageLocations.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.checkButton.setOnClickListener {
            searchWeatherViewModel.addLocation(Location(args.coordinates, args.address, args.listSize))
            findNavController().popBackStack()
        }
    }
}