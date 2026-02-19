package com.focusguard.app

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import com.focusguard.app.utils.AppDisplayNames
import kotlinx.coroutines.*

class MonitorService : Service() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)
    @Volatile
    private var isMonitoring = false
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        private const val TAG = "MonitorService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "app_blocker_channel"
        private const val FOREGROUND_NOTIFICATION_ID = 2
        private const val ALERT_CHANNEL_ID = "app_blocker_alerts"

        // Action pour recharger la liste d'apps sans redémarrer le service
        const val ACTION_REFRESH_BLOCKED_APPS = "com.focusguard.app.REFRESH_BLOCKED_APPS"

        // Intervalles de monitoring
        private const val MONITOR_INTERVAL_MS = 500L
        private const val MONITOR_ERROR_INTERVAL_MS = 2000L
        private const val MONITOR_PAUSE_ON_ERRORS_MS = 30_000L
        private const val MAX_CONSECUTIVE_ERRORS = 10
        private const val USAGE_EVENTS_WINDOW_MS = 3000L
        private const val MONITOR_WAIT_FOR_APPS_MS = 3000L // Attente si aucune app sélectionnée

        // Wake lock
        private const val WAKE_LOCK_TIMEOUT_MS = 10 * 60 * 1000L // 10 minutes

        // Restart delay
        private const val SERVICE_RESTART_DELAY_MS = 1000L
    }

    // Applications à surveiller
    @Volatile
    private var blockedApps: Set<String> = setOf()
    private val prefs by lazy { getSharedPreferences("lock", Context.MODE_PRIVATE) }
    private val settingsPrefs by lazy { getSharedPreferences("app_blocker_settings", Context.MODE_PRIVATE) }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service créé")

        // Charger les applications
        loadSelectedApps()

        // Créer les canaux de notification
        createNotificationChannels()

        // Acquérir un wake lock partiel pour maintenir le service actif
        acquireWakeLock()

        // Démarrer en avant-plan immédiatement
        startForegroundWithNotification()

        // Vérifier les permissions
        if (!hasUsageStatsPermission()) {
            Log.w(TAG, "Permission d'accès aux statistiques manquante")
            showPermissionNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service démarré avec commande: ${intent?.action}")

        // Recharger les apps sélectionnées
        loadSelectedApps()

        if (intent?.action == ACTION_REFRESH_BLOCKED_APPS) {
            // Rafraîchissement explicite : s'assurer que le monitoring tourne
            Log.d(TAG, "🔄 Rafraîchissement de la liste d'apps: ${blockedApps.size} apps")
            // Le polling loop dans monitorApps() détectera automatiquement le changement
            // Si monitoring n'est pas actif (stopMonitoring() appelé), on le redémarre
            if (!isMonitoring) {
                startMonitoring()
            }
        } else {
            // Démarrer la surveillance si pas déjà active
            if (!isMonitoring) {
                startMonitoring()
            }
        }

        // START_STICKY garantit que le service sera redémarré si Android le tue
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "Service en cours de destruction")

        stopMonitoring()
        releaseWakeLock()

        // Cancel all coroutines and wait for them to finish
        scope.cancel()
        job.cancel()

        // Redémarrer le service automatiquement (fallback)
        val shouldRestart = shouldRestartService()

        super.onDestroy()

        if (shouldRestart) {
            try {
                val restartIntent = Intent(applicationContext, MonitorService::class.java)
                val pendingIntent = PendingIntent.getService(
                    applicationContext, 1, restartIntent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + SERVICE_RESTART_DELAY_MS,
                    pendingIntent
                )
            } catch (e: Exception) {
                Log.e(TAG, "Erreur lors de la planification du redémarrage", e)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startMonitoring() {
        if (isMonitoring) return

        isMonitoring = true
        Log.d(TAG, "Démarrage de la surveillance")

        scope.launch {
            monitorApps()
        }
    }

    private fun stopMonitoring() {
        isMonitoring = false
        Log.d(TAG, "Arrêt de la surveillance")
    }

    private fun shouldRestartService(): Boolean {
        // Vérifier si le service doit être redémarré
        return settingsPrefs.getBoolean("service_enabled", true) && blockedApps.isNotEmpty()
    }

    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "AppBlocker:MonitoringWakeLock"
            ).apply {
                acquire(WAKE_LOCK_TIMEOUT_MS)
            }
            Log.d(TAG, "Wake lock acquis")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de l'acquisition du wake lock", e)
        }
    }

    private fun releaseWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                    Log.d(TAG, "Wake lock libéré")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la libération du wake lock", e)
        }
    }

    private fun loadSelectedApps() {
        // Charger la liste globale par défaut
        blockedApps = settingsPrefs.getStringSet("selected_apps", emptySet()) ?: emptySet()

        Log.d(TAG, "Applications chargées pour surveillance: ${blockedApps.size} apps")

        if (blockedApps.isNotEmpty()) {
            Log.d(TAG, "Apps bloquées (global): ${blockedApps.joinToString()}")
        } else {
            Log.d(TAG, "⚠️ Aucune application sélectionnée")
        }

        // Mettre à jour la notification
        updateForegroundNotification()
    }
    
    /**
     * Retourne la liste d'apps à bloquer selon le contexte actif
     * - Mode Focus actif → selected_apps_focus
     * - Zone géographique active → selected_apps_location
     * - Sinon → selected_apps (liste globale)
     *
     * Respecte les accès temporaires accordés (allow_until_$packageName)
     */
    private fun getActiveBlockedApps(): Set<String> {
        val planningPrefs = getSharedPreferences("smart_planning", Context.MODE_PRIVATE)
        val currentTime = System.currentTimeMillis()

        val baseApps: Set<String>

        // 1. Vérifier si mode focus est actif
        val focusActive = planningPrefs.getBoolean("focus_active", false)
        if (focusActive) {
            val focusStartTime = planningPrefs.getLong("focus_start_time", 0L)
            val focusDuration = planningPrefs.getInt("focus_duration", 0)
            val focusEndTime = focusStartTime + (focusDuration * 60_000L)

            if (currentTime < focusEndTime) {
                val focusApps = settingsPrefs.getStringSet("selected_apps_focus", null)
                baseApps = if (focusApps != null && focusApps.isNotEmpty()) {
                    Log.d(TAG, "Mode Focus actif - selected_apps_focus (${focusApps.size} apps)")
                    focusApps
                } else {
                    Log.d(TAG, "Mode Focus actif - liste globale")
                    blockedApps
                }
                return filterTemporaryAccess(baseApps, currentTime)
            }
        }

        // 2. Vérifier si on est dans une zone géographique active
        if (isInBlockingZoneCheck()) {
            val locationApps = settingsPrefs.getStringSet("selected_apps_location", null)
            baseApps = if (locationApps != null && locationApps.isNotEmpty()) {
                Log.d(TAG, "Dans zone de blocage - selected_apps_location (${locationApps.size} apps)")
                locationApps
            } else {
                Log.d(TAG, "Dans zone de blocage - liste globale")
                blockedApps
            }
            return filterTemporaryAccess(baseApps, currentTime)
        }

        // 3. Par défaut, utiliser la liste globale
        return filterTemporaryAccess(blockedApps, currentTime)
    }

    /**
     * Filtre les apps qui ont un accès temporaire en cours
     */
    private fun filterTemporaryAccess(apps: Set<String>, currentTime: Long): Set<String> {
        return apps.filter { packageName ->
            val allowedUntil = prefs.getLong("allow_until_$packageName", 0L)
            currentTime >= allowedUntil
        }.toSet()
    }
    
    /**
     * Version simplifiée de isInBlockingZone pour getActiveBlockedApps
     * (sans logs détaillés pour éviter le spam)
     */
    private fun isInBlockingZoneCheck(): Boolean {
        val planningPrefs = getSharedPreferences("smart_planning", Context.MODE_PRIVATE)
        val json = planningPrefs.getString("location_zones", "[]")
        
        return try {
            val jsonArray = org.json.JSONArray(json)
            if (jsonArray.length() == 0) return false
            
            if (android.content.pm.PackageManager.PERMISSION_GRANTED != 
                androidx.core.app.ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )) {
                return false
            }
            
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
            val lastLocation = try {
                locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
            } catch (e: SecurityException) {
                null
            }
            
            if (lastLocation == null) return false
            
            val currentLat = lastLocation.latitude
            val currentLon = lastLocation.longitude
            
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val blockingEnabled = obj.getBoolean("blockingEnabled")
                
                if (blockingEnabled) {
                    val zoneLat = obj.getDouble("latitude")
                    val zoneLon = obj.getDouble("longitude")
                    val radius = obj.getInt("radius")
                    val distance = calculateDistance(currentLat, currentLon, zoneLat, zoneLon)
                    
                    if (distance <= radius) {
                        return true
                    }
                }
            }
            
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Canal pour le service en avant-plan
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "App Blocker Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Service de surveillance des applications"
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
            }

            // Canal pour les alertes importantes
            val alertChannel = NotificationChannel(
                ALERT_CHANNEL_ID,
                "App Blocker Alertes",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications importantes de l'app blocker"
            }

            notificationManager.createNotificationChannel(serviceChannel)
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    private fun startForegroundWithNotification() {
        val notification = createServiceNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateForegroundNotification() {
        val notification = createServiceNotification()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createServiceNotification(): Notification {
        val appCount = blockedApps.size
        val statusText = if (isMonitoring) "Actif" else "En attente"

        // Intent pour ouvrir l'application
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("FocusGuard - $statusText")
                .setContentText(if (appCount > 0) "$appCount application(s) surveillée(s)" else "Aucune app sélectionnée")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setAutoCancel(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("FocusGuard - $statusText")
                .setContentText(if (appCount > 0) "$appCount application(s) surveillée(s)" else "Aucune app sélectionnée")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_LOW)
                .build()
        }
    }

    private fun showPermissionNotification() {
        val intent = Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, ALERT_CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }
            .setContentTitle("Permission requise")
            .setContentText("Activez l'accès aux statistiques d'utilisation")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private suspend fun monitorApps() {
        if (!hasUsageStatsPermission()) {
            Log.e(TAG, "Impossible de surveiller sans permission")
            return
        }

        // Attendre si aucune app n'est sélectionnée (polling sans sortir)
        while (isMonitoring && scope.isActive && blockedApps.isEmpty()) {
            Log.d(TAG, "⏳ En attente d'apps à surveiller...")
            delay(MONITOR_WAIT_FOR_APPS_MS)
        }

        if (!isMonitoring || !scope.isActive) return

        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        var lastForegroundApp: String? = null
        var consecutiveErrors = 0

        Log.d(TAG, "✅ Surveillance démarrée pour ${blockedApps.size} applications")

        while (isMonitoring && scope.isActive) {
            try {
                // Vérifier périodiquement les permissions
                if (!hasUsageStatsPermission()) {
                    Log.w(TAG, "Permission perdue, arrêt de la surveillance")
                    showPermissionNotification()
                    break
                }

                val currentForegroundApp = getCurrentForegroundApp(usageStatsManager)

                if (currentForegroundApp != null &&
                    currentForegroundApp != lastForegroundApp &&
                    isAppBlocked(currentForegroundApp) &&
                    shouldShowBlockScreen(currentForegroundApp)) {

                    Log.d(TAG, "🚫 Application bloquée détectée: ${getAppDisplayName(currentForegroundApp)}")
                    showBlockingScreen(currentForegroundApp)
                }

                lastForegroundApp = currentForegroundApp
                consecutiveErrors = 0 // Reset compteur d'erreurs

                // Renouveler le wake lock périodiquement
                if (wakeLock?.isHeld != true) {
                    acquireWakeLock()
                }

            } catch (e: Exception) {
                consecutiveErrors++
                Log.e(TAG, "Erreur lors de la surveillance (tentative $consecutiveErrors)", e)

                // Si trop d'erreurs consécutives, arrêter temporairement
                if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
                    Log.e(TAG, "Trop d'erreurs consécutives, pause temporaire")
                    delay(MONITOR_PAUSE_ON_ERRORS_MS)
                    consecutiveErrors = 0
                }
            }

            // Intervalle de vérification adaptatif
            delay(if (consecutiveErrors > 0) MONITOR_ERROR_INTERVAL_MS else MONITOR_INTERVAL_MS)
        }

        Log.d(TAG, "Surveillance arrêtée")
    }

    private fun getCurrentForegroundApp(usageStatsManager: UsageStatsManager): String? {
        return try {
            val currentTime = System.currentTimeMillis()
            val events = usageStatsManager.queryEvents(currentTime - USAGE_EVENTS_WINDOW_MS, currentTime)

            var lastResumedApp: String? = null
            val event = UsageEvents.Event()

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    lastResumedApp = event.packageName
                }
            }

            lastResumedApp
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la détection de l'app au premier plan", e)
            null
        }
    }

    private fun isAppBlocked(packageName: String): Boolean {
        // Utiliser la liste active selon le contexte
        val activeApps = getActiveBlockedApps()
        return activeApps.contains(packageName)
    }

    private fun shouldShowBlockScreen(packageName: String): Boolean {
        // 1. Vérifier si l'app a un accès temporaire accordé
        val allowedUntil = prefs.getLong("allow_until_$packageName", 0L)
        val currentTime = System.currentTimeMillis()

        val isCurrentlyAllowed = currentTime < allowedUntil

        if (isCurrentlyAllowed) {
            val remainingMinutes = (allowedUntil - currentTime) / (60 * 1000)
            Log.d(TAG, "✅ Application ${getAppDisplayName(packageName)} autorisée pour encore ${remainingMinutes}min")
            return false
        }

        // 2. Vérifier la pause temporaire globale
        if (AppPreferences.isPaused(this)) {
            Log.d(TAG, "⏸️ Système en pause - ${getAppDisplayName(packageName)} autorisée")
            return false
        }

        // 3. Vérifier le mode focus actif
        val planningPrefs = getSharedPreferences("smart_planning", Context.MODE_PRIVATE)
        val focusActive = planningPrefs.getBoolean("focus_active", false)
        if (focusActive) {
            val focusStartTime = planningPrefs.getLong("focus_start_time", 0L)
            val focusDuration = planningPrefs.getInt("focus_duration", 0)
            val focusEndTime = focusStartTime + (focusDuration * 60_000L)
            
            if (currentTime < focusEndTime) {
                Log.d(TAG, "🎯 Mode Focus actif - ${getAppDisplayName(packageName)} bloquée")
                return true // En mode focus, on bloque TOUTES les apps sélectionnées
            } else {
                // Le mode focus est terminé, le désactiver
                planningPrefs.edit()
                    .putBoolean("focus_active", false)
                    .remove("focus_name")
                    .remove("focus_duration")
                    .remove("focus_start_time")
                    .apply()
            }
        }

        // 4. Vérifier les horaires personnalisés
        if (isOutsideScheduledHours()) {
            Log.d(TAG, "⌛ En dehors des horaires - ${getAppDisplayName(packageName)} autorisée")
            return false
        }

        // 5. Vérifier la géolocalisation
        if (!isInBlockingZone()) {
            Log.d(TAG, "📍 Hors zone de blocage - ${getAppDisplayName(packageName)} autorisée")
            return false
        }

        // Si aucune condition d'autorisation n'est remplie, bloquer l'app
        return true
    }

    private fun isOutsideScheduledHours(): Boolean {
        // Charger les horaires depuis JSON
        val planningPrefs = getSharedPreferences("smart_planning", Context.MODE_PRIVATE)
        val json = planningPrefs.getString("schedules", "[]")
        
        return try {
            val jsonArray = org.json.JSONArray(json)
            if (jsonArray.length() == 0) {
                // Pas d'horaires définis = toujours bloquer
                return false
            }
            
            val calendar = java.util.Calendar.getInstance()
            val currentDay = (calendar.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7 // Lun=0, Dim=6
            val currentTime = calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60 + calendar.get(java.util.Calendar.MINUTE)
            
            // Vérifier si au moins un horaire actif correspond
            var hasActiveSchedule = false
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val enabled = obj.getBoolean("enabled")
                
                if (enabled) {
                    val days = obj.getInt("days")
                    val startHour = obj.getInt("startHour")
                    val startMinute = obj.getInt("startMinute")
                    val endHour = obj.getInt("endHour")
                    val endMinute = obj.getInt("endMinute")
                    
                    val isDayActive = (days and (1 shl currentDay)) != 0
                    val startTime = startHour * 60 + startMinute
                    val endTime = endHour * 60 + endMinute
                    val isTimeActive = currentTime in startTime..endTime
                    
                    if (isDayActive && isTimeActive) {
                        hasActiveSchedule = true
                        break
                    }
                }
            }
            
            // Si on est DANS un horaire planifié, retourner false (= on bloque)
            // Si on est HORS d'un horaire planifié, retourner true (= on ne bloque pas)
            !hasActiveSchedule
            
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la vérification des horaires", e)
            false // En cas d'erreur, bloquer par défaut
        }
    }

    private fun isInBlockingZone(): Boolean {
        // Charger les zones depuis JSON
        val planningPrefs = getSharedPreferences("smart_planning", Context.MODE_PRIVATE)
        val json = planningPrefs.getString("location_zones", "[]")
        
        return try {
            val jsonArray = org.json.JSONArray(json)
            if (jsonArray.length() == 0) {
                // Pas de zones définies = toujours bloquer
                return true
            }
            
            // Vérifier la permission de localisation
            if (android.content.pm.PackageManager.PERMISSION_GRANTED != 
                androidx.core.app.ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )) {
                Log.d(TAG, "📍 Permission localisation manquante - bloquer par défaut")
                return true
            }
            
            // Récupérer la position actuelle
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
            val lastLocation = try {
                locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
            } catch (e: SecurityException) {
                Log.e(TAG, "Erreur de sécurité lors de l'accès à la localisation", e)
                null
            }
            
            if (lastLocation == null) {
                Log.d(TAG, "📍 Position inconnue - bloquer par défaut")
                return true
            }
            
            val currentLat = lastLocation.latitude
            val currentLon = lastLocation.longitude
            
            // Vérifier si on est dans au moins une zone de blocage active
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val blockingEnabled = obj.getBoolean("blockingEnabled")
                
                if (blockingEnabled) {
                    val zoneLat = obj.getDouble("latitude")
                    val zoneLon = obj.getDouble("longitude")
                    val radius = obj.getInt("radius")
                    
                    // Calculer la distance entre position actuelle et zone
                    val distance = calculateDistance(currentLat, currentLon, zoneLat, zoneLon)
                    
                    if (distance <= radius) {
                        Log.d(TAG, "📍 Dans zone de blocage '${obj.getString("name")}' (${distance.toInt()}m)")
                        return true // On est dans une zone de blocage
                    }
                }
            }
            
            Log.d(TAG, "📍 Hors de toutes les zones de blocage")
            false // On n'est dans aucune zone de blocage
            
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la vérification des zones", e)
            true // En cas d'erreur, bloquer par défaut
        }
    }
    
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        // Formule de Haversine pour calculer la distance entre deux points GPS
        val earthRadius = 6371000.0 // Rayon de la Terre en mètres
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return earthRadius * c
    }

    private fun showBlockingScreen(packageName: String) {
        try {
            val intent = Intent(this, LockActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("pkg", packageName)
                putExtra("delaySeconds", AppPreferences.getWaitingDuration(this@MonitorService))
            }

            startActivity(intent)
            Log.d(TAG, "🔒 Écran de blocage affiché pour ${getAppDisplayName(packageName)}")

        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de l'affichage de l'écran de blocage", e)
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        return try {
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager.checkOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(),
                packageName
            )

            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la vérification des permissions", e)
            false
        }
    }

    private fun getAppDisplayName(packageName: String): String {
        return AppDisplayNames.getDisplayName(this, packageName)
    }
}