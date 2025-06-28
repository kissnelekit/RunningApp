package com.example.myapplication.data.runningtype
import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class RunningTypeConverters {
    @androidx.room.TypeConverter
    fun fromRunningType(value: RunningType): String = value.name

    @androidx.room.TypeConverter
    fun toRunningType(value: String): RunningType = RunningType.valueOf(value)
}