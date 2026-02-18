package com.focusguard.app.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import com.focusguard.app.MonitorService

fun isServiceRunning(context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    @Suppress("DEPRECATION")
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (MonitorService::class.java.name == service.service.className) {
            return true
        }
    }
    return false
}

fun hasUsageStatsPermission(context: Context): Boolean {
    return try {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            "android:get_usage_stats",
            android.os.Process.myUid(),
            context.packageName
        )
        mode == android.app.AppOpsManager.MODE_ALLOWED
    } catch (e: Exception) {
        false
    }
}

fun isBatteryOptimized(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return !powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }
    return false
}

fun requestBatteryOptimizationExemption(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            context.startActivity(intent)
        }
    }
}

fun ComponentActivity.cleanupInvalidPreferences() {
    val prefs = getSharedPreferences("app_blocker_settings", Context.MODE_PRIVATE)
    val selectedApps = prefs.getStringSet("selected_apps", emptySet()) ?: emptySet()

    if (selectedApps.isNotEmpty()) {
        val packageManager = packageManager
        val validApps = selectedApps.filter { packageName ->
            try {
                packageManager.getApplicationInfo(packageName, 0)
                true
            } catch (e: Exception) {
                false
            }
        }.toSet()

        if (validApps.size != selectedApps.size) {
            prefs.edit().putStringSet("selected_apps", validApps).apply()
            Log.d("MainActivity", "Nettoyage: ${selectedApps.size - validApps.size} app(s) invalide(s) supprimée(s)")
        }
    }
}
