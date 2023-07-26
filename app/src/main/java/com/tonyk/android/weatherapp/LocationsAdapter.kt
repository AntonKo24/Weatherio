package com.tonyk.android.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tonyk.android.weatherapp.data.WeatherioItem
import com.tonyk.android.weatherapp.databinding.LocationItemBinding
import com.tonyk.android.weatherapp.util.ItemTouchHelperAdapter
import com.tonyk.android.weatherapp.util.WeatherConverter
import java.util.Collections

class LocationsAdapter(
    private val onLocationItemClick: (WeatherioItem) -> Unit,
    private val onLongItemClick: (WeatherioItem) -> Unit
) : ListAdapter<WeatherioItem, LocationsViewHolder>(LocationDiffCallback()),
    ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LocationItemBinding.inflate(inflater, parent, false)
        return LocationsViewHolder(binding, onLocationItemClick, onLongItemClick)
    }

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val list = currentList.toMutableList()
        Collections.swap(list, fromPosition, toPosition)
        submitList(list)
    }
    fun getUpdatedList(): List<WeatherioItem> {
        return currentList.toList()
    }
}


class LocationsViewHolder(private val binding: LocationItemBinding, private val onLocationItemClick: (WeatherioItem) -> Unit, private val onLongItemClick: (WeatherioItem) -> Unit) :
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
            tempText.setOnClickListener {
                onLongItemClick(locationListItem)
            }
        }
    }
}

class LocationDiffCallback : DiffUtil.ItemCallback<WeatherioItem>() {
    override fun areItemsTheSame(oldItem: WeatherioItem, newItem: WeatherioItem): Boolean {
        return oldItem.location == newItem.location
    }

    override fun areContentsTheSame(oldItem: WeatherioItem, newItem: WeatherioItem): Boolean {
        return oldItem == newItem
    }
}
