package com.example.wearos.data

import android.util.Log
import com.google.android.gms.wearable.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DataLayerListenerService : WearableListenerService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    companion object {
        private const val TAG = "WearDataListener"
        // Passe diese Pfade an die Konstanten in deiner Mobile-App an!
        const val SINGLE_WORKOUT_PATH_PREFIX = "/workout"
        // Schlüssel für DataMap (idealerweise aus einem geteilten Modul)
        const val KEY_WORKOUT_ID = "workout_id"
        const val KEY_WORKOUT_NAME = "workout_name"
        const val KEY_WORKOUT_DESCRIPTION = "workout_description"
        const val KEY_WORKOUT_DATE = "workout_date"
        const val KEY_WORKOUT_TYPE = "workout_type"
        const val KEY_WORKOUT_LENGTH = "workout_length"
        const val KEY_WORKOUT_INTERVALS = "workout_intervals"

        const val KEY_INTERVAL_ID = "interval_id"
        const val KEY_INTERVAL_ORDER = "interval_order"
        const val KEY_INTERVAL_DURATION_MILLIS = "interval_duration_millis"
        const val KEY_INTERVAL_MIN_PULSE = "interval_min_pulse"
        const val KEY_INTERVAL_MAX_PULSE = "interval_max_pulse"
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "onDataChanged - Anzahl Ereignisse: ${dataEvents.count}")
        dataEvents.forEach { event ->
            val uri = event.dataItem.uri
            val path = uri.path ?: ""
            Log.d(TAG, "Event: Typ=${event.type}, URI='${uri}', Path='${path}'")

            if (event.type == DataEvent.TYPE_CHANGED) {
                if (path.startsWith(SINGLE_WORKOUT_PATH_PREFIX)) {
                    val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                    val workoutDataMap = dataMapItem.dataMap
                    Log.i(TAG, "Workout Daten empfangen für Pfad: $path")
                    processWorkoutData(workoutDataMap)
                }
            } else if (event.type == DataEvent.TYPE_DELETED) {
                if (path.startsWith(SINGLE_WORKOUT_PATH_PREFIX)) {
                    val workoutIdString = path.substringAfter(SINGLE_WORKOUT_PATH_PREFIX)
                    Log.i(TAG, "Workout Lösch-Signal empfangen für Pfad: $path, ID-String: $workoutIdString")
                    // TODO: Logik zum Löschen des Workouts auf der Uhr anhand der workoutIdString
                }
            }
        }
        dataEvents.release()
    }

    private fun processWorkoutData(dataMap: DataMap) {
        val workoutId = dataMap.getLong(KEY_WORKOUT_ID, -1L)
        val workoutName = dataMap.getString(KEY_WORKOUT_NAME, "N/A")
        val description = dataMap.getString(KEY_WORKOUT_DESCRIPTION, "")
        val date = dataMap.getLong(KEY_WORKOUT_DATE, 0L)
        val type = dataMap.getString(KEY_WORKOUT_TYPE, "N/A")
        val length = dataMap.getDouble(KEY_WORKOUT_LENGTH, 0.0)

        Log.i(TAG, "----- Empfangenes Workout -----")
        Log.i(TAG, "ID: $workoutId")
        Log.i(TAG, "Name: $workoutName")
        Log.i(TAG, "Beschreibung: $description")
        Log.i(TAG, "Datum (ms): $date")
        Log.i(TAG, "Typ: $type")
        Log.i(TAG, "Länge: $length")

        val intervalDataMaps = dataMap.getDataMapArrayList(KEY_WORKOUT_INTERVALS)
        if (intervalDataMaps != null) {
            Log.i(TAG, "Anzahl Intervalle: ${intervalDataMaps.size}")
            intervalDataMaps.forEachIndexed { index, intervalMap ->
                val intervalOrderId = intervalMap.getLong(KEY_INTERVAL_ID, -1L)
                val intervalOrder = intervalMap.getInt(KEY_INTERVAL_ORDER, -1)
                val durationMillis = intervalMap.getLong(KEY_INTERVAL_DURATION_MILLIS, 0L)
                val minPulse = intervalMap.getString(KEY_INTERVAL_MIN_PULSE, "N/A")
                val maxPulse = intervalMap.getString(KEY_INTERVAL_MAX_PULSE, "N/A")
                Log.i(TAG, "  Intervall ${index + 1}: ID=$intervalOrderId, Order=$intervalOrder, Dauer(ms)=$durationMillis, Puls=$minPulse-$maxPulse")
            }
        } else {
            Log.i(TAG, "Keine Intervalle empfangen.")
        }
        Log.i(TAG, "-----------------------------")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}