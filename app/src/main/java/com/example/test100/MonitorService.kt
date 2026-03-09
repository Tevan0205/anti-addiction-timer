package com.example.test100

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*

class MonitorService : Service() {

    private val scope =
        CoroutineScope(Dispatchers.Default)

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        scope.launch {

            while (true) {

                delay(1000)

            }

        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}