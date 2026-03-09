package com.example.test100

import android.app.usage.UsageStatsManager
import android.content.Context

object UsageHelper {

    fun getCurrentApp(context: Context): String {

        val usm =
            context.getSystemService(Context.USAGE_STATS_SERVICE)
                    as UsageStatsManager

        val time = System.currentTimeMillis()

        val stats =
            usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                time - 1000 * 10,
                time
            )

        if (stats.isNullOrEmpty()) {
            return "No permission"
        }

        val recent =
            stats.maxByOrNull { it.lastTimeUsed }

        return recent?.packageName ?: "Unknown"
    }

}