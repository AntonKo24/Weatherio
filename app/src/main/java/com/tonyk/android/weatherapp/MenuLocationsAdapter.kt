package com.tonyk.android.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tonyk.android.weatherapp.data.WeatherioItem
import com.tonyk.android.weatherapp.databinding.MenuLocationItemBinding
import com.tonyk.android.weatherapp.util.WeatherConverter
import com.tonyk.android.weatherapp.util.WeatherIconMapper

class MenuLocationHolder(private val binding: MenuLocationItemBinding, private val onLocationItemClick: (WeatherioItem) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: WeatherioItem) {
        binding.apply {
            itemAddress.text = item.location.address
            itemPic.load(WeatherIconMapper.getIconResourceId(item.weather.currentConditions.icon))
            itemTemp.text = root.context.getString(R.string.Temperature,WeatherConverter.formatData(item.weather.currentConditions.temp))
            root.setOnClickListener { onLocationItemClick(item) }
        }

    }
}

class MenuLocationsAdapter (private val weatherList : List<WeatherioItem>, private val onLocationItemClick: (WeatherioItem) -> Unit) : RecyclerView.Adapter<MenuLocationHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuLocationHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MenuLocationItemBinding.inflate(inflater, parent, false)
        return MenuLocationHolder(binding, onLocationItemClick)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    override fun onBindViewHolder(holder: MenuLocationHolder, position: Int) {
        val item = weatherList[position]
        holder.bind(item)

    }
}