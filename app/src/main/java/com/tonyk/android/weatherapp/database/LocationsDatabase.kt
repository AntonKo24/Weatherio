package com.tonyk.android.weatherapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tonyk.android.weatherapp.data.LocationItem

@Database(entities = [LocationItem::class], version=1)
abstract class LocationsDatabase : RoomDatabase() {
    abstract fun locationDao() : LocationsDao

}