package com.tonyk.android.weatherapp.di

import com.tonyk.android.weatherapp.repositories.WeatherApiRepository
import com.tonyk.android.weatherapp.api.WeatherApi
import com.tonyk.android.weatherapp.api.WeatherInterceptor
import com.tonyk.android.weatherapp.repositories.WeatherApiRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(WeatherInterceptor())
            .build()
    }
    @Provides
    @Singleton
    fun provideWeatherApi(okHttpClient: OkHttpClient): WeatherApi {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://weather.visualcrossing.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
        return retrofit.create(WeatherApi::class.java)
    }
    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApi): WeatherApiRepository {
        return WeatherApiRepositoryImpl(api)
    }
}