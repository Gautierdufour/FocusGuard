package com.focusguard.app

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

class ServiceWatchdogWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    companion object {
        private const val TAG = "ServiceWatchdog"
        private const val WORK_NAME = "service_watchdog"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<ServiceWatchdogWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )

            Log.d(TAG, "Watchdog programmé")
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Log.d(TAG, "Watchdog annulé")
        }
    }

    override fun doWork(): Result {
        Log.d(TAG, "Vérification du service...")

        val prefs = applicationContext.getSharedPreferences(
            "app_blocker_settings",
            Context.MODE_PRIVATE
        )
        val selectedApps = prefs.getStringSet("selected_apps", emptySet()) ?: emptySet()

        if (selectedApps.isEmpty()) {
            Log.d(TAG, "Aucune app sélectionnée, watchdog désactivé")
            return Result.success()
        }

        if (!isServiceRunning()) {
            Log.w(TAG, "Service arrêté détecté ! Redémarrage...")
            startMonitorService()
        } else {
            Log.d(TAG, "Service actif ✓")
        }

        return Result.success()
    }

    private fun isServiceRunning(): Boolean {
        val manager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        @Suppress("DEPRECATION")
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (MonitorService::class.java.name == service.service.className) {
                return true
            }
        }

        return false
    }

    private fun startMonitorService() {
        try {
            val intent = Intent(applicationContext, MonitorService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(intent)
            } else {
                applicationContext.startService(intent)
            }

            Log.d(TAG, "Service redémarré")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur redémarrage service", e)
        }
    }
}

object ServiceWatchdog {
    fun enable(context: Context) {
        ServiceWatchdogWorker.schedule(context)
    }

    fun disable(context: Context) {
        ServiceWatchdogWorker.cancel(context)
    }
}