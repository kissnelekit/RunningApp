package com.example.myapplication.ui.edit_workout

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
import com.example.myapplication.utils.TimeFormatUtils

// Importiere deinen Adapter und ViewHolder

class EditWorkoutActivity : AppCompatActivity(), IntervalChangeListener {
    companion object {
        const val EXTRA_WORKOUT_ID = "com.example.myapplication.EXTRA_WORKOUT_ID" // Eindeutiger String

        /**
         * Hilfsfunktion zum Erstellen eines Intents für diese Activity im Erstellmodus.
         */
        fun newIntentForCreate(context: Context): Intent {
            return Intent(context, EditWorkoutActivity::class.java)
        }

        /**
         * Hilfsfunktion zum Erstellen eines Intents für diese Activity im Bearbeitungsmodus.
         * @param context Der Context, von dem aus der Intent gestartet wird.
         * @param workoutId Die ID des zu bearbeitenden Workouts.
         */
        fun newIntentForEdit(context: Context, workoutId: Long): Intent {
            val intent = Intent(context, EditWorkoutActivity::class.java)
            intent.putExtra(EXTRA_WORKOUT_ID, workoutId)
            return intent
        }
    }
    private var existingWorkoutId: Long? = null
    private var isEditMode: Boolean = false
    private var workoutToEdit: Workout? = null // Um das geladene Workout zu halten
    private lateinit var title: TextView
    private lateinit var editTextWorkoutName: EditText
    private lateinit var editTextWorkoutDescription: EditText
    private lateinit var workoutDAO: WorkoutDAO
    private lateinit var recyclerViewIntervals: RecyclerView
    private lateinit var intervalAdapter: IntervalAdapter
    private val intervalList = mutableListOf<IntervalView>() // Die Datenquelle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_workout) // Dein Activity-Layout mit der RecyclerView

        recyclerViewIntervals = findViewById(R.id.recyclerViewIntervals) // Deine RecyclerView-ID

        // Initialisiere die Liste (z.B. mit einem Start-Intervall)

        workoutDAO = (application as MyApplication).workoutDao
        intervalAdapter = IntervalAdapter(intervalList, this) // 'this' implementiert IntervalChangeListener
        recyclerViewIntervals.adapter = intervalAdapter
        recyclerViewIntervals.layoutManager = LinearLayoutManager(this)

        // Finde den Button zum Hinzufügen von Intervallen in deinem Activity-Layout
        val buttonAddInterval: Button = findViewById(R.id.buttonAddInterval)
        buttonAddInterval.setOnClickListener {
            intervalAdapter.addInterval(IntervalView(tempUiLabel = "interval_" + intervalList.size + 1))
        }
        title = findViewById(R.id.title)
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
        if (intent.hasExtra(EXTRA_WORKOUT_ID)) {

            existingWorkoutId = intent.getLongExtra(EXTRA_WORKOUT_ID, -1L)
            if (existingWorkoutId != -1L) {
                isEditMode = true
                Log.d("EditWorkout", "Edit-Modus aktiviert mit ID: $existingWorkoutId")
                title.text = "Workout bearbeiten" // Ändere den Titel der Activity
                loadWorkoutData(existingWorkoutId!!)
            } else {
                // Fehlerfall: ID wurde erwartet, aber war ungültig
                isEditMode = false
                title.text = "Workout erstellen"
            }
        } else {
            isEditMode = false
            title.text = "Workout erstellen"
            // Initialisiere UI für neuen Workout (z.B. leere Felder, Standardwerte)
            setupInitialIntervalsIfNeeded() // Falls neue Workouts Standardintervalle haben sollen
            if (intervalList.isEmpty()) { // Nur wenn noch keine Daten vorhanden sind (z.B. bei Konfigurationsänderung)
                intervalList.add(IntervalView(tempUiLabel = "interval_" + 1))
            }
        }

    }
    suspend fun saveWorkout() {
        var length = 0.0
        val currentIntervals = intervalAdapter.getIntervals()
        Log.d("CreateWorkout", "Aktuelle Intervalle beim Speichern: $currentIntervals")

        val dataIntervals: MutableList<Interval> = mutableListOf()
        for (intervalView in currentIntervals) {
            // Hier Annahme: IntervalView hat Eigenschaften duration, minPulse, maxPulse als String
            // und Interval erwartet Long für duration und Int für Pulse.
            // Du musst hier die Konvertierung und Fehlerbehandlung einbauen.
            try {
                val durationLong = TimeFormatUtils.parseDurationToSecondsLong(intervalView.duration) // Eigene Funktion nötig!
                Log.d("CreateWorkout", "durationLong: $durationLong")
                val minPulseInt = intervalView.minPulse.toIntOrNull() ?: 0
                val maxPulseInt = intervalView.maxPulse.toIntOrNull() ?: 0

                dataIntervals.add(
                    Interval(
                        duration = durationLong,
                        minPulse = minPulseInt,
                        maxPulse = maxPulseInt
                    )
                )
                length += durationLong
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



        try {
            if (isEditMode && existingWorkoutId != null) {
                val workout = Workout(
                    id = existingWorkoutId!!,
                    name = workoutName,
                    description = workoutDescription,
                    date = System.currentTimeMillis(),
                    intervals = dataIntervals,
                    length = length,
                    type = RunningType.TEMPO // Oder einen anderen Typ
                )
                workoutDAO.updateWorkout(workout)
                Log.d("CreateWorkout", "Workout erfolgreich aktualisiert.")
                return
            } else {
                val workout = Workout(
                    name = workoutName,
                    description = workoutDescription,
                    date = System.currentTimeMillis(),
                    intervals = dataIntervals,
                    length = length,
                    type = RunningType.TEMPO)
                workoutDAO.insertWorkout(workout)
                Log.d("CreateWorkout", "Workout erfolgreich in DB eingefügt.")
            }

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
    private fun loadWorkoutData(workoutId: Long) {
        // Lade das Workout aus der Datenbank (z.B. über dein ViewModel und DAO)
        // Beispiel mit Coroutine (in einem ViewModel wäre es sauberer)
        lifecycleScope.launch {
            workoutToEdit = workoutDAO.getWorkoutById(workoutId)// Annahme: ViewModel-Methode
            if (workoutToEdit != null) {
                populateUiWithWorkoutData(workoutToEdit!!)
            } else {
                // Fehler: Workout nicht gefunden
                Toast.makeText(this@EditWorkoutActivity, "Workout nicht gefunden", Toast.LENGTH_SHORT).show()
                finish() // Schließe die Activity
            }
        }
    }

    private fun populateUiWithWorkoutData(workout: Workout) {
        editTextWorkoutName.setText(workout.name)
        editTextWorkoutDescription.setText(workout.description)
        for (interval in workout.intervals) {

            Log.d("CreateWorkout", "interval: " + TimeFormatUtils.formatDurationFromSeconds(interval.duration))
            intervalAdapter.addInterval(IntervalView(
                duration = TimeFormatUtils.formatDurationFromSeconds(interval.duration),
                minPulse = interval.minPulse.toString(),
                maxPulse = interval.maxPulse.toString(),
                tempUiLabel = "interval_" + intervalList.size + 1))
        }
    }
    fun setupInitialIntervalsIfNeeded() {

    }
}

