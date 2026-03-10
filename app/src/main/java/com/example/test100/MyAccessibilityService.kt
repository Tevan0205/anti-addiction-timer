package com.example.test100

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import kotlinx.coroutines.*

class MyAccessibilityService : AccessibilityService() {

    private val scope =
        CoroutineScope(Dispatchers.Default)

    private var currentApp = ""

    private var seconds = 0

    private val warningTime = 5
    private val limitTime = 10

    override fun onServiceConnected() {
        super.onServiceConnected()

        scope.launch {

            while (true) {

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

                if (seconds == warningTime) {

                    Toast.makeText(
                        this@MyAccessibilityService,
                        "快到時間",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                if (seconds >= limitTime &&
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

                delay(1000)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event == null) return

        val pkg =
            event.packageName?.toString()
                ?: return

        currentApp = pkg
    }

    override fun onInterrupt() {}

}