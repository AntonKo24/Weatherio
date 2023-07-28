package com.tonyk.android.weatherapp.database

import com.tonyk.android.weatherapp.data.LocationItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationsDatabaseRepositoryImpl @Inject constructor(private val database: LocationsDatabase): LocationsDatabaseRepository  {

    override suspend fun updateLocation(location: LocationItem) {
        database.locationDao().updateLocation(location)
    }
    override fun getLocations(): Flow<List<LocationItem>> = database.locationDao().getLocations()

    override suspend fun addLocation(location: LocationItem) {
        database.locationDao().addLocation(location)
    }

    override suspend fun deleteLocation(location: LocationItem) {
        database.locationDao().deleteLocation(location)
    }



}
