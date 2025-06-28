package com.example.myapplication.ui.create_workout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MyApplication
import com.example.myapplication.R // Für Layout-IDs
import com.example.myapplication.data.WorkoutDAO
import com.example.myapplication.data.interval.Interval
import com.example.myapplication.data.Workout
import com.example.myapplication.data.runningtype.RunningType
import kotlinx.coroutines.launch
import kotlin.text.toIntOrNull

// Importiere deinen Adapter und ViewHolder

class CreateWorkoutActivity : AppCompatActivity(), IntervalChangeListener {
    private lateinit var editTextWorkoutName: EditText
    private lateinit var editTextWorkoutDescription: EditText
    private lateinit var workoutDAO: WorkoutDAO
    private lateinit var recyclerViewIntervals: RecyclerView
    private lateinit var intervalAdapter: IntervalAdapter
    private val intervalList = mutableListOf<IntervalView>() // Die Datenquelle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_workout) // Dein Activity-Layout mit der RecyclerView

        recyclerViewIntervals = findViewById(R.id.recyclerViewIntervals) // Deine RecyclerView-ID

        // Initialisiere die Liste (z.B. mit einem Start-Intervall)
        if (intervalList.isEmpty()) { // Nur wenn noch keine Daten vorhanden sind (z.B. bei Konfigurationsänderung)
            intervalList.add(IntervalView(tempUiLabel = "interval_" + 1))
        }
        workoutDAO = (application as MyApplication).workoutDao
        intervalAdapter = IntervalAdapter(intervalList, this) // 'this' implementiert IntervalChangeListener
        recyclerViewIntervals.adapter = intervalAdapter
        recyclerViewIntervals.layoutManager = LinearLayoutManager(this)

        // Finde den Button zum Hinzufügen von Intervallen in deinem Activity-Layout
        val buttonAddInterval: Button = findViewById(R.id.buttonAddInterval)
        buttonAddInterval.setOnClickListener {
            intervalAdapter.addInterval(IntervalView(tempUiLabel = "interval_" + intervalList.size + 1))
        }
        editTextWorkoutName = findViewById(R.id.editTextWorkoutName)
        editTextWorkoutDescription = findViewById(R.id.editTextWorkoutDescription)

        // Finde den Button zum Speichern des Workouts
        val buttonSaveWorkout: Button = findViewById(R.id.buttonSaveWorkout)
        buttonSaveWorkout.setOnClickListener {
            lifecycleScope.launch {
                saveWorkout()
                Log.d("CreateWorkout", "Workout saved")
                finish() // Optional: Beende die Activity nach dem Speichern
            }
        }
    }
    suspend fun saveWorkout() {
        val currentIntervals = intervalAdapter.getIntervals()
        Log.d("CreateWorkout", "Aktuelle Intervalle beim Speichern: $currentIntervals")

        val dataIntervals: MutableList<Interval> = mutableListOf()
        for (intervalView in currentIntervals) {
            // Hier Annahme: IntervalView hat Eigenschaften duration, minPulse, maxPulse als String
            // und Interval erwartet Long für duration und Int für Pulse.
            // Du musst hier die Konvertierung und Fehlerbehandlung einbauen.
            try {
                val durationLong = parseDurationToLong(intervalView.duration) // Eigene Funktion nötig!
                val minPulseInt = intervalView.minPulse.toIntOrNull() ?: 0
                val maxPulseInt = intervalView.maxPulse.toIntOrNull() ?: 0

                dataIntervals.add(
                    Interval(
                        duration = durationLong,
                        minPulse = minPulseInt,
                        maxPulse = maxPulseInt
                    )
                )
            } catch (e: NumberFormatException) {
                Log.e("CreateWorkout", "Fehler beim Parsen der Intervalldaten für: $intervalView", e)
                // Handle den Fehler, z.B. zeige eine Nachricht dem Benutzer
                // return // Beende das Speichern, wenn ein Fehler auftritt
            }
        }

        val workoutName = editTextWorkoutName.text.toString()
        val workoutDescription = editTextWorkoutDescription.text.toString()

        // Validierung (Beispiel)
        if (workoutName.isBlank()) {
            Log.e("CreateWorkout", "Workout-Name darf nicht leer sein.")
            // Zeige dem Benutzer eine Fehlermeldung (z.B. Toast oder Snackbar)
            // editTextWorkoutName.error = "Name erforderlich"
            return // Beende die Funktion hier
        }

        val workout = Workout(
            name = workoutName,
            description = workoutDescription,
            intervals = dataIntervals,
            type = RunningType.TEMPO // Oder einen anderen Typ
        )

        try {
            workoutDAO.insertWorkout(workout)
            Log.d("CreateWorkout", "Workout erfolgreich in DB eingefügt.")
        } catch (e: Exception) {
            Log.e("CreateWorkout", "Fehler beim Einfügen des Workouts in die DB.", e)
            // Handle DB-Fehler
        }
    }

    // Implementierung der Methoden aus IntervalChangeListener
    /*
    override fun onIntervalDataChanged(position: Int, duration: String, minPulse: Int, maxPulse: Int) {
        intervalAdapter.updateIntervalData(position, duration, minPulse, maxPulse)
        // Optional: Hier könntest du zusätzliche Logik ausführen, wenn sich Daten ändern
        Log.d("CreateWorkout", "Intervall $position geändert: D=$duration, MinP=$minPulse, MaxP=$maxPulse")
    }

     */

    override fun onIntervalDataChanged(position: Int, duration: String, minPulse: String, maxPulse: String) {
        intervalAdapter.updateIntervalData(position, duration, minPulse, maxPulse)
    }

    override fun onIntervalDeleted(position: Int) {
        intervalAdapter.removeInterval(position)
        Log.d("CreateWorkout", "Intervall $position gelöscht")
    }
    fun parseDurationToLong(durationString: String): Long {
        val parts = durationString.split(" ")
        var duration = 0L
        for (part in parts) {
            val value = part.substring(0, part.length - 1).toLongOrNull()
            if (value != null) {
                when (part.last()) {
                    's' -> duration += value * 1000 //
                    'm' -> duration += value * 60000
                    'h' -> duration += value * 3600000
                }
            }
        }
        return duration
    }
}

