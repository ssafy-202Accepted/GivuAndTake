package com.project.givuandtake.core.data

data class WeatherData(
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    val temp: Float // 섭씨 온도
)

data class Weather(
    val main: String,
    val description: String
)