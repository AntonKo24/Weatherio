package com.tonyk.android.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tonyk.android.weatherapp.api.HourlyWeatherItem
import com.tonyk.android.weatherapp.databinding.FragmentForecastBinding
import com.tonyk.android.weatherapp.databinding.HourlyForecastItemBinding
import com.tonyk.android.weatherapp.util.WeatherIconMapper


class TodayListHolder(private val binding: HourlyForecastItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(weather: HourlyWeatherItem) {
        binding.hourlyTxt.text = weather.datetime
        binding.tempTxt.text = weather.temp
        val iconName = weather.icon
        val iconResourceId = WeatherIconMapper.getIconResourceId(iconName)
        binding.hourlyPic.load(iconResourceId)

    }
}


class TodayWeatherAdapter (private val weatherList : List<HourlyWeatherItem>) : RecyclerView.Adapter<TodayListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayListHolder {
         val inflater = LayoutInflater.from(parent.context)
        val binding = HourlyForecastItemBinding.inflate(inflater, parent, false)
        return TodayListHolder(binding)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    override fun onBindViewHolder(holder: TodayListHolder, position: Int) {
        val item = weatherList[position]
        holder.bind(item)

    }
}