package com.example.climaapp.ui.forecast

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climaapp.R
import com.example.climaapp.adapter.ForecastAdapter
import com.example.climaapp.di.Injectable
import com.example.climaapp.ui.viewModel.WeatherViewModel
import com.example.climaapp.vo.WeatherResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_forecast.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt


const val PERMISSION_REQUEST_LOCATION = 0

class ForecastFragment : Fragment(), Injectable, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback  {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var weatherViewModel: WeatherViewModel

    private var locationManager : LocationManager? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_forecast, container, false)

        weatherViewModel = ViewModelProviders.of(requireActivity(),viewModelFactory)
                            .get(WeatherViewModel::class.java)

        locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager?

        if (weatherViewModel.selected.value == null)
            getUserLocation()

        weatherViewModel.weather.observe(viewLifecycleOwner, Observer {
            populateLayout(it)
        })
        return root
    }

    private fun populateLayout(weatherResponse: WeatherResponse){
        showViews(true)
        val gcd = Geocoder(requireActivity(), Locale("es", "ES"))
        val addresses = gcd.getFromLocation(weatherViewModel.selected.value!!.latitude, weatherViewModel.selected.value!!.longitude, 1)

        var locationString = ""
        if (addresses.size > 0) {
            if(addresses[0].locality != null)
                locationString = addresses[0].locality
            else if (addresses[0].countryName != null)
                locationString = addresses[0].countryName
        }
        text_location.text = locationString

        val daily = weatherResponse.daily?: throw Exception("Invalid daily in weatherResponse")
        val current = weatherResponse.current?: throw Exception("Invalid current in weatherResponse")
        val weather = current.weather?: throw Exception("Invalid weather in weatherResponse")
        val currentWeather = weather[0]
        val iconResource = activity?.resources?.getIdentifier("drawable/ic_" + currentWeather.icon,
            null, activity?.packageName)!!

        val date = Date(current.dt!! * 1000L)

        val sdf = SimpleDateFormat("E MMMM d  h:m a", Locale("es","ES"))
        text_datetime.text = sdf.format(date).capitalize()

        val weatherTemperatureStr = current.temp.roundToInt().toString()+ getString(R.string.celsiusDegrees)
        text_weather_temperature.text =weatherTemperatureStr
        text_weather_description.text = currentWeather.description.capitalize()
        image_weather.setImageResource(iconResource)

        val feelsLikeStr = current.feelsLike.roundToInt().toString() + getString(R.string.celsiusDegrees)
        text_feels_like_value.text = feelsLikeStr
        text_feels_like_comment.text = feelsLikeDescription(current.feelsLike)

        val humidityStr = current.humidity.toString() + getString(R.string.percentage)
        text_humidity_value.text = humidityStr
        text_humidity_comment.text = humidityDescription(current.humidity)

        text_uv_value.text = current.uvIndex.roundToInt().toString()
        text_uv_comment.text = uvIndexDescription(current.uvIndex.roundToInt())

        val windStr = current.windSpeed.roundToInt().toString() + getString(R.string.kilometersPerHour)
        text_wind_value.text = windStr
        text_wind_comment.text = windSpeedDescription(current.windSpeed)

        recyclerView_upcoming_seven_days.adapter = ForecastAdapter(requireActivity(), daily)
        val l = LinearLayoutManager(requireActivity())
        l.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView_upcoming_seven_days.layoutManager = l
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getUserLocation()
            }
            else{
                showViews(false)
                val listenerGoToMap: View.OnClickListener = View.OnClickListener {
                    requireActivity().findViewById<BottomNavigationView>(R.id.nav_view).selectedItemId = R.id.navigation_map
                }
                text_noLocation_map.setOnClickListener(listenerGoToMap)
                image_noLocation_map.setOnClickListener(listenerGoToMap)
                val listenerAskPermission: View.OnClickListener = View.OnClickListener {
                    requestLocationPermission()
                }
                text_noLocation_location.setOnClickListener(listenerAskPermission)
                image_noLocation_location.setOnClickListener(listenerAskPermission)
            }
        }
    }

    private fun getUserLocation() {
        // Check if the location permission has been granted
        if (checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this)
        else
            requestLocationPermission()
    }

    // Parameter: If GPS permissions has been granted
    private fun showViews(permission: Boolean){
        val visibilityNormalViews = if (permission) View.VISIBLE else View.GONE
        val visibilityNoPermissionViews = if (permission) View.GONE else View.VISIBLE

        progressBar.visibility = View.GONE
        text_noLocation_message.visibility = visibilityNoPermissionViews
        image_noLocation_map.visibility = visibilityNoPermissionViews
        text_noLocation_map.visibility = visibilityNoPermissionViews
        text_noLocation_or.visibility = visibilityNoPermissionViews
        image_noLocation_location.visibility = visibilityNoPermissionViews
        text_noLocation_location.visibility = visibilityNoPermissionViews

        text_location.visibility = visibilityNormalViews
        text_datetime.visibility = visibilityNormalViews

        image_weather.visibility = visibilityNormalViews

        text_weather_temperature.visibility = visibilityNormalViews
        text_weather_description.visibility = visibilityNormalViews

        rectangle_feels_like.visibility = visibilityNormalViews
        text_feels_like.visibility = visibilityNormalViews
        text_feels_like_value.visibility = visibilityNormalViews
        text_feels_like_comment.visibility = visibilityNormalViews

        rectangle_humidity.visibility = visibilityNormalViews
        text_humidity.visibility = visibilityNormalViews
        text_humidity_value.visibility = visibilityNormalViews
        text_humidity_comment.visibility = visibilityNormalViews

        rectangle_uv.visibility = visibilityNormalViews
        text_uv.visibility = visibilityNormalViews
        text_uv_value.visibility = visibilityNormalViews
        text_uv_comment.visibility = visibilityNormalViews

        rectangle_wind.visibility = visibilityNormalViews
        text_wind.visibility = visibilityNormalViews
        text_wind_value.visibility = visibilityNormalViews
        text_wind_comment.visibility = visibilityNormalViews

        recyclerView_upcoming_seven_days.visibility = visibilityNormalViews
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_LOCATION)
    }

    // humidity percentage
    private fun humidityDescription(humidity: Int) = when (humidity){
        in 0..30 -> getString(R.string.humidity_dry)
        in 30..60 -> getString(R.string.humidity_normal)
        in 60..100 -> getString(R.string.humidity_moist)
        else -> throw Exception("Humidity is out of range")
    }

    // feels like temperature in celsius
    private fun feelsLikeDescription(feelsLike: Float) = when (feelsLike){
        in 27.0..60.0 -> getString(R.string.feels_like_hot)
        in 16.0..27.0 -> getString(R.string.feels_like_warm)
        in 5.0..16.0 -> getString(R.string.feels_like_cool)
        in -10.0..5.0 -> getString(R.string.feels_like_cold)
        in -90.0..5.0 -> getString(R.string.feels_like_freezing)
        else -> throw Exception("Temperature is out of range")
    }

    // windSpeed in km/h
    private fun windSpeedDescription(windSpeed: Float) = when (windSpeed){
        in 0.0..2.0 -> getString(R.string.wind_speed_low)
        in 2.0..8.0 -> getString(R.string.wind_speed_calm)
        in 8.0..60.0 -> getString(R.string.wind_speed_windy)
        in 60.0..380.0 -> getString(R.string.wind_speed_extreme)
        else -> throw Exception("Wind velocity is out of range")
    }

    // uv index
    private fun uvIndexDescription(uvIndex: Int) = when (uvIndex){
        in 0..2 -> getString(R.string.uv_low)
        in 3..5 -> getString(R.string.uv_moderate)
        in 6..7 -> getString(R.string.uv_high)
        in 8..10 -> getString(R.string.uv_very_high)
        in 11..45 -> getString(R.string.uv_extreme)
        else -> throw Exception("UvIndex is out of range")
    }

    @SuppressWarnings
    override fun onLocationChanged(location: Location) {
        weatherViewModel.setLocation(location)
        locationManager?.removeUpdates(this)
    }

    override fun onProviderDisabled(arg0: String ) {}
    override fun onProviderEnabled(arg0: String) {}
    override fun onStatusChanged(arg0: String, ar10: Int, arg2: Bundle) {}
}
