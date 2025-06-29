package com.example.myapplication.ui.show_workouts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Workout
import com.example.myapplication.data.WorkoutDAO


interface OnWorkoutClickListener {
    fun onWorkoutClick(workout: Workout)
}

class WorkoutAdapter(
    private var workouts: List<Workout>,
    private val listener: OnWorkoutClickListener // Add listener
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_workouts, parent, false)
        return WorkoutViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val currentWorkout = workouts[position]
        holder.bind(currentWorkout)
    }

    override fun getItemCount() = workouts.size

    fun updateWorkouts(newWorkouts: List<Workout>) {
        workouts = newWorkouts
        notifyDataSetChanged() // For simplicity. Consider DiffUtil for better performance.
    }

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val workoutNameTextView: TextView = itemView.findViewById(R.id.textViewWorkoutName)
        private val workoutDateTextView: TextView = itemView.findViewById(R.id.textViewWorkoutLength)
        private val workoutTypeTextView: TextView = itemView.findViewById(R.id.textViewWorkoutType)
        // Add other TextViews from list_item_workout.xml if you have them

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onWorkoutClick(workouts[position])
                }
            }
        }

        fun bind(workout: Workout) {
            workoutNameTextView.text = workout.name
            workoutDateTextView.text = workout.length.toString()
            workoutTypeTextView.text = workout.type.toString()
        }
    }
}