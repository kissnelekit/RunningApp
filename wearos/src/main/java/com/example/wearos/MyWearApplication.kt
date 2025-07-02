package com.example.wearos
import android.app.Application
import android.content.Context // Import für die Erweiterungsfunktion
import androidx.datastore.core.DataStore // Import für DataStore
import androidx.datastore.preferences.core.Preferences // Import für Preferences
import androidx.datastore.preferences.preferencesDataStore // Import für die Erweiterung
import com.example.wearos.data.DataLayerListenerService
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.Wearable

// Definiere die DataStore-Instanz für Wear OS auf Top-Level oder in einer Util-Datei
// Diese Zeile ist wichtig und muss außerhalb der Klasse sein, wenn du sie als Erweiterung nutzen willst.

val Context.wearSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "wear_app_settings")

class MyWearApplication : Application() {

    // Lazy Initialisierung für den WearAppSettingsManager
    /*
    val wearAppSettingsManager: WearAppSettingsManager by lazy {
        // Greift auf die oben definierte Erweiterungsfunktion wearSettingsDataStore zu
        WearAppSettingsManager(this) // Übergibt den ApplicationContext
    }
    */


    // Lazy Initialisierung für den DataClient
    val dataClient: DataClient by lazy {
        Wearable.getDataClient(this)
    }

    // Später könntest du hier auch deine Room-Datenbank oder Health Services Client initialisieren
    // val database: AppDatabase by lazy { /* ... Room Initialisierung ... */ }
    // val exerciseClient: ExerciseClient by lazy { HealthServices.getClient(this).exerciseClient }


    override fun onCreate() {
        super.onCreate()
        /*
        Wearable.getDataClient(this).addListener(DataLayerListenerService())

         */
        // Hier könnten einmalige Initialisierungen für die gesamte App stattfinden
    }

}

