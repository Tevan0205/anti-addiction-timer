package com.example.test100

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*

class MonitorService : Service() {

    companion object {
        var isRunning = false
    }

    private val scope =
        CoroutineScope(Dispatchers.Default)

    private var seconds = 0

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (isRunning) {
            return START_STICKY
        }

        isRunning = true

        scope.launch {

            while (true) {

                val prefs =
                    getSharedPreferences(
                        "settings",
                        MODE_PRIVATE
                    )

                val app =
                    UsageHelper.getCurrentApp(this@MonitorService)

                val limit =
                    prefs.getString(
                        "limit",
                        "10"
                    )?.toIntOrNull() ?: 10

                val selected =
                    prefs.getStringSet(
                        "apps",
                        emptySet()
                    ) ?: emptySet()

                prefs.edit()
                    .putString(
                        "currentApp",
                        app
                    )
                    .apply()

                if (selected.contains(app)) {

                    seconds++

                } else {

                    seconds = 0

                }

                if (seconds >= limit &&
                    selected.contains(app)
                ) {

                    val home =
                        Intent(Intent.ACTION_MAIN)

                    home.addCategory(
                        Intent.CATEGORY_HOME)

                    home.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK

                    startActivity(home)

                }

                delay(1000)

            }

        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}