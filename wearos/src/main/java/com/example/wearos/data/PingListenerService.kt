package com.example.wearos.data

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class PingListenerService : WearableListenerService() {
    private val TAG = "PingListenerSvc"

    override fun onMessageReceived(event: MessageEvent) {
        Log.d(TAG, "onMessageReceived: path=${event.path}, data=${String(event.data)}")
        if (event.path == "/ping") {
            Log.i(TAG, "Ping empfangen: ${String(event.data)}")
        }
    }
}