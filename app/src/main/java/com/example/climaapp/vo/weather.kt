package com.example.climaapp.vo

import com.google.gson.annotations.SerializedName

// WeatherResponse object follows OpenWeatherMap's OneCallApi format, parsed with gson
class WeatherResponse {
    @SerializedName("current")
    var current: Current? = null

    @SerializedName("daily")
    var daily: List<Daily>? = null

    class Daily {
        @SerializedName("dt")
        var dt: Long = 0
        @SerializedName("weather")
        var weather: List<Weather>? = null
        @SerializedName("temp")
        var temp: Temp? = null

        class Temp {
            @SerializedName("min")
            var min: Float = 0.0f
            @SerializedName("max")
            var max: Float = 0.0f
        }
    }

    class Current {
        @SerializedName("temp")
        var temp: Float = 0.0f
        @SerializedName("feels_like")
        var feelsLike: Float = 0.0f
        @SerializedName("weather")
        var weather: List<Weather>? = null
        @SerializedName("dt")
        var dt: Long? = 0
        @SerializedName("humidity")
        var humidity: Int = 0
        @SerializedName("uvi")
        var uvIndex: Float = 0.0f
        @SerializedName("wind_speed")
        var windSpeed: Float = 0.0f
    }

    class Weather {
        @SerializedName("id")
        var id: Int = 0
        @SerializedName("description")
        var description: String = ""
        @SerializedName("icon")
        var icon: String = ""
    }
}