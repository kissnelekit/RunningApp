package com.example.myapplication.ui.edit_workout

data class IntervalView(

    var duration: String = "0s", // Hier wird die Dauer als String gespeichert,
    var minPulse: String = "0",
    var maxPulse: String = "0",
    var tempUiLabel: String // Hier wird das temporäre Label gespeichert
)
