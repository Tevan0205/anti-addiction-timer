package com.example.test100

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.test100.ui.theme.Test100Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apps = getInstalledApps()

        setContent {
            Test100Theme {
                AppListScreen(apps)
            }
        }
    }

    private fun getInstalledApps(): List<AppInfo> {

        val pm = packageManager

        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        return packages
            .filter {

                val isSystem =
                    (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                !isSystem

            }
            .map {

                AppInfo(
                    name = pm.getApplicationLabel(it).toString(),
                    packageName = it.packageName
                )

            }
            .sortedBy { it.name.lowercase() }
    }
}

@Composable
fun AppListScreen(apps: List<AppInfo>) {

    val selected = remember { mutableStateListOf<String>() }

    Column {

        Text(
            text = "選擇要限制的App",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn {

            items(apps) { app ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {

                    Checkbox(
                        checked = selected.contains(app.packageName),
                        onCheckedChange = { isChecked ->

                            if (isChecked) {
                                selected.add(app.packageName)
                            } else {
                                selected.remove(app.packageName)
                            }

                        }
                    )

                    Text(
                        text = app.name,
                        modifier = Modifier.padding(top = 14.dp)
                    )

                }

            }

        }

    }
}