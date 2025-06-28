package com.example.myapplication.data

import kotlin.jvm.JvmSuppressWildcards

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.data.runningtype.RunningType
import com.example.myapplication.data.interval.Interval
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long=0,
    val name: String,
    val description: String,
    val type: RunningType,
    val intervals: @JvmSuppressWildcards List<Interval>
    )
