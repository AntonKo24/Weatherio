package com.tonyk.android.weatherapp.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tonyk.android.weatherapp.data.LocationItem
import kotlinx.coroutines.flow.Flow

interface LocationsDatabaseRepository {

    fun getLocations(): Flow<List<LocationItem>>

    suspend fun addLocation(location: LocationItem)

    suspend fun deleteLocation(location: LocationItem)

    suspend fun updateLocation(location: LocationItem)

}