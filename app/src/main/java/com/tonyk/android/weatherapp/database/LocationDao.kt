package com.tonyk.android.weatherapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tonyk.android.weatherapp.data.LocationItem
import kotlinx.coroutines.flow.Flow


@Dao
interface LocationDao {

    @Query("SELECT * FROM locationitem ORDER by position")
    fun getLocations(): Flow<List<LocationItem>>

    @Insert
    suspend fun addLocation(location: LocationItem)

    @Delete
    suspend fun deleteLocation(location: LocationItem)

    @Update
    suspend fun updateLocation(location: LocationItem)

    @Query("DELETE FROM locationitem")
    suspend fun deleteAllLocations()

}