package com.example.climaapp.di

import com.example.climaapp.ui.forecast.ForecastFragment
import com.example.climaapp.ui.map.MapFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeForecastFragment(): ForecastFragment

    @ContributesAndroidInjector
    abstract fun contributeMapFragment(): MapFragment
}