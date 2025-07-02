package com.example.myapplication.ui.show_workouts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Workout


interface OnWorkoutClickListener {
    fun onWorkoutClick(workout: Workout)
    fun onToggleWatchStatus(workout: Workout, isChecked: Boolean)
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
        private val isOnWereButton: ToggleButton = itemView.findViewById(R.id.toggleButtonOnWatch)
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
            isOnWereButton.isChecked = workout.isOnWear
            isOnWereButton.setOnCheckedChangeListener { _, isChecked ->
                listener.onToggleWatchStatus(workout, isChecked)
            }
        }
    }
}