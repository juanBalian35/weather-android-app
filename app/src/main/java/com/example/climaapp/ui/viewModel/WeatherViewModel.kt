package com.example.climaapp.ui.viewModel

import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.climaapp.R
import com.example.climaapp.vo.WeatherResponse
import com.example.climaapp.repository.WeatherRepository
import javax.inject.Inject

class WeatherViewModel  @Inject constructor(
    weatherRepository: WeatherRepository,
    application: Application
) : ViewModel() {
    val selected = MutableLiveData<Location>()
    var weather : LiveData<WeatherResponse> = Transformations.switchMap(selected) {
        weatherRepository.getWeather(it,
            application.getString(R.string.open_weather_map_unit),
            application.getString(R.string.open_weather_map_language),
            application.getString(R.string.open_weather_map_app_id)
        )
    }

    fun setLocation(item: Location) {
        selected.value = item
    }
}
