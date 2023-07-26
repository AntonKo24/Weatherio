package com.tonyk.android.weatherapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class LocationItem (
    @PrimaryKey val coordinates : String,
    val address : String,
    val position: Int
)