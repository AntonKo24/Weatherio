package com.tonyk.android.weatherapp.database

import com.tonyk.android.weatherapp.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationsDatabaseRepository {

    fun getLocations(): Flow<List<Location>>

    suspend fun addLocation(location: Location)

    suspend fun deleteLocation(location: Location)

    suspend fun updateLocation(location: Location)

}