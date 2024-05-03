package com.example.weatherapp.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.model.Weather
import com.example.weatherapp.data.remote.WeatherApiService
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiService: WeatherApiService
) {

    private val _weather = MutableLiveData<Weather>()
    val weather: LiveData<Weather> = _weather

    suspend fun getWeatherResponse(city: String) {
        try {
            val result = apiService.getWeather(city)
            if (result.body() != null) {
                _weather.postValue(result.body())
            }
        } catch (e: Exception) {
            Log.d("Network Response", e.message.toString())
        }
    }

    suspend fun getUserWeather(latitude: String, longitude: String) {
        try {
            val result = apiService.getUserWeather(latitude,longitude)
            if (result.body() != null) {
                _weather.postValue(result.body())
            }
        } catch (e: Exception) {
            Log.d("Network Response", e.message.toString())
        }
    }


}