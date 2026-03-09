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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Test100Theme {
                CurrentAppScreen()
            }
        }
    }
}

@Composable
fun CurrentAppScreen() {

    val context = LocalContext.current

    var currentApp by remember {
        mutableStateOf("unknown")
    }

    Column {

        Button(
            onClick = {

                val intent =
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)

                context.startActivity(intent)

            }
        ) {
            Text("開啟使用情況存取權限")
        }

        Button(
            onClick = {

                currentApp =
                    UsageHelper.getCurrentApp(context)

            }
        ) {
            Text("取得目前App")
        }

        Text(
            text = "現在使用中: $currentApp"
        )

    }

}