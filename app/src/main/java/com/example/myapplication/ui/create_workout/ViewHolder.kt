package com.example.myapplication.ui.create_workout
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.setText
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.utils.TimeFormatUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.sql.Time

class IntervalEditViewHolder(itemView: View, private val listener: IntervalChangeListener) : RecyclerView.ViewHolder(itemView) {
    private val intervalLabelTextView: TextView = itemView.findViewById(R.id.textViewIntervalLabel)
    val durationEditText: EditText = itemView.findViewById(R.id.editTextIntervalDuration)
    val minPulseEditText: EditText = itemView.findViewById(R.id.editTextMinPulse)
    val maxPulseEditText: EditText = itemView.findViewById(R.id.editTextMaxPulse)
    private val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteInterval)
    private var currentPosition: Int = RecyclerView.NO_POSITION

    // TextWatcher, um Änderungen in den EditText-Feldern zu erfassen
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            // Wichtig: Nur aktualisieren, wenn der ViewHolder noch an ein gültiges Element gebunden ist
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onIntervalDataChanged(
                    adapterPosition,
                    durationEditText.text.toString(),
                    minPulseEditText.text.toString(),
                    maxPulseEditText.text.toString()
                )
            }
        }
    }

    init {
        // Füge die TextWatcher zu den EditText-Feldern hinzu
        durationEditText.addTextChangedListener(textWatcher)
        minPulseEditText.addTextChangedListener(textWatcher)
        maxPulseEditText.addTextChangedListener(textWatcher)
        durationEditText.isFocusable = false
        durationEditText.isClickable = true
        durationEditText.setOnClickListener {
            openDurationPickerDialog(itemView.context)
        }
        deleteButton.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onIntervalDeleted(adapterPosition)
            }
        }
    }
    fun bind(interval: IntervalView, position: Int) {
        currentPosition = position
        intervalLabelTextView.text = interval.tempUiLabel

        // TextWatcher temporär entfernen
        durationEditText.removeTextChangedListener(textWatcher)
        minPulseEditText.removeTextChangedListener(textWatcher)
        maxPulseEditText.removeTextChangedListener(textWatcher)

        durationEditText.setText(interval.duration)
        minPulseEditText.setText(interval.minPulse.toString())
        maxPulseEditText.setText(interval.maxPulse.toString())

        // TextWatcher wieder hinzufügen
        durationEditText.addTextChangedListener(textWatcher)
        minPulseEditText.addTextChangedListener(textWatcher)
        maxPulseEditText.addTextChangedListener(textWatcher)
    }

    private fun openDurationPickerDialog(context: Context) {
        val builder = MaterialAlertDialogBuilder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_duration_picker, null)
        builder.setView(view)

        val hourPicker = view.findViewById<NumberPicker>(R.id.numberPickerHours)
        val minutePicker = view.findViewById<NumberPicker>(R.id.numberPickerMinutes)
        val secondPicker = view.findViewById<NumberPicker>(R.id.numberPickerSeconds)

        // Aktuelle Zeit aus dem EditText parsen
        val currentTime = durationEditText.text.toString()
        val parts = currentTime.split(":")
        var hours = 0
        var minutes = 0
        var seconds = 0

        if (parts.size >= 3) {
            hours = parts[0].toIntOrNull() ?: 0
            minutes = parts[1].toIntOrNull() ?: 0
            seconds = parts[2].toIntOrNull() ?: 0
        } else if (parts.size == 2) {
            minutes = parts[0].toIntOrNull() ?: 0
            seconds = parts[1].toIntOrNull() ?: 0
        } else if (parts.size == 1) {
            seconds = parts[0].toIntOrNull() ?: 0
        }

        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        secondPicker.minValue = 0
        secondPicker.maxValue = 59

        hourPicker.value = hours
        minutePicker.value = minutes
        secondPicker.value = seconds

        // Formatierung mit führenden Nullen
        hourPicker.setFormatter { String.format("%02d", it) }
        minutePicker.setFormatter { String.format("%02d", it) }
        secondPicker.setFormatter { String.format("%02d", it) }

        builder.setPositiveButton("OK") { dialog, _ ->
            val hours = hourPicker.value
            val minutes = minutePicker.value
            val seconds = secondPicker.value

            // Neuen Zeitstring erstellen
            val newDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            durationEditText.setText(newDuration)

            // Änderung an Listener melden
            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onIntervalDataChanged(
                    currentPosition,
                    newDuration,
                    minPulseEditText.text.toString(),
                    maxPulseEditText.text.toString()
                )
            }
        }

        builder.setNegativeButton("Abbrechen", null)
        builder.show()
    }

}


// Interface, um Änderungen an den Adapter/die Activity zurückzumelden
interface IntervalChangeListener {
    fun onIntervalDataChanged(position: Int, duration: String, minPulse: String, maxPulse: String)
    fun onIntervalDeleted(position: Int)
}

