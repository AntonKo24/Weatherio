package com.tonyk.android.weatherapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.databinding.HoursForecastItemBinding
import com.tonyk.android.weatherapp.model.HourlyForecastItem

import com.tonyk.android.weatherapp.util.DateConverter
import com.tonyk.android.weatherapp.util.WeatherConverter
import com.tonyk.android.weatherapp.util.WeatherIconMapper

class TodayListHolder(private val binding: HoursForecastItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(weather: HourlyForecastItem) {
        binding.apply {
            hourlyTxt.text = DateConverter.formatTime(weather.datetime)
            tempTxt.text = root.context.getString(R.string.Temperature,WeatherConverter.formatData(weather.temp))
            hourlyPic.load(WeatherIconMapper.getIconResourceId(weather.icon))
        }

    }
}

class TodayWeatherAdapter (private val weatherList : List<HourlyForecastItem>) : RecyclerView.Adapter<TodayListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayListHolder {
         val inflater = LayoutInflater.from(parent.context)
        val binding = HoursForecastItemBinding.inflate(inflater, parent, false)
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