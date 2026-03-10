package com.example.test100

import android.content.Context
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
                MonitorScreen(apps)
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
fun MonitorScreen(apps: List<AppInfo>) {

    val context = LocalContext.current

    val prefs =
        context.getSharedPreferences(
            "settings",
            Context.MODE_PRIVATE
        )

    var currentApp by remember {
        mutableStateOf("unknown")
    }

    var limitText by remember {
        mutableStateOf(
            prefs.getString("limit", "10") ?: "10"
        )
    }

    val selected = remember {

        mutableStateListOf<String>().apply {

            val saved =
                prefs.getStringSet(
                    "apps",
                    emptySet()
                )!!

            addAll(saved)

        }

    }

    Column(
        modifier = Modifier.padding(8.dp)
    ) {

        OutlinedTextField(
            value = limitText,
            onValueChange = {

                limitText = it

                prefs.edit()
                    .putString("limit", it)
                    .apply()

            },
            label = { Text("限制秒數") }
        )

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
                    Intent(
                        context,
                        MonitorService::class.java
                    )

                context.stopService(intent)

            }
        ) {
            Text("停止監控")
        }

        Button(
            onClick = {

                val intent =
                    Intent(
                        Settings.ACTION_USAGE_ACCESS_SETTINGS
                    )

                context.startActivity(intent)

            }
        ) {
            Text("開權限")
        }

        Button(
            onClick = {

                val intent =
                    Intent(
                        Settings.ACTION_ACCESSIBILITY_SETTINGS
                    )

                context.startActivity(intent)

            }
        ) {
            Text("開啟無障礙")
        }

        Text("現在使用: $currentApp")

        LazyColumn(
            modifier = Modifier.height(300.dp)
        ) {

            items(apps) { app ->

                Row {

                    Checkbox(
                        checked =
                            selected.contains(
                                app.packageName
                            ),

                        onCheckedChange = {

                            if (it) {
                                selected.add(
                                    app.packageName
                                )
                            } else {
                                selected.remove(
                                    app.packageName
                                )
                            }

                            prefs.edit()
                                .putStringSet(
                                    "apps",
                                    selected.toSet()
                                )
                                .apply()

                        }
                    )

                    Text(app.name)

                }

            }

        }

    }

    LaunchedEffect(Unit) {

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