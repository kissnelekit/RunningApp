package com.example.wearos.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Text
import com.example.wearos.presentation.theme.MyApplicationTheme
import com.google.android.gms.wearable.*
class TestReceiverActivity : ComponentActivity(), DataClient.OnDataChangedListener {

    private val TAG = "TestReceiver"
    private var statusText by mutableStateOf("Warte auf Daten...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TestReceiverApp(statusText)
        }
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
        Log.d(TAG, "Listener registriert")
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            statusText = if (nodes.isEmpty()) {
                "Keine Verbindung zum Handy!"
            } else {
                val phoneNames = nodes.joinToString { it.displayName }
                "Verbunden mit: $phoneNames"
            }
        }.addOnFailureListener {
            statusText = "Verbindungsfehler: ${it.message}"
        }
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
        Log.d(TAG, "Listener entfernt")
    }
    /*
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "Datenänderung empfangen: ${dataEvents.count} Ereignisse")

        dataEvents.forEach { event ->
            val path = event.dataItem.uri.path ?: ""
            Log.d(TAG, "Pfad: $path")

            if (path.startsWith("/workout") && event.type == DataEvent.TYPE_CHANGED) {
                try {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val workoutName = dataMap.getString("workout_name", "Unbekannt")

                    runOnUiThread {
                        statusText = "Empfangen: $workoutName"
                    }

                    Log.i(TAG, "Workout '$workoutName' erfolgreich verarbeitet")
                } catch (e: Exception) {
                    Log.e(TAG, "Fehler bei der Datenverarbeitung", e)
                    statusText = "Fehler: ${e.message}"
                }
            }
        }
        dataEvents.release()
    }
     */
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            val path = event.dataItem.uri.path ?: ""
            if (path == "/test_path" && event.type == DataEvent.TYPE_CHANGED) {
                val data = DataMapItem.fromDataItem(event.dataItem).dataMap
                val message = data.getString("test_key", "Keine Daten")
                val timestamp = data.getLong("timestamp", 0)

                statusText = "Testdaten: $message @ $timestamp"
            }
        }
        dataEvents.release()
    }
}


@Composable
fun TestReceiverApp(status: String) {
    MyApplicationTheme {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = status
            )
        }
    }
}