package com.example.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {
    val weather = repository.weather

    fun getWeatherResponse(city: String) {
        viewModelScope.launch {
            repository.getWeatherResponse(city)
        }
    }

    fun getUserWeather(lat: String, lon: String) {
        viewModelScope.launch {
            repository.getUserWeather(lat,lon)
        }
    }
}