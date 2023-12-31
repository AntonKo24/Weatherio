package com.tonyk.android.weatherapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tonyk.android.weatherapp.R
import com.tonyk.android.weatherapp.model.DailyForecast
import com.tonyk.android.weatherapp.databinding.DaysForecastItemBinding
import com.tonyk.android.weatherapp.util.DateConverter
import com.tonyk.android.weatherapp.util.WeatherConverter
import com.tonyk.android.weatherapp.util.WeatherIconMapper

class ForecastListHolder(private val binding: DaysForecastItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(weather: DailyForecast) {
        binding.apply {
            dayTxt.text = DateConverter.formatDate(weather.datetime)
            tempText.text = binding.root.context.getString(
                R.string.High_Low_temp2,
                WeatherConverter.formatData(weather.tempmax),
                WeatherConverter.formatData(weather.tempmin))
            statusTxt.text = weather.conditions
            dailyWeatherPic.load(WeatherIconMapper.getIconResourceId(weather.icon))
        }
    }
}

class ForecastWeatherAdapter (private val weatherList : List<DailyForecast>) : RecyclerView.Adapter<ForecastListHolder>() {
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