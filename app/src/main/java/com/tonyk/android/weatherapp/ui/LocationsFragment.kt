package com.tonyk.android.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.tonyk.android.weatherapp.databinding.FragmentLocationsBinding
import com.tonyk.android.weatherapp.viewmodel.LocationsViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationsFragment: Fragment() {
    private var _binding: FragmentLocationsBinding? = null
    private val binding
        get () = checkNotNull(_binding)

    private val locationsViewModel : LocationsViewModel by viewModels()
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
                locationsViewModel.locationsList.collect { list ->
                    if (list.isNotEmpty()) {
                        binding.rcvLocations.adapter = LocationsAdapter(list)
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
                findNavController().navigate(LocationsFragmentDirections.check(coordinates, placeName))
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
