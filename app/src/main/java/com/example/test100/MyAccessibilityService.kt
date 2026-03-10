package com.example.test100

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

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

        prefs.edit()
            .putString(
                "currentApp",
                pkg
            )
            .apply()
    }

    override fun onInterrupt() {}

}