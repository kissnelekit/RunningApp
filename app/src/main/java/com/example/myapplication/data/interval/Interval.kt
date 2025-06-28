package com.example.myapplication.data.interval

import kotlinx.serialization.Serializable

@Serializable
data class Interval(
    val duration: Long,
    val minPulse: Int,
    val maxPulse: Int,
)