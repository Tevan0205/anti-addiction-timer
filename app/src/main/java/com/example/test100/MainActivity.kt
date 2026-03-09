package com.example.test100

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.test100.ui.theme.Test100Theme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Test100Theme {
                MonitorScreen()
            }
        }
    }
}

@Composable
fun MonitorScreen() {

    val context = LocalContext.current

    var currentApp by remember {
        mutableStateOf("unknown")
    }

    Column {

        Button(
            onClick = {

                val intent =
                    Intent(
                        context,
                        MonitorService::class.java
                    )

                context.startService(intent)

            }
        ) {
            Text("開始監控")
        }

        Button(
            onClick = {

                val intent =
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)

                context.startActivity(intent)

            }
        ) {
            Text("開啟權限")
        }

        Text(
            text = "現在使用: $currentApp"
        )

    }

    LaunchedEffect(Unit) {

        val prefs =
            context.getSharedPreferences(
                "settings",
                Context.MODE_PRIVATE
            )

        while (true) {

            currentApp =
                prefs.getString(
                    "currentApp",
                    "none"
                ) ?: "none"

            delay(1000)

        }

    }

}