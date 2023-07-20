package com.tonyk.android.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tonyk.android.weatherapp.databinding.FragmentSearchBinding

import com.tonyk.android.weatherapp.viewmodel.LocationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment: Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding
        get () = checkNotNull(_binding)

    private val locationsViewModel
    : LocationsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = SearchFragmentArgs.fromBundle(requireArguments())
        val firstString = args.location
        val secondString = args.placeName

        locationsViewModel.setQuery(firstString, secondString)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationsViewModel.locationsList.collect { list ->
                    if (list.isNotEmpty()) {
                        binding.textView2.text = list[0].weather.currentConditions.temp.toString()
                        binding.textView3.text = secondString
                    }
                }

            }
        }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
