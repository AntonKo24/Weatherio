package com.tonyk.android.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.tonyk.android.weatherapp.database.LocationsDatabase
import com.tonyk.android.weatherapp.database.LocationsDatabaseRepository
import com.tonyk.android.weatherapp.database.LocationsDatabaseRepositoryImpl
import com.tonyk.android.weatherapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLocationsDatabase(@ApplicationContext context: Context): LocationsDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            LocationsDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieDatabaseRepository(database: LocationsDatabase): LocationsDatabaseRepository {
        return LocationsDatabaseRepositoryImpl(database)
    }
}