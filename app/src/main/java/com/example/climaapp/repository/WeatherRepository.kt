package com.example.climaapp.repository

import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.climaapp.vo.WeatherResponse
import com.example.climaapp.api.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

// WeatherRepository interacts with the service and abstracts getting data from the ViewModel
@Singleton
class WeatherRepository  @Inject constructor(
    private val weatherService: WeatherService
) {
    fun getWeather(location: Location, units: String,
                   lang: String, appId: String): LiveData<WeatherResponse> {
        val data = MutableLiveData<WeatherResponse>()

        weatherService.getWeather(location.latitude.toFloat(),
                                  location.longitude.toFloat(), units, lang, appId)
                                        .enqueue(object: Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>,
                                    response: Response<WeatherResponse>) {
                data.value = response.body()
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable){
                Log.d("WREP", t.message)
            }
        })

        return data
    }
}
