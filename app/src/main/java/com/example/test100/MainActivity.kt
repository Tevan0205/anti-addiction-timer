package com.example.test100

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.test100.ui.theme.Test100Theme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apps = getLaunchableApps()

        setContent {
            Test100Theme {
                MonitorWithListScreen(apps)
            }
        }
    }

    private fun getLaunchableApps(): List<AppInfo> {

        val pm = packageManager

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val resolveInfos = pm.queryIntentActivities(intent, 0)

        return resolveInfos.map {

            AppInfo(
                name = it.loadLabel(pm).toString(),
                packageName = it.activityInfo.packageName
            )

        }.sortedBy { it.name }
    }
}

@Composable
fun MonitorWithListScreen(apps: List<AppInfo>) {

    val context = LocalContext.current

    val selected = remember {
        mutableStateListOf<String>()
    }

    var currentApp by remember {
        mutableStateOf("unknown")
    }

    var monitoring by remember {
        mutableStateOf(false)
    }

    var seconds by remember {
        mutableStateOf(0)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var limitText by remember {
        mutableStateOf("10")
    }

    val limit =
        limitText.toIntOrNull() ?: 10

    Column {

        OutlinedTextField(
            value = limitText,
            onValueChange = { limitText = it },
            label = { Text("限制秒數") }
        )

        Button(
            onClick = {
                val intent =
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                context.startActivity(intent)
            }
        ) {
            Text("開啟權限")
        }

        Button(
            onClick = {
                monitoring = true
            }
        ) {
            Text("開始監控")
        }

        Text("現在使用: $currentApp")

        val restricted =
            selected.contains(currentApp)

        if (restricted) {
            Text("受限制: YES")
        } else {
            Text("受限制: NO")
        }

        Text("使用時間: $seconds 秒")

        LazyColumn(
            modifier = Modifier.height(300.dp)
        ) {

            items(apps) { app ->

                Row {

                    Checkbox(
                        checked =
                            selected.contains(app.packageName),

                        onCheckedChange = {

                            if (it) {
                                selected.add(app.packageName)
                            } else {
                                selected.remove(app.packageName)
                            }

                        }
                    )

                    Text(app.name)

                }

            }

        }

    }

    if (showDialog) {

        AlertDialog(
            onDismissRequest = { },

            confirmButton = {

                Button(
                    onClick = {
                        showDialog = false
                        seconds = 0
                    }
                ) {
                    Text("知道了")
                }

            },

            title = {
                Text("超過限制")
            },

            text = {
                Text("已強制返回桌面")
            }
        )

    }

    if (monitoring) {

        LaunchedEffect(Unit) {

            while (true) {

                val app =
                    UsageHelper.getCurrentApp(context)

                currentApp = app

                if (selected.contains(app)) {
                    seconds++
                } else {
                    seconds = 0
                }

                if (seconds >= limit && selected.contains(app)) {

                    showDialog = true

                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    context.startActivity(intent)

                }

                delay(1000)

            }

        }

    }
}