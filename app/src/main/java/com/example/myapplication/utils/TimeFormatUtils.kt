package com.example.myapplication.utils

import android.util.Log

// Funktion zum Parsen des Dauer-Strings
object TimeFormatUtils {
    fun parseDurationStringToComponents(duration: String): Triple<Int, Int, Int> {
        var hours = 0
        var minutes = 0
        var seconds = 0
        val cleanDuration = duration.replace(" ", "").lowercase()
        val pattern = """(?:(\d+)h)?(?:(\d+)m)?(?:(\d+)s)?""".toRegex()
        val matchResult = pattern.matchEntire(cleanDuration)

        if (matchResult != null) {
            hours = matchResult.groups[1]?.value?.toIntOrNull() ?: 0
            minutes = matchResult.groups[2]?.value?.toIntOrNull() ?: 0
            seconds = matchResult.groups[3]?.value?.toIntOrNull() ?: 0
        } else {
            val fallbackSeconds = cleanDuration.toIntOrNull()
            if (fallbackSeconds != null) {
                seconds = fallbackSeconds
                if (seconds >= 60) {
                    minutes = seconds / 60
                    seconds %= 60
                }
                if (minutes >= 60) {
                    hours = minutes / 60
                    minutes %= 60
                }
            } else {
                Log.w("TimeFormatUtils", "Konnte Dauer nicht parsen: $duration")
            }
        }
        return Triple(hours.coerceIn(0, 99), minutes.coerceIn(0, 59), seconds.coerceIn(0, 59))
    }
    fun parseDurationToSecondsLong(duration: String): Long {
        val (h, m, s) = parseDurationStringToComponents(duration)
        return (h * 3600L + m * 60L + s)
    }
    fun parseSecondsLongToDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60
        return formatDurationFromComponents(hours.toInt(), minutes.toInt(), remainingSeconds.toInt())
    }

    // Funktion zum Formatieren der Dauer-Komponenten in einen String
    fun formatDurationFromComponents(hours: Int, minutes: Int, seconds: Int): String {
        val parts = mutableListOf<String>()
        if (hours > 0) parts.add("${hours}h")
        if (minutes > 0) parts.add("${minutes}m")
        // Sekunden immer anzeigen, wenn sonst nichts da ist, oder wenn sie nicht 0 sind und andere Teile existieren
        if (seconds > 0 || parts.isEmpty()) {
            parts.add("${seconds}s")
        }
        return if (parts.isEmpty()) "0s" else parts.joinToString("")
    }

    // Optional: Eine Funktion, die direkt einen Dauer-String in Millisekunden umwandelt (nützlich für interne Speicherung)
    fun parseDurationStringToMillis(duration: String): Long {
        val (h, m, s) = parseDurationStringToComponents(duration)
        return (h * 3600L + m * 60L + s) * 1000L
    }

    // Optional: Eine Funktion, die Millisekunden in einen formatierten Dauer-String umwandelt
    fun formatDurationFromMillis(millis: Long): String {
        if (millis < 0) return "0s" // Oder Fehlerbehandlung
        var remainingMillis = millis
        val hours = remainingMillis / (3600 * 1000)
        remainingMillis %= (3600 * 1000)
        val minutes = remainingMillis / (60 * 1000)
        remainingMillis %= (60 * 1000)
        val seconds = remainingMillis / 1000
        return formatDurationFromComponents(hours.toInt(), minutes.toInt(), seconds.toInt())
    }
}