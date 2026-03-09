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

    Column {

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

        if (selected.contains(currentApp)) {
            Text("受限制: YES")
        } else {
            Text("受限制: NO")
        }

        LazyColumn(
            modifier = Modifier.height(300.dp)
        ) {

            items(apps) { app ->

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

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