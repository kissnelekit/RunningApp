package com.example.myapplication.ui.show_workouts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

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

    // Implementation of the click listener from WorkoutAdapter
    override fun onWorkoutClick(workout: Workout) {
        Log.d("ShowWorkoutsActivity", "Workout clicked: ${workout.id}")
        // Intent to navigate to your EditWorkoutActivity, passing the workout ID
        val intent = Intent(this, EditWorkoutActivity::class.java)
        intent.putExtra(EXTRA_WORKOUT_ID, workout.id) // Assuming Workout has an 'id' property
        startActivity(intent)
    }
}