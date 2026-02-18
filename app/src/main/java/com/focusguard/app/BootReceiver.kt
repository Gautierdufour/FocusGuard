package com.focusguard.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Broadcast reçu: ${intent.action}")

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                startServiceIfEnabled(context)
            }
        }
    }

    private fun startServiceIfEnabled(context: Context) {
        val prefs = context.getSharedPreferences("app_blocker_settings", Context.MODE_PRIVATE)
        val selectedApps = prefs.getStringSet("selected_apps", emptySet()) ?: emptySet()

        if (selectedApps.isNotEmpty()) {
            try {
                val serviceIntent = Intent(context, MonitorService::class.java)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }

                Log.d(TAG, "Service redémarré avec succès")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur lors du redémarrage du service", e)
            }
        } else {
            Log.d(TAG, "Aucune app sélectionnée, service non démarré")
        }
    }
}