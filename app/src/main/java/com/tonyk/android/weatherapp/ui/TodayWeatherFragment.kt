package com.tonyk.android.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout


import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope

import androidx.navigation.fragment.findNavController
import com.tonyk.android.weatherapp.R

import com.tonyk.android.weatherapp.databinding.FragmentTodayBinding
import com.tonyk.android.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TodayWeatherFragment : BaseWeatherFragment() {

    private val todayWeatherViewModel: WeatherViewModel by activityViewModels()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navDrawer: ConstraintLayout

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
            findNavController().navigate(TodayWeatherFragmentDirections.showForecast())
        }
        binding.manageLocations.setOnClickListener {
            findNavController().navigate(TodayWeatherFragmentDirections.manageLocations())
        }

        drawerLayout = binding.drawerLayout
        navDrawer = binding.navDrawer

        val toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent))



        binding.manageLocations.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val slideX = drawerView.width * slideOffset
                binding.mainContent.translationX = slideX
            }

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {}
        })
    }
}




