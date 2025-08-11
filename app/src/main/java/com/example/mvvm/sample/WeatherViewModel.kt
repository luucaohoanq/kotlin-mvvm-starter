package org.com.hcmurs.ui.components.weather

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.example.mvvm.sample.WeatherRepository
import com.example.mvvm.sample.WeatherResult
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _temperature = mutableStateOf<Double?>(null)
    val temperature: State<Double?> = _temperature

    private val _weather = mutableStateOf<WeatherResult?>(null)
    val weather: State<WeatherResult?> = _weather

    init {
        fetchWeather()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchTemperature() {
        viewModelScope.launch {
            try {
                _temperature.value = repository.getTemperature(10.823, 106.6296)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error: ${e.message}")
            }
        }
    }

    private fun fetchWeather() {
        viewModelScope.launch {
            try {
                _weather.value = repository.getCurrentWeather(10.823, 106.6296)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error: ${e.message}")
            }
        }
    }
}
