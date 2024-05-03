package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    suspend fun getWeather(
        @Query("q") cityName: String,
        @Query("units") unit: String = "metric",
        @Query("appid") apiKey: String = "7088bc5043f891ed50f39a3dd9d742f5"
    ): Response<Weather>

    @GET("weather")
    suspend fun getUserWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") unit: String = "metric",
        @Query("appid") apiKey: String = "7088bc5043f891ed50f39a3dd9d742f5"
    ): Response<Weather>
}