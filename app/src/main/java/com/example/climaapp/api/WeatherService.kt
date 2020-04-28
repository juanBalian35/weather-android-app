package com.example.climaapp.api

import com.example.climaapp.vo.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

// Service that interacts with OpenWeatherMap's onCall api through RetroFit
@Singleton
interface WeatherService {
    @GET("onecall")
    fun getWeather(@Query("lat") lat: Float,
                   @Query("lon") lon: Float,
                   @Query("units") units: String,
                   @Query("lang") lang: String,
                   @Query("appid") appId: String): Call<WeatherResponse>
}
