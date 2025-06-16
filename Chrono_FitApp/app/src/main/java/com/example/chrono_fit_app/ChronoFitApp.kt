package com.example.chrono_fit_app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChronoFitApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}