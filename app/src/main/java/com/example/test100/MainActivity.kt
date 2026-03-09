package com.example.test100

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

    var monitoring by remember {
        mutableStateOf(false)
    }

    Column {

        Button(
            onClick = {

                val intent =
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)

                context.startActivity(intent)

            }
        ) {
            Text("開啟使用情況權限")
        }

        Button(
            onClick = {

                monitoring = true

            }
        ) {
            Text("開始監控")
        }

        Text(
            text = "現在使用中: $currentApp"
        )

    }

    if (monitoring) {

        LaunchedEffect(Unit) {

            while (true) {

                currentApp =
                    UsageHelper.getCurrentApp(context)

                delay(1000)

            }

        }

    }

}