package com.tonyk.android.weatherapp.database

import com.tonyk.android.weatherapp.data.LocationItem
import kotlinx.coroutines.flow.Flow

interface LocationsDatabaseRepository {

    fun getLocations(): Flow<List<LocationItem>>

    suspend fun addLocation(location: LocationItem)

    suspend fun deleteLocation(location: LocationItem)

    suspend fun updateLocation(location: LocationItem)

}