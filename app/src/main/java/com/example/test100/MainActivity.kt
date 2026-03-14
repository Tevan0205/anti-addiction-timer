package com.example.test100

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
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

    var warningText by remember {
        mutableStateOf(
            prefs.getString("warning", "5") ?: "5"
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

    fun isAccessibilityEnabled(): Boolean {

        val enabled =
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

        return enabled.contains(
            context.packageName
        )
    }

    fun isNotificationEnabled(): Boolean {

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        return manager.areNotificationsEnabled()
    }

    fun isIgnoringBattery(): Boolean {

        val pm =
            context.getSystemService(
                Context.POWER_SERVICE
            ) as PowerManager

        return pm.isIgnoringBatteryOptimizations(
            context.packageName
        )
    }

    Column(
        modifier = Modifier.padding(8.dp)
    ) {

        Text(
            "無障礙：" +
                    if (isAccessibilityEnabled())
                        "已開啟"
                    else
                        "未開啟"
        )

        Text(
            "通知權限：" +
                    if (isNotificationEnabled())
                        "已開啟"
                    else
                        "未開啟"
        )

        Text(
            "省電限制：" +
                    if (isIgnoringBattery())
                        "未限制"
                    else
                        "被限制"
        )

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

        OutlinedTextField(
            value = warningText,
            onValueChange = {

                warningText = it

                prefs.edit()
                    .putString("warning", it)
                    .apply()

            },
            label = { Text("提醒秒數") }
        )

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

        Button(
            onClick = {

                val intent =
                    Intent(
                        Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    )

                intent.putExtra(
                    Settings.EXTRA_APP_PACKAGE,
                    context.packageName
                )

                context.startActivity(intent)

            }
        ) {
            Text("開啟通知權限")
        }

        Button(
            onClick = {

                val intent =
                    Intent(
                        Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                    )

                context.startActivity(intent)

            }
        ) {
            Text("開啟省電設定")
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