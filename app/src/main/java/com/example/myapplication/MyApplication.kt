package com.example.myapplication
import android.app.Application

class MyApplication : Application() {

    // Lazy Initialisierung der DataStore-Instanz
    // appSettingsDataStore kommt von der Erweiterungsfunktion, die wir in AppSettingsManager.kt definiert haben
    val appSettingsManager by lazy {
        AppSettingsManager(appSettingsDataStore)
    }

    override fun onCreate() {
        super.onCreate()
        // Hier könnten später weitere globale Initialisierungen erfolgen
    }
}