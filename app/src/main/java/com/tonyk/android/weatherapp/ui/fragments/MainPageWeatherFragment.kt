package com.tonyk.android.weatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.tonyk.android.weatherapp.ui.adapters.MenuLocationsAdapter
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.databinding.FragmentWeatherBinding
import com.tonyk.android.weatherapp.util.LocationService
import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainPageWeatherFragment : BaseWeatherFragment() {

    private val todayWeatherViewModel: WeatherViewModel by activityViewModels()


    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentWeatherBinding {
        return FragmentWeatherBinding.inflate(inflater, container, false)
    }
    override fun getWeatherViewModel(): WeatherViewModel {
        return todayWeatherViewModel
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeErrorState(getWeatherViewModel())

        binding.checkButton.setOnClickListener {
            binding.checkButton.load(R.drawable.ic_loading)
            binding.checkButton.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                if (LocationService.isLocationPermissionGranted(requireActivity())) {
                    if (LocationService.isGPSEnabled(requireActivity())) {
                        LocationService.getLocationData(requireActivity()) { coordinates, address ->
                            todayWeatherViewModel.initializeWeatherViewModel(coordinates, address)
                        }
                    } else {
                        LocationService.showGPSAlertDialog(requireActivity())
                    }
                } else {
                    Toast.makeText(context, "Permission is not granted", Toast.LENGTH_LONG).show()
                }
                delay(3000)
                binding.checkButton.postDelayed({
                    binding.checkButton.isEnabled = true
                    binding.checkButton.load(R.drawable.ic_refresh)
                }, 0)
            }
        }

        binding.checkForecastBtn.setOnClickListener {
            if (todayWeatherViewModel.weatherio.value.weather.days.isNotEmpty()) {
            findNavController().navigate(
                MainPageWeatherFragmentDirections.showForecast(
                    todayWeatherViewModel.weatherio.value
                )
            ) }
        }

        binding.menuRcv.layoutManager = LinearLayoutManager(context)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                todayWeatherViewModel.locationsList.collect() { it ->
                    binding.menuRcv.adapter = MenuLocationsAdapter(it) { todayWeatherViewModel.setWeather(it)
                    binding.drawerLayout.closeDrawer(GravityCompat.START) }
                }
            }
        }



        val drawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        drawerLayout.setScrimColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
        binding.manageButton.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(MainPageWeatherFragmentDirections.manageLocations())
        }
        binding.manageLocations.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.settingsBtn.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(MainPageWeatherFragmentDirections.checkSettings())
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                binding.mainContent.translationX = drawerView.width * slideOffset
            }
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })
    }
}




