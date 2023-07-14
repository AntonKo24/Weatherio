package com.tonyk.android.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tonyk.android.weatherapp.api.DailyWeatherItem
import com.tonyk.android.weatherapp.databinding.DaysForecastItemBinding
import com.tonyk.android.weatherapp.util.WeatherIconMapper

class ForecastListHolder(private val binding: DaysForecastItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(weather: DailyWeatherItem) {
        binding.dayTxt.text = weather.datetime
        binding.tempText.text = "${weather.tempmax}/${weather.tempmin}"
        binding.statusTxt.text = weather.conditions

        val iconName = weather.icon
        val iconResourceId = WeatherIconMapper.getIconResourceId(iconName)
        binding.dailyWeatherPic.load(iconResourceId)

    }
}


class ForecastWeatherAdapter (private val weatherList : List<DailyWeatherItem>) : RecyclerView.Adapter<ForecastListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastListHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DaysForecastItemBinding.inflate(inflater, parent, false)
        return ForecastListHolder(binding)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    override fun onBindViewHolder(holder: ForecastListHolder, position: Int) {
        val item = weatherList[position]
        holder.bind(item)

    }
}