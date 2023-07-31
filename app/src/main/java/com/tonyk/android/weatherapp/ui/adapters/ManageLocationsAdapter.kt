package com.tonyk.android.weatherapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.model.Weatherio
import com.tonyk.android.weatherapp.databinding.ManageLocationsItemBinding
import com.tonyk.android.weatherapp.util.itemtouchhelper.ItemTouchHelperAdapter
import com.tonyk.android.weatherapp.util.WeatherConverter
import com.tonyk.android.weatherapp.util.WeatherIconMapper
import java.util.Collections

class LocationsAdapter(
    private val onLocationItemClick: (Weatherio) -> Unit,
    private val deleteItem: (Weatherio) -> Unit,
    private val onListReordered: (List<Weatherio>) -> Unit
) : ListAdapter<Weatherio, LocationsViewHolder>(LocationDiffCallback()),
    ItemTouchHelperAdapter {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ManageLocationsItemBinding.inflate(inflater, parent, false)
        return LocationsViewHolder(binding, onLocationItemClick, deleteItem)
    }

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val list = currentList.toMutableList()
        Collections.swap(list, fromPosition, toPosition)
        submitList(list)
        onListReordered(list)
    }
}

class LocationsViewHolder(private val binding: ManageLocationsItemBinding, private val onLocationItemClick: (Weatherio) -> Unit, private val deleteItem: (Weatherio) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Weatherio) {
        binding.apply {
            resolvedAddressTxt.text = item.location.address
            tempText.text = root.context.getString(R.string.Temperature, WeatherConverter.formatData(item.weather.currentConditions.temp))
            highLowText.text = root.context.getString(
                R.string.High_Low_temp,
                WeatherConverter.formatData(item.weather.days[0].tempmax),
                WeatherConverter.formatData(item.weather.days[0].tempmin))
            weatherPic.load(WeatherIconMapper.getIconResourceId(item.weather.currentConditions.icon))
            root.setOnClickListener {
                onLocationItemClick(item)
            }
            deleteBtn.setOnClickListener {
                deleteItem(item)
            }
        }
    }
}

class LocationDiffCallback : DiffUtil.ItemCallback<Weatherio>() {
    override fun areItemsTheSame(oldItem: Weatherio, newItem: Weatherio): Boolean {
        return oldItem.location == newItem.location
    }

    override fun areContentsTheSame(oldItem: Weatherio, newItem: Weatherio): Boolean {
        return oldItem == newItem
    }
}
