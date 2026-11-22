package com.example.wasteco

import android.app.Application
import com.example.wasteco.di.AppContainer
import com.example.wasteco.di.DefaultAppContainer

class WasteCoApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
