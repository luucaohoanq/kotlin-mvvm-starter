package com.example.mvvm.apis

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("hourly") hourly: String = "temperature_2m,wind_speed_10m"
    ): HourlyWeatherResponse
}

data class HourlyWeatherResponse(
    val hourly: HourlyWeatherData
)

data class HourlyWeatherData(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val wind_speed_10m: List<Double>
)
