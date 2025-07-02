package com.example.myapplication.data

import androidx.compose.ui.input.key.type
import com.google.android.gms.wearable.DataMap
import com.example.myapplication.data.Workout
import com.example.myapplication.data.interval.Interval
// import com.example.myapplication.data.RunningType // <--- FALLS DU EIN ENUM FÜR RunningType HAST

object WorkoutDataSerializer {

    // Schlüssel für das Haupt-Workout DataMap
    const val KEY_WORKOUT_ID = "workout_id"
    const val KEY_WORKOUT_NAME = "workout_name"
    const val KEY_WORKOUT_DESCRIPTION = "workout_description"
    const val KEY_WORKOUT_DATE = "workout_date"       // Annahme: Long (Timestamp in Millisekunden)
    const val KEY_WORKOUT_TYPE = "workout_type"       // Annahme: String (z.B. Name des Enums oder ein Typ-String)
    const val KEY_WORKOUT_LENGTH = "workout_length"   // Annahme: Double oder Float (z.B. Distanz)
    const val KEY_WORKOUT_TOTAL_DURATION_SECONDS = "workout_total_duration_seconds" // Annahme: Long

    // Schlüssel für die Liste der Intervalle
    const val KEY_WORKOUT_INTERVALS = "workout_intervals" // Wird eine ArrayList<DataMap> sein

    // Schlüssel für jedes Intervall-DataMap
    const val KEY_INTERVAL_ID = "interval_id"           // Eindeutige ID des Intervalls (Long)
    const val KEY_INTERVAL_WORKOUT_ID = "interval_workout_id" // Fremdschlüssel zum Workout (Long)
    const val KEY_INTERVAL_ORDER = "interval_order"       // Reihenfolge (Int)
    const val KEY_INTERVAL_NAME = "interval_name"         // Name des Intervalls (String, optional)
    const val KEY_INTERVAL_DURATION_SECONDS = "interval_duration_seconds" // Dauer des Intervalls in Sekunden (Long)
    const val KEY_INTERVAL_TYPE = "interval_type"         // Typ des Intervalls (String, z.B. "Warmup", "Run", "CoolDown")
    const val KEY_INTERVAL_MIN_PULSE = "interval_min_pulse" // String
    const val KEY_INTERVAL_MAX_PULSE = "interval_max_pulse" // String
    const val KEY_INTERVAL_DISTANCE_METERS = "interval_distance_meters" // Distanz in Metern (Int oder Long, optional)
    // Füge hier weitere Schlüssel für Interval-Eigenschaften hinzu, falls nötig

    // Pfad-Präfix für einzelne Workout-DataItem-Objekte
    const val SINGLE_WORKOUT_PATH_PREFIX = "/workout"


    fun workoutToDataMap(workout: Workout): DataMap {
        val workoutDataMap = DataMap()
        workoutDataMap.putLong(KEY_WORKOUT_ID, workout.id)
        workoutDataMap.putString(KEY_WORKOUT_NAME, workout.name)
        workoutDataMap.putString(KEY_WORKOUT_DESCRIPTION, workout.description ?: "")
        workoutDataMap.putLong(KEY_WORKOUT_DATE, workout.date) // Stelle sicher, dass 'workout.date' ein Long ist
        workoutDataMap.putString(KEY_WORKOUT_TYPE, workout.type.name) // Annahme: workout.type ist ein Enum und du speicherst den Namen
        workoutDataMap.putDouble(KEY_WORKOUT_LENGTH, workout.length) // Stelle sicher, workout.length ist Double/Float
        //workoutDataMap.putLong(KEY_WORKOUT_TOTAL_DURATION_SECONDS, workout.totalDurationSeconds) // Annahme

        val intervalDataMaps = ArrayList<DataMap>()

        workout.intervals.forEachIndexed { index, interval ->
            val intervalMap = DataMap()
            intervalMap.putInt(KEY_INTERVAL_ORDER, index)
            intervalMap.putLong(KEY_INTERVAL_DURATION_SECONDS, interval.duration) // Annahme: interval.duration ist Long (Sekunden)
            //intervalMap.putString(KEY_INTERVAL_TYPE, interval.type ?: "")
            intervalMap.putInt(KEY_INTERVAL_MIN_PULSE, interval.minPulse)
            intervalMap.putInt(KEY_INTERVAL_MAX_PULSE, interval.maxPulse)
            // intervalMap.putInt(KEY_INTERVAL_DISTANCE_METERS, interval.distanceMeters) // Falls vorhanden

            intervalDataMaps.add(intervalMap)
        }
        workoutDataMap.putDataMapArrayList(KEY_WORKOUT_INTERVALS, intervalDataMaps)

        return workoutDataMap
    }
}