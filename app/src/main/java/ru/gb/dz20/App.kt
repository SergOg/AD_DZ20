package ru.gb.dz20

import android.app.Application
import com.yandex.mapkit.MapKitFactory
private const val MAP_API_KEY = "ea81d586-ca67-4627-b712-9f276784ad97"

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAP_API_KEY)
    }
}