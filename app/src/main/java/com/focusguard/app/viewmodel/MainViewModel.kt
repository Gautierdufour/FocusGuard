package com.focusguard.app.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focusguard.app.MonitorService
import com.focusguard.app.ServiceWatchdog
import com.focusguard.app.data.StatsRepository
import com.focusguard.app.utils.hasUsageStatsPermission
import com.focusguard.app.utils.isBatteryOptimized
import com.focusguard.app.utils.isServiceRunning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _serviceRunning = MutableStateFlow(false)
    val serviceRunning: StateFlow<Boolean> = _serviceRunning.asStateFlow()

    private val _batteryOptimized = MutableStateFlow(true)
    val batteryOptimized: StateFlow<Boolean> = _batteryOptimized.asStateFlow()

    private val _selectedApps = MutableStateFlow<Set<String>>(emptySet())
    val selectedApps: StateFlow<Set<String>> = _selectedApps.asStateFlow()

    private val context: Context get() = getApplication()

    init {
        refreshState()
        // Lancer la migration SharedPreferences -> Room au premier démarrage
        viewModelScope.launch {
            try {
                StatsRepository.getInstance(context).migrateFromSharedPreferencesIfNeeded()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Erreur migration Room", e)
            }
        }
    }

    fun refreshState() {
        reloadSelectedApps()
        _serviceRunning.value = isServiceRunning(context)
        _batteryOptimized.value = isBatteryOptimized(context)
    }

    private fun reloadSelectedApps() {
        val prefs = context.getSharedPreferences("app_blocker_settings", Context.MODE_PRIVATE)
        _selectedApps.value = prefs.getStringSet("selected_apps", emptySet()) ?: emptySet()
    }

    fun startService(): Boolean {
        if (!hasUsageStatsPermission(context)) {
            return false
        }

        val intent = Intent(context, MonitorService::class.java)
        context.startForegroundService(intent)
        ServiceWatchdog.enable(context)

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _serviceRunning.value = isServiceRunning(context)
        }, 500)
        return true
    }

    fun stopService() {
        val intent = Intent(context, MonitorService::class.java)
        context.stopService(intent)
        ServiceWatchdog.disable(context)
        _serviceRunning.value = false
    }

    fun cleanupInvalidPreferences() {
        val prefs = context.getSharedPreferences("app_blocker_settings", Context.MODE_PRIVATE)
        val selectedApps = prefs.getStringSet("selected_apps", emptySet()) ?: emptySet()

        if (selectedApps.isNotEmpty()) {
            val packageManager = context.packageManager
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
                Log.d("MainViewModel", "Nettoyage: ${selectedApps.size - validApps.size} app(s) invalide(s) supprimée(s)")
            }
        }
    }
}
