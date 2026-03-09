package com.example.test100

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TimerApp()
        }
    }
}

@Composable
fun TimerApp() {

    var minutesText by remember { mutableStateOf("1") }
    var secondsLeft by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var showWarning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {

        Text("防沉迷測試", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = minutesText,
            onValueChange = { minutesText = it },
            label = { Text("分鐘") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val m = minutesText.toIntOrNull() ?: 0
                secondsLeft = m * 60
                running = true
                showWarning = false
            }
        ) {
            Text("開始")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("剩餘秒數: $secondsLeft")

        if (showWarning) {
            Text(
                "時間到！不要再滑短影片了！",
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    if (running) {
        LaunchedEffect(secondsLeft) {

            if (secondsLeft > 0) {

                delay(1000)

                secondsLeft--

            } else {

                running = false
                showWarning = true

            }

        }
    }
}