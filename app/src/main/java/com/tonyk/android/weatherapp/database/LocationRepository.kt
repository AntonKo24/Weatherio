package com.tonyk.android.weatherapp.database

import android.content.Context
import androidx.room.Room
import com.tonyk.android.weatherapp.data.LocationItem
import kotlinx.coroutines.flow.Flow


private const val DATABASE_NAME = "location-database"


class LocationRepository private constructor(context: Context) {

    private val database: LocationDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            LocationDatabase::class.java,
            DATABASE_NAME
        )
        .build()

    suspend fun deleteAllLocations() {
        database.locationDao().deleteAllLocations()
    }
    suspend fun updateLocations(locations: List<LocationItem>) {
        database.locationDao().updateLocations(locations)
    }
    fun getLocations(): Flow<List<LocationItem>> = database.locationDao().getLocations()

    suspend fun addLocation(location: LocationItem) {
        database.locationDao().addLocation(location)
    }

    suspend fun deleteLocation(location: LocationItem) {
        database.locationDao().deleteLocation(location)
    }



    companion object {
        private var INSTANCE: LocationRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = LocationRepository(context)
            }
        }
        fun get(): LocationRepository {
            return INSTANCE ?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}
