/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.wearos.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.wearos.R
import com.example.wearos.presentation.theme.MyApplicationTheme
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.example.wearos.data.WorkoutDataProcessor
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener {
    private val TAG = "WearDataListener"

    override fun onCreate(savedInstanceState: Bundle?) {
        // SplashScreen so früh wie möglich
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Wearable.getMessageClient(this).addListener(this)
        // Theme vor setContent
        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android")
        }
    }

    override fun onResume() {
        Wearable.getMessageClient(this).addListener(this)
        Log.d(TAG, "onResume: Registriere DataClient listener")
        super.onResume()
        // Listener nur hier hinzufügen
        Wearable.getDataClient(this)
            .addListener(this)
            .addOnSuccessListener { Log.d(TAG, "DataChangedListener registriert") }
            .addOnFailureListener { e -> Log.e(TAG, "Listener konnte nicht registriert werden: $e") }
    }

    override fun onPause() {
        super.onPause()
        Wearable.getMessageClient(this).removeListener(this)
        // Und hier wieder entfernen!
        Wearable.getDataClient(this)
            .removeListener(this)
            .addOnSuccessListener { Log.d(TAG, "DataChangedListener abgemeldet") }
            .addOnFailureListener { e -> Log.e(TAG, "Listener konnte nicht abgemeldet werden: $e") }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "onDataChanged: ${dataEvents.count} Events")
        dataEvents.forEach { event ->
            val path = event.dataItem.uri.path ?: return@forEach
            Log.d(TAG, "  Event-Pfad: $path")
            if (event.type == DataEvent.TYPE_CHANGED) {
                when {
                    path.startsWith("/workout") -> {
                        // dein Workout-Handling…
                    }
                    path == "/ping" -> {
                        val pingMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                        val ts = pingMap.getLong("timestamp")
                        Log.i(TAG, "🏓 Data‑Ping empfangen! timestamp=$ts")
                        Toast.makeText(this, "Data‑Ping: $ts", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        dataEvents.release()
    }
    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.i(TAG, "onMessageReceived: path=${messageEvent.path}")
        val message = String(messageEvent.data)
        Log.i(TAG, "onMessageReceived: message=$message")
        if (messageEvent.path == "/ping") {
            val message = String(messageEvent.data)
            Log.i("PingTest", "Ping empfangen: $message")
        }
        Toast.makeText(this, "Ping erhalten!", Toast.LENGTH_SHORT).show()

    }

}

@Composable
fun WearApp(greetingName: String) {
    MyApplicationTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = "baum"
    )
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}
