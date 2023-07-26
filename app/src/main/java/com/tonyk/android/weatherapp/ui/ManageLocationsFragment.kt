package com.tonyk.android.weatherapp.ui

import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.tonyk.android.weatherapp.LocationsAdapter
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.data.LocationItem
import com.tonyk.android.weatherapp.databinding.FragmentLocationsBinding
import com.tonyk.android.weatherapp.util.DragItemTouchHelperCallback
import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageLocationsFragment: Fragment() {
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

        val adapter = LocationsAdapter({ item ->
            weatherViewModel.setWeather(item)
            findNavController().popBackStack()
        }, { item ->
                weatherViewModel.deleteLocation(item.location)
        })

        binding.rcvLocations.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(DragItemTouchHelperCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.rcvLocations)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.locationsList.collect {
                    adapter.submitList(it)

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
                val address = place.name ?: ""

                if (weatherViewModel.locationsList.value.none { it.location == LocationItem(coordinates, address) }) {
                    findNavController().navigate(ManageLocationsFragmentDirections.searchResult(coordinates, address))
                } else {
                    Toast.makeText(requireContext(), "Location is already in the list", Toast.LENGTH_LONG).show()
                }
            }
            override fun onError(status: Status) {
                Toast.makeText(requireContext(), "Location not picked", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch() {
            weatherViewModel.deleteAllLocations()
        val updatedList = (binding.rcvLocations.adapter as? LocationsAdapter)?.getUpdatedList()
        val locoList = updatedList?.map { it.location }
        if (updatedList != null) {
            if (locoList != null) {
                weatherViewModel.locoUpdate(updatedList)
                weatherViewModel.updateLocations(locoList)
            }
            Log.d("Testo", "$locoList")
        }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
