package com.example.climaapp.ui.map

import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.climaapp.R
import com.example.climaapp.di.Injectable
import com.example.climaapp.ui.viewModel.WeatherViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject

class MapFragment : Fragment(), OnMapReadyCallback, Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mMap: GoogleMap
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        weatherViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(WeatherViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById((R.id.map)) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener {
            mMap.clear()
            val l = Location(LocationManager.GPS_PROVIDER)
            l.latitude = it.latitude
            l.longitude = it.longitude
            weatherViewModel.setLocation(l)
            mMap.addMarker(MarkerOptions().position(it))
        }
        weatherViewModel.selected.observeOnce(viewLifecycleOwner, Observer { addMarker(it) })
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    private fun addMarker(location: Location){
        val marker = LatLng(location.latitude, location.longitude)
        mMap.addMarker(MarkerOptions().position(marker))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
    }
}
