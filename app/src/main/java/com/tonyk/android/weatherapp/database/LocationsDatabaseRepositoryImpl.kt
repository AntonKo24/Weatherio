package com.tonyk.android.weatherapp.database

import com.tonyk.android.weatherapp.model.Location
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationsDatabaseRepositoryImpl @Inject constructor(private val database: LocationsDatabase): LocationsDatabaseRepository  {

    override suspend fun updateLocation(location: Location) {
        database.locationDao().updateLocation(location)
    }
    override fun getLocations(): Flow<List<Location>> = database.locationDao().getLocations()

    override suspend fun addLocation(location: Location) {
        database.locationDao().addLocation(location)
    }

    override suspend fun deleteLocation(location: Location) {
        database.locationDao().deleteLocation(location)
    }



}
