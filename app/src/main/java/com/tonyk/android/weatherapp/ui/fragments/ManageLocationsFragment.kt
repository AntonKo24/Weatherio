package com.tonyk.android.weatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.tonyk.android.weatherapp.ui.adapters.LocationsAdapter
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.databinding.FragmentManageLocationsBinding
import com.tonyk.android.weatherapp.util.itemtouchhelper.DragItemTouchHelperCallback
import com.tonyk.android.weatherapp.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageLocationsFragment: Fragment() {
    private var _binding: FragmentManageLocationsBinding? = null
    private val binding
        get () = checkNotNull(_binding)

    private val sharedViewModel : SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentManageLocationsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.rcvLocations.layoutManager = LinearLayoutManager(context)
        val adapter = LocationsAdapter({ item ->
            sharedViewModel.setWeather(item)
            findNavController().popBackStack()
        }, { item ->
            sharedViewModel.deleteLocation(item.location)
            sharedViewModel.updateLocationsPosition(sharedViewModel.locationsList.value)
        }, { reorderedList ->
            sharedViewModel.updateViewModelList(reorderedList)
            sharedViewModel.updateLocationsPosition(reorderedList)
        })
        binding.rcvLocations.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(DragItemTouchHelperCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.rcvLocations)


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.locationsList.collect {
                    adapter.submitList(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.errorState.collect { error ->
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                }
            }
        }


        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocompleteFragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setTypesFilter(listOf(PlaceTypes.CITIES))
        autocompleteFragment.view?.findViewById<EditText>(com.google.android.libraries.places.R.id.places_autocomplete_search_input)?.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.white))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val coordinates = "${place.latLng?.latitude ?: 0.0},${place.latLng?.longitude ?: 0.0}"
                val address = place.name ?: ""
                if (sharedViewModel.locationsList.value.none { it.location.coordinates == coordinates }) {
                    findNavController().navigate(ManageLocationsFragmentDirections.searchResult(
                            coordinates,
                            address,
                            sharedViewModel.locationsList.value.size
                        )
                    )
                } else {
                    Toast.makeText(requireContext(), "Location is already in the list", Toast.LENGTH_LONG).show()
                }
            }
            override fun onError(status: Status) {
                Toast.makeText(requireContext(), "Location not picked", Toast.LENGTH_SHORT).show()
            }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
