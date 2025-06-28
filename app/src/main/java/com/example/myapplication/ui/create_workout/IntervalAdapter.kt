package com.example.myapplication.ui.create_workout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
class IntervalAdapter(
    private val intervals: MutableList<IntervalView>,
    private val listener: IntervalChangeListener // Das gleiche Listener-Interface
) : RecyclerView.Adapter<IntervalEditViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntervalEditViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_interval_edit, parent, false)
        return IntervalEditViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: IntervalEditViewHolder, position: Int) {
        val interval = intervals[position]
        // Aktualisiere das temporäre Label für die UI
        interval.tempUiLabel = "Intervall ${position + 1}"
        holder.bind(interval, position)
    }

    override fun getItemCount(): Int = intervals.size

    // Methoden zum Modifizieren der Liste (Beispiele)
    fun addInterval(interval: IntervalView) {
        intervals.add(interval)
        notifyItemInserted(intervals.size - 1)
        // Stelle sicher, dass nach dem Hinzufügen auch die Labels neu gebunden werden,
        // falls sich Positionsnummern ändern. notifyDataSetChanged() ist die einfachste,
        // aber nicht die performanteste Lösung. Besser: notifyItemRangeChanged().
        notifyItemRangeChanged(0, intervals.size) // Um alle Labels neu zu rendern
    }

    fun removeInterval(position: Int) {
        if (position >= 0 && position < intervals.size) {
            intervals.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, intervals.size - position) // Aktualisiere Labels der nachfolgenden Items
        }
    }

    fun updateIntervalData(position: Int, duration: String, minPulse: String, maxPulse: String) {
        if (position >= 0 && position < intervals.size) {
            val interval = intervals[position]
            interval.duration = duration
            interval.minPulse = minPulse
            interval.maxPulse = maxPulse
            // Kein notifyItemChanged nötig, da der ViewHolder die Daten schon hat.
            // Die Daten sind jetzt in der 'intervals'-Liste im Adapter aktuell.
        }
    }

    fun getIntervals(): List<IntervalView> {
        return intervals.toList() // Gibt eine Kopie zurück, um externe Modifikationen zu vermeiden
    }
}