package com.tonyk.android.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.tonyk.android.weatherapp.LocationsAdapter
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.data.WeatherioItem
import com.tonyk.android.weatherapp.databinding.FragmentLocationsBinding
import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationsFragment: Fragment() {
    private var _binding: FragmentLocationsBinding? = null
    private val binding
        get () = checkNotNull(_binding)

    private val weatherViewModel : WeatherViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentLocationsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.rcvLocations.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.locationsList.collect {
                    binding.rcvLocations.adapter = LocationsAdapter(it) { item ->
                        weatherViewModel.initializeWeatherViewModel(item.weather.resolvedAddress, item.address)
                        findNavController().popBackStack()
                    }
                }
            }
        }
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocompleteFragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setTypesFilter(listOf(PlaceTypes.CITIES))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val coordinates = "${place.latLng?.latitude ?: 0.0},${place.latLng?.longitude ?: 0.0}"
                val placeName = place.name ?: "Not found"
                weatherViewModel.setQuery(coordinates, placeName)
            }
            override fun onError(status: Status) {
                Toast.makeText(requireContext(), "Autocomplete error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
