package com.tonyk.android.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tonyk.android.weatherapp.api.WeatherResponse
import com.tonyk.android.weatherapp.databinding.LocationItemBinding
import com.tonyk.android.weatherapp.util.WeatherConverter

class LocationsViewHolder(private val binding: LocationItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(locationListItem: WeatherResponse) {
        binding.apply {
            resolvedAddressTxt.text = locationListItem.resolvedAddress
            tempText.text = root.context.getString(R.string.Temperature, WeatherConverter.formatData(locationListItem.currentConditions.temp))
            highLowText.text = root.context.getString(R.string.High_Low_temp,
                WeatherConverter.formatData(locationListItem.days[0].tempmax),
                WeatherConverter.formatData(locationListItem.days[0].tempmin))
            root.setOnClickListener {

            }
        }
    }
}

class LocationsAdapter(private val locationsList: List<WeatherResponse>) :
    RecyclerView.Adapter<LocationsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LocationItemBinding.inflate(inflater, parent, false)
        return LocationsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        val item = locationsList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return locationsList.size
    }
}