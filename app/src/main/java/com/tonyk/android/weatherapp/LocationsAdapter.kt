package com.tonyk.android.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tonyk.android.weatherapp.api.WeatherResponse
import com.tonyk.android.weatherapp.data.LocationItem
import com.tonyk.android.weatherapp.data.WeatherioItem
import com.tonyk.android.weatherapp.databinding.LocationItemBinding
import com.tonyk.android.weatherapp.util.WeatherConverter

class LocationsViewHolder(private val binding: LocationItemBinding, private val onLocationItemClick: (WeatherioItem) -> Unit, private val onLongItemClick: (LocationItem) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(locationListItem: WeatherioItem) {
        binding.apply {
            resolvedAddressTxt.text = locationListItem.location.address
            tempText.text = root.context.getString(R.string.Temperature, WeatherConverter.formatData(locationListItem.weather.currentConditions.temp))
            highLowText.text = root.context.getString(R.string.High_Low_temp,
                WeatherConverter.formatData(locationListItem.weather.days[0].tempmax),
                WeatherConverter.formatData(locationListItem.weather.days[0].tempmin))


            root.setOnClickListener {
                onLocationItemClick(locationListItem)
            }
            root.setOnLongClickListener {
                onLongItemClick(locationListItem.location)
                true // or false, depending on whether you want to consume the event or not
            }
        }
    }
}

class LocationsAdapter(private val locationsList: List<WeatherioItem>, private val onLocationItemClick: (WeatherioItem) -> Unit, private val onLongItemClick: (LocationItem) -> Unit) :
    RecyclerView.Adapter<LocationsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LocationItemBinding.inflate(inflater, parent, false)
        return LocationsViewHolder(binding, onLocationItemClick, onLongItemClick)
    }

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        val item = locationsList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return locationsList.size
    }
}