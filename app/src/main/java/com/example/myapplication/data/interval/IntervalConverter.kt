package com.example.myapplication.data.interval
import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class IntervalConverters {
    @TypeConverter
    fun fromIntervalList(value: List<Interval>): String = Json.encodeToString(value)

    @TypeConverter
    fun toIntervalList(value: String): List<Interval> = Json.decodeFromString(value)
}