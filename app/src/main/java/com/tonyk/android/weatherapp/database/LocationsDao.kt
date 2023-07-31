package com.tonyk.android.weatherapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tonyk.android.weatherapp.model.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationsDao {

    @Query("SELECT * FROM locationitem ORDER by position")
    fun getLocations(): Flow<List<Location>>

    @Insert
    suspend fun addLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Update
    suspend fun updateLocation(location: Location)

}