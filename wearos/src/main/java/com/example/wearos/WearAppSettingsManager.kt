package com.example.wearos

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
/*
val Context.appSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
class WearAppSettingsManager (private val dataStore: DataStore<Preferences>){// 3. Definiere die Schlüssel für deine Einstellungen
//    Es ist gute Praxis, sie in einem privaten Objekt zu gruppieren.
private object PreferencesKeys {
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val USER_NAME = stringPreferencesKey("user_name")
    // Füge hier später weitere Schlüssel für andere Einstellungen hinzu
}

    // --- Beispiel: Benachrichtigungen aktiviert (Boolean) ---

    // Flow zum Lesen der "Notifications Enabled"-Einstellung
    val notificationsEnabledFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            // IOException wird geworfen, wenn beim Lesen der Daten ein Fehler auftritt.
            if (exception is IOException) {
                emit(emptyPreferences()) // Bei Fehler leere Preferences ausgeben
            } else {
                throw exception // Andere Fehler weiterwerfen
            }
        }
        .map { preferences ->
            // Hole den Boolean-Wert. Gib 'false' als Standardwert zurück, falls der Schlüssel nicht existiert.
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true // Standardmäßig aktiviert
        }

    // Funktion zum Speichern der "Notifications Enabled"-Einstellung
    suspend fun setNotificationsEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = isEnabled
        }
    }

    // --- Beispiel: Nutzername (String) ---

    // Flow zum Lesen der "User Name"-Einstellung
    val userNameFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_NAME] ?: "Gast" // Standardwert "Gast"
        }

    // Funktion zum Speichern der "User Name"-Einstellung
    suspend fun setUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    // Du kannst hier weitere Flows und suspend-Funktionen für andere Einstellungen hinzufügen
}

 */