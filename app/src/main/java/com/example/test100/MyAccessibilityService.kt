package com.example.test100

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    private var currentApp = ""

    private var seconds = 0

    private val warningTime = 5
    private val limitTime = 10

    private val handler =
        Handler(Looper.getMainLooper())

    private val timerRunnable =
        object : Runnable {

            override fun run() {

                val prefs =
                    getSharedPreferences(
                        "settings",
                        MODE_PRIVATE
                    )

                val selected =
                    prefs.getStringSet(
                        "apps",
                        emptySet()
                    ) ?: emptySet()

                prefs.edit()
                    .putString(
                        "currentApp",
                        currentApp
                    )
                    .apply()

                if (selected.contains(currentApp)) {

                    seconds++

                } else {

                    seconds = 0

                }

                if (selected.contains(currentApp)) {

                    seconds++

                } else {

                    seconds = 0

                }

                if (seconds == warningTime) {

                    showWarningNotification()

                }

                if (selected.contains(currentApp)) {

                    seconds++

                } else {

                    seconds = 0

                }

                if (seconds == warningTime) {

                    showWarningNotification()

                }

                if (
                    seconds >= limitTime &&
                    selected.contains(currentApp)
                ) {

                    val home =
                        Intent(Intent.ACTION_MAIN)

                    home.addCategory(
                        Intent.CATEGORY_HOME
                    )

                    home.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK

                    startActivity(home)

                }

                handler.postDelayed(
                    this,
                    1000
                )
            }
        }

    override fun onServiceConnected() {
        super.onServiceConnected()

        val info = AccessibilityServiceInfo()

        info.eventTypes =
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED

        info.feedbackType =
            AccessibilityServiceInfo.FEEDBACK_GENERIC

        info.notificationTimeout = 100

        serviceInfo = info

        handler.post(timerRunnable)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event == null) return

        val pkg =
            event.packageName?.toString()
                ?: return

        currentApp = pkg
    }

    private fun showWarningNotification() {

        val manager =
            getSystemService(
                NOTIFICATION_SERVICE
            ) as android.app.NotificationManager

        val channelId = "warn"

        val channel =
            android.app.NotificationChannel(
                channelId,
                "warning",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            )

        manager.createNotificationChannel(channel)

        val notification =
            android.app.Notification.Builder(
                this,
                channelId
            )
                .setContentTitle("時間快到了")
                .setContentText("請準備離開")
                .setSmallIcon(
                    android.R.drawable.ic_dialog_alert
                )
                .build()

        manager.notify(
            1,
            notification
        )
    }

    override fun onInterrupt() {}

}