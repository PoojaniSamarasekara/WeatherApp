package com.example.weatherapp

data class WeatherResponse(
    val name: String,
    val main: MainData,
    val weather: List<WeatherData>,
    val wind: WindData
)

data class MainData(
    val temp: Double,
    val humidity: Int
)

data class WeatherData(
    val description: String
)

data class WindData(
    val speed: Double
)