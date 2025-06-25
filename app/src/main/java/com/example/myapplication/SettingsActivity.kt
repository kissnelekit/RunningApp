package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.SettingsActivityBinding// Stelle sicher, dass du ViewBinding aktiviert hast
import kotlinx.coroutines.flow.first // Zum einmaligen Lesen des aktuellen Werts aus dem Flow
import kotlinx.coroutines.launch
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding
    private lateinit var appSettingsManager: AppSettingsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val returnButton: FloatingActionButton = findViewById(R.id.return_button)
        returnButton.setOnClickListener { view ->
            finish()
        }
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hole die Instanz des AppSettingsManager von deiner Application-Klasse
        appSettingsManager = (application as MyApplication).appSettingsManager

        // Lade die aktuellen Einstellungen und setze die UI-Elemente
        loadAndDisplaySettings()

        // Listener für den Speicher-Button
        binding.buttonSaveSettings.setOnClickListener {
            saveSettings()
        }
    }
    private fun loadAndDisplaySettings() {
        lifecycleScope.launch {
            // Starte eine Coroutine im Lifecycle-Scope der Activity
            // Lese den aktuellen Wert für Benachrichtigungen
            val notificationsEnabled = appSettingsManager.notificationsEnabledFlow.first()
            binding.switchNotifications.isChecked = notificationsEnabled

            // Lese den aktuellen Nutzernamen
            val userName = appSettingsManager.userNameFlow.first()
            binding.editTextUserName.setText(userName)
        }
    }

    private fun saveSettings() {
        lifecycleScope.launch {
            // Speichere den Zustand des Switches
            val notificationsEnabled = binding.switchNotifications.isChecked
            appSettingsManager.setNotificationsEnabled(notificationsEnabled)

            // Speichere den eingegebenen Nutzernamen
            val userName = binding.editTextUserName.text.toString()
            appSettingsManager.setUserName(userName)

            Toast.makeText(this@SettingsActivity, "Einstellungen gespeichert!", Toast.LENGTH_SHORT).show()
            // Optional: Activity beenden oder zur vorherigen zurückkehren
            finish()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}