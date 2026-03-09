package com.example.test100

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*

class MonitorService : Service() {

    private val scope =
        CoroutineScope(Dispatchers.Default)

    private var seconds = 0

    private val limit = 10

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        scope.launch {

            while (true) {

                val app =
                    UsageHelper.getCurrentApp(this@MonitorService)

                val prefs =
                    getSharedPreferences(
                        "settings",
                        MODE_PRIVATE
                    )

                prefs.edit()
                    .putString("currentApp", app)
                    .apply()

                if (app.contains("instagram")
                    || app.contains("youtube")
                ) {

                    seconds++

                } else {

                    seconds = 0

                }

                if (seconds >= limit) {

                    val home =
                        Intent(Intent.ACTION_MAIN)

                    home.addCategory(
                        Intent.CATEGORY_HOME
                    )

                    home.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK

                    startActivity(home)

                }

                delay(1000)

            }

        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}