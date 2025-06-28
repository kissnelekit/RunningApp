package com.example.myapplication
import com.example.myapplication.data.AppDatabase //
import com.example.myapplication.data.WorkoutDAO
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.AppSettingsManager
import com.example.myapplication.data.Workout
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// DataStore-Instanz Definition (stelle sicher, dass der Name "app_settings" korrekt ist für dein Smartphone-Modul)
val Context.settingsDataStoreInstance: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class MyApplication : Application() {

    // DataStore und AppSettingsManager (wenn du Ansatz 1 für den Manager verwendest)
    private val appDataStore: DataStore<Preferences> by lazy {
        this.settingsDataStoreInstance
    }
    private val database by lazy { AppDatabase.getDatabase(this) }

    val workoutDao: WorkoutDAO by lazy {
        database.workoutDao()
    }


    val appSettingsManager: AppSettingsManager by lazy { // Annahme: Du hast einen AppSettingsManager
        AppSettingsManager(appDataStore)
    }

    // DataClient für die Smartphone-App
    val dataClient: DataClient by lazy {
        Wearable.getDataClient(this)
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO) // IO-Dispatcher für Netzwerk/Datei-Operationen


    companion object {
        const val WORKOUT_DATA_PATH = "/current_workout_data"
        const val WORKOUT_JSON_KEY = "workout_json_payload"
        private const val TAG = "MyApplication" // Log-Tag
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MyApplication onCreate")
    }

    /**
     * Sendet ein Workout-Objekt an die verbundene Wear OS-App.
     * Muss aus einer Coroutine aufgerufen werden.
     */
    suspend fun sendWorkoutToWear(workout: Workout) {
        try {
            val workoutJson = Json.encodeToString(workout)
            val putDataMapReq = PutDataMapRequest.create(WORKOUT_DATA_PATH).apply {
                dataMap.putString(WORKOUT_JSON_KEY, workoutJson)
                dataMap.putLong("timestamp", System.currentTimeMillis())
            }

            val putDataReq = putDataMapReq.asPutDataRequest().setUrgent()
            Log.d(TAG, "Sende Workout an Pfad $WORKOUT_DATA_PATH: $workoutJson")

            // Konvertiere Task zu Deferred und warte mit await()
            val result = dataClient.putDataItem(putDataReq).await()
            Log.i(TAG, "Workout erfolgreich gesendet")

        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Senden des Workouts: ${e.message}", e)
        }
    }
    // Nicht-suspendierende Hilfsfunktion, um das Senden aus nicht-Coroutine-Kontexten zu starten
    fun triggerWorkoutSend(workout: Workout) {
        applicationScope.launch {
            sendWorkoutToWear(workout)
            Log.d(TAG, "triggerWorkoutSend für ${workout.name} gestartet.")
        }
    }
}