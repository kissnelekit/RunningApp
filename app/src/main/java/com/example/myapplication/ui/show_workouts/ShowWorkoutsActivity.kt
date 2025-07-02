package com.example.myapplication.ui.show_workouts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.vector.path
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.Workout
import com.example.myapplication.ui.edit_workout.EditWorkoutActivity
import com.example.myapplication.data.WorkoutDAO
import com.example.myapplication.ui.edit_workout.EditWorkoutActivity.Companion.EXTRA_WORKOUT_ID
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import com.example.myapplication.data.WorkoutDataSerializer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.PutDataRequest

class ShowWorkoutsActivity : AppCompatActivity(), OnWorkoutClickListener {

    private lateinit var recyclerViewWorkouts: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var workoutDAO: WorkoutDAO
    private lateinit var fabAddWorkout: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_workouts)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize DAO (You should get this via dependency injection or a ViewModel)
        workoutDAO = AppDatabase.getDatabase(applicationContext).workoutDao()

        recyclerViewWorkouts = findViewById(R.id.recyclerViewWorkouts)
        //fabAddWorkout = findViewById(R.id.fabAddWorkout)

        // Initialize adapter with an empty list and the click listener
        workoutAdapter = WorkoutAdapter(emptyList(), this)

        recyclerViewWorkouts.adapter = workoutAdapter
        recyclerViewWorkouts.layoutManager = LinearLayoutManager(this)
        /*
        fabAddWorkout.setOnClickListener {
            // Intent to navigate to your CreateWorkoutActivity
            val intent = Intent(this, CreateWorkoutActivity::class.java)
            startActivity(intent)
        }

         */

        loadWorkouts()
    }




    override fun onResume() {
        super.onResume()
        // Reload workouts when the activity resumes, in case new ones were added
        loadWorkouts()
    }
    private fun loadWorkouts() {
        lifecycleScope.launch {
            // This is a suspend function, so it needs to be called in a coroutine
            val workouts = workoutDAO.getAllWorkoutsSortedByDate() // Assuming getAllWorkouts() is your DAO method
            workoutAdapter.updateWorkouts(workouts)
        }
    }
    override fun onToggleWatchStatus(workout: Workout, isChecked: Boolean) {
        Log.d("ShowWorkouts", "Toggle für Workout '${workout.name}', neuer Status: $isChecked")

        // 1. Update den Status in der Datenbank
        workout.isOnWear = isChecked
        lifecycleScope.launch {
            //workoutViewModel.updateWorkout(workout) // Annahme: updateWorkout aktualisiert das ganze Objekt

            // 2. Aktion für die Uhr auslösen
            if (isChecked) {
                // Stelle sicher, dass das Workout-Objekt die Intervalle enthält, bevor es gesendet wird!
                // Wenn 'workout.intervals' nicht bereits geladen ist, lade es hier.
                /*
                if (workout.intervals.isEmpty() && workout.id != 0L) { // Kleine Sicherheitsprüfung
                    val intervals = workoutViewModel.getIntervalsForWorkout(workout.id) // Annahme
                    workout.intervals = intervals // Füge sie dem Objekt hinzu
                }

                 */
                sendWorkoutToWearable(workout)
            } else {
                removeWorkoutFromWearable(workout.id)
            }
        }
    }
    private fun removeWorkoutFromWearable(workoutId: Long)  {
        Log.d("WorkoutSync", "Entferne Workout $workoutId von Wear OS.")
        Toast.makeText(this, "Entferne Workout von Uhr...", Toast.LENGTH_SHORT).show()

        val dataClient = Wearable.getDataClient(this)
        val workoutPath = "${WorkoutDataSerializer.SINGLE_WORKOUT_PATH_PREFIX}${workoutId}"
        val deleteUri =
            Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(workoutPath).build()


        dataClient.deleteDataItems(deleteUri)
            .addOnSuccessListener { Log.i("WorkoutSync", "Lösch-Signal für Workout $workoutId erfolgreich gesendet.") }
            .addOnFailureListener { e -> Log.e("WorkoutSync", "Fehler beim Senden des Lösch-Signals für Workout $workoutId: $e") }
    }


    private fun sendWorkoutToWearable(workout: Workout) {
        val nodeClient = Wearable.getNodeClient(this)

        // 1. Verbundene Nodes asynchron abrufen
        nodeClient.connectedNodes.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val nodes = task.result ?: emptyList()

                if (nodes.isEmpty()) {
                    // 2. Keine verbundenen Geräte
                    Log.e("WorkoutSync", "Keine verbundene Uhr gefunden!")
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Keine Uhr verbunden! Bitte Bluetooth aktivieren.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // 3. Geräte gefunden - Daten senden
                    Log.d("WorkoutSync", "Verbunden mit ${nodes.size} Geräten")
                    sendWorkoutData(workout)
                }
            } else {
                // 4. Fehler bei der Abfrage
                Log.e("WorkoutSync", "Fehler bei Knotenabfrage: ${task.exception?.message}")
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Fehler bei Verbindungsprüfung: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    private fun sendWorkoutData(workout: Workout) {
        val dataClient = Wearable.getDataClient(this)
        Log.d("WorkoutSync", "Versuche Workout ${workout.id} ('${workout.name}') zu senden")

        runOnUiThread {
            Toast.makeText(this, "Sende Workout an Uhr...", Toast.LENGTH_SHORT).show()
        }

        val workoutDataMap = WorkoutDataSerializer.workoutToDataMap(workout)
        val workoutPath = "${WorkoutDataSerializer.SINGLE_WORKOUT_PATH_PREFIX}${workout.id}"

        val putDataMapReq = PutDataMapRequest.create(workoutPath)
        putDataMapReq.dataMap.putAll(workoutDataMap)
        val putDataReq = putDataMapReq.asPutDataRequest().setUrgent()

        dataClient.putDataItem(putDataReq).apply {
            addOnSuccessListener { dataItem ->
                Log.i("WorkoutSync", "Workout erfolgreich gesendet: ${dataItem.uri}")
                runOnUiThread {
                    Toast.makeText(
                        this@ShowWorkoutsActivity,
                        "Workout an Uhr gesendet!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            addOnFailureListener { e ->
                Log.e("WorkoutSync", "Senden fehlgeschlagen: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@ShowWorkoutsActivity,
                        "Fehler beim Senden: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Implementation of the click listener from WorkoutAdapter
    override fun onWorkoutClick(workout: Workout) {
        Log.d("ShowWorkoutsActivity", "Workout clicked: ${workout.id}")
        // Intent to navigate to your EditWorkoutActivity, passing the workout ID
        val intent = Intent(this, EditWorkoutActivity::class.java)
        intent.putExtra(EXTRA_WORKOUT_ID, workout.id) // Assuming Workout has an 'id' property
        startActivity(intent)
    }

}