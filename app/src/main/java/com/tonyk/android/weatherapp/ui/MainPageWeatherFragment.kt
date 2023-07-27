package com.tonyk.android.weatherapp.ui

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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


        binding.checkButton.setOnClickListener {
            lifecycleScope.launch {
                binding.checkButton.visibility = View.GONE
                if (LocationService.isLocationPermissionGranted(requireActivity())) {
                    if (LocationService.isGPSEnabled(requireActivity())) {
                        LocationService.getLocationData(requireActivity()) { coordinates, address ->
                            todayWeatherViewModel.initializeWeatherViewModel(coordinates, address)
                        }
                    } else { LocationService.showGPSAlertDialog(requireActivity()) }
                }
                else Toast.makeText(context, "Permission is not granted", Toast.LENGTH_LONG).show()
                delay(3000)
                binding.checkButton.visibility = View.VISIBLE
            }
        }

        binding.checkForecastBtn.setOnClickListener {
            if (todayWeatherViewModel.weatherioItem.value.weather.days.isNotEmpty()) {
            findNavController().navigate(MainPageWeatherFragmentDirections.showForecast(todayWeatherViewModel.weatherioItem.value)) }
        }


        val drawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        drawerLayout.setScrimColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
        binding.settingsBtn.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(MainPageWeatherFragmentDirections.manageLocations())
        }
        binding.manageLocations.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
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




