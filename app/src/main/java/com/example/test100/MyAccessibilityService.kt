package com.example.test100

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class MyAccessibilityService : AccessibilityService() {

    private var seconds = 0

    private val warningTime = 5
    private val limitTime = 10

    private var lastApp = ""

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event == null) return

        val pkg =
            event.packageName?.toString()
                ?: return

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

        if (pkg != lastApp) {
            seconds = 0
            lastApp = pkg
        }

        prefs.edit()
            .putString(
                "currentApp",
                pkg
            )
            .apply()

        if (!selected.contains(pkg)) return

        seconds++

        if (seconds == warningTime) {

            Toast.makeText(
                this,
                "快到時間了",
                Toast.LENGTH_SHORT
            ).show()

        }

        if (seconds >= limitTime) {

            val home =
                Intent(Intent.ACTION_MAIN)

            home.addCategory(
                Intent.CATEGORY_HOME
            )

            home.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(home)

        }

    }

    override fun onInterrupt() {}
}