package com.example.wearos.data

import android.util.Log
import com.google.android.gms.wearable.DataMap

object WorkoutDataProcessor {
    private const val TAG = "WorkoutProcessor"
    private const val PREFIX = "/workout"

    fun process(dataMap: DataMap) {
        val id = dataMap.getLong("workout_id", -1L)
        val name = dataMap.getString("workout_name", "—")
        val dateMs = dataMap.getLong("workout_date", 0L)
        Log.i(TAG, "→ Workout $id – $name @ $dateMs")
        // Hier alle weiteren Keys auslesen und verarbeiten…
    }
}