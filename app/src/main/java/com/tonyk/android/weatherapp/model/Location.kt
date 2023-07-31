package com.tonyk.android.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location (
    @PrimaryKey val coordinates : String,
    val address : String,
    val position: Int
)