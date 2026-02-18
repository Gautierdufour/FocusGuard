package com.focusguard.app

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val isSystemApp: Boolean
)

object AppScanner {
    private const val TAG = "AppScanner"

    // Cache to avoid rescanning all apps on every screen load
    private var cachedApps: List<InstalledApp>? = null
    private var cachedIncludeSystemApps: Boolean? = null
    private var cacheTimestamp: Long = 0L
    private const val CACHE_DURATION_MS = 60_000L // 1 minute

    fun invalidateCache() {
        cachedApps = null
        cachedIncludeSystemApps = null
        cacheTimestamp = 0L
    }

    suspend fun getAllInstalledApps(
        context: Context,
        includeSystemApps: Boolean = false
    ): List<InstalledApp> = withContext(Dispatchers.IO) {
        // Return cached results if still valid
        val now = System.currentTimeMillis()
        if (cachedApps != null &&
            cachedIncludeSystemApps == includeSystemApps &&
            (now - cacheTimestamp) < CACHE_DURATION_MS
        ) {
            Log.d(TAG, "Utilisation du cache (${cachedApps!!.size} apps)")
            return@withContext cachedApps!!
        }
        val packageManager = context.packageManager
        val apps = mutableListOf<InstalledApp>()

        var totalScanned = 0
        var totalSkipped = 0
        var totalIncluded = 0
        var systemAppsFound = 0
        var userAppsFound = 0

        try {
            val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            totalScanned = packages.size
            Log.d(TAG, "========================================")
            Log.d(TAG, "🔍 Début du scan - Total packages: $totalScanned")
            Log.d(TAG, "📱 includeSystemApps: $includeSystemApps")
            Log.d(TAG, "========================================")

            packages.forEach { appInfo ->
                try {
                    // Exclure l'app elle-même
                    if (appInfo.packageName == context.packageName) {
                        Log.d(TAG, "⏭️  Skip: ${appInfo.packageName} (c'est nous)")
                        totalSkipped++
                        return@forEach
                    }

                    // Vérifier que l'app peut être lancée
                    val launchIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName)

                    // Obtenir le nom de l'app pour les logs
                    val appName = try {
                        packageManager.getApplicationLabel(appInfo).toString()
                    } catch (e: Exception) {
                        appInfo.packageName
                    }

                    if (launchIntent == null) {
                        Log.d(TAG, "⏭️  Skip (pas de launch intent): $appName (${appInfo.packageName})")
                        totalSkipped++
                        return@forEach
                    }

                    // Déterminer si c'est une app système
                    val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    val isUpdatedSystemApp = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

                    if (isSystemApp) {
                        systemAppsFound++
                    } else {
                        userAppsFound++
                    }

                    // LOGIQUE DE FILTRAGE
                    if (isSystemApp && !isUpdatedSystemApp) {
                        // App système pure (pas mise à jour par utilisateur)
                        if (!includeSystemApps) {
                            // L'utilisateur ne veut pas les apps système
                            Log.d(TAG, "❌ Skip système: $appName (${appInfo.packageName})")
                            totalSkipped++
                            return@forEach
                        }

                        // L'utilisateur veut les apps système, garder seulement les utiles
                        val popularSystemApps = listOf(
                            "com.android.chrome",
                            "com.google.android.youtube",
                            "com.google.android.gm",
                            "com.android.vending",
                            "com.google.android.apps.photos",
                            "com.google.android.apps.maps",
                            "com.android.settings",
                            "com.google.android.apps.messaging",
                            "com.google.android.contacts",
                            "com.android.camera2"
                        )

                        if (!popularSystemApps.contains(appInfo.packageName)) {
                            Log.d(TAG, "❌ Skip système non-populaire: $appName")
                            totalSkipped++
                            return@forEach
                        }

                        Log.d(TAG, "✅ Inclus système populaire: $appName")
                    } else {
                        // App utilisateur normale OU app système mise à jour
                        Log.d(TAG, "✅ Inclus app utilisateur: $appName (système=$isSystemApp, updated=$isUpdatedSystemApp)")
                    }

                    val icon = try {
                        packageManager.getApplicationIcon(appInfo)
                    } catch (e: Exception) {
                        null
                    }

                    apps.add(
                        InstalledApp(
                            packageName = appInfo.packageName,
                            appName = appName,
                            icon = icon,
                            isSystemApp = isSystemApp && !isUpdatedSystemApp
                        )
                    )
                    totalIncluded++

                } catch (e: Exception) {
                    Log.e(TAG, "Erreur lecture app: ${appInfo.packageName}", e)
                    totalSkipped++
                }
            }

            apps.sortBy { it.appName.lowercase() }

        } catch (e: Exception) {
            Log.e(TAG, "Erreur scan applications", e)
        }

        Log.d(TAG, "========================================")
        Log.d(TAG, "RESUME DU SCAN:")
        Log.d(TAG, "   Total scanne: $totalScanned")
        Log.d(TAG, "   Apps systeme trouvees: $systemAppsFound")
        Log.d(TAG, "   Apps utilisateur trouvees: $userAppsFound")
        Log.d(TAG, "   Total ignore: $totalSkipped")
        Log.d(TAG, "   Total INCLUS: $totalIncluded")
        Log.d(TAG, "========================================")

        // Store in cache
        cachedApps = apps.toList()
        cachedIncludeSystemApps = includeSystemApps
        cacheTimestamp = System.currentTimeMillis()

        apps
    }

    fun filterByCategory(apps: List<InstalledApp>, category: AppCategory): List<InstalledApp> {
        if (category == AppCategory.ALL) return apps

        return apps.filter { app ->
            val packageLower = app.packageName.lowercase()
            val nameLower = app.appName.lowercase()

            when (category) {
                AppCategory.SOCIAL -> {
                    // Packages exacts connus
                    packageLower in listOf(
                        "com.instagram.android",
                        "com.facebook.katana",
                        "com.facebook.lite",
                        "com.twitter.android",
                        "com.snapchat.android",
                        "com.zhiliaoapp.musically", // TikTok
                        "com.linkedin.android",
                        "com.reddit.frontpage",
                        "com.pinterest",
                        "com.tumblr",
                        "com.twitter.android"
                    ) ||
                            // Patterns dans le package
                            packageLower.contains("instagram") ||
                            packageLower.contains("facebook") ||
                            packageLower.contains("twitter") ||
                            packageLower.contains("snapchat") ||
                            packageLower.contains("tiktok") ||
                            packageLower.contains("reddit") ||
                            // Patterns dans le nom
                            nameLower.contains("instagram") ||
                            nameLower.contains("facebook") ||
                            nameLower.contains("twitter") ||
                            nameLower.contains("snapchat") ||
                            nameLower.contains("tiktok")
                }

                AppCategory.VIDEO -> {
                    packageLower in listOf(
                        "com.google.android.youtube",
                        "com.netflix.mediaclient",
                        "com.amazon.avod.thirdpartyclient", // Prime Video
                        "tv.twitch.android.app",
                        "com.hulu.plus",
                        "com.disney.disneyplus"
                    ) ||
                            packageLower.contains("youtube") ||
                            packageLower.contains("netflix") ||
                            packageLower.contains("twitch") ||
                            packageLower.contains("prime") ||
                            nameLower.contains("youtube") ||
                            nameLower.contains("netflix") ||
                            nameLower.contains("video")
                }

                AppCategory.GAMING -> {
                    // Packages de jeux
                    packageLower.contains(".game.") ||
                            packageLower.contains("game") ||
                            packageLower.contains("casino") ||
                            packageLower.contains("puzzle") ||
                            packageLower.startsWith("com.king.") || // Candy Crush etc
                            packageLower.contains("supercell") || // Clash of Clans etc
                            nameLower.contains("game") ||
                            nameLower.contains("jeu") ||
                            nameLower.contains("play")
                }

                AppCategory.MESSAGING -> {
                    packageLower in listOf(
                        "com.whatsapp",
                        "com.facebook.orca", // Messenger
                        "org.telegram.messenger",
                        "com.discord",
                        "org.thoughtcrime.securesms", // Signal
                        "com.viber.voip",
                        "jp.naver.line.android"
                    ) ||
                            packageLower.contains("whatsapp") ||
                            packageLower.contains("messenger") ||
                            packageLower.contains("telegram") ||
                            packageLower.contains("discord") ||
                            packageLower.contains("signal") ||
                            nameLower.contains("whatsapp") ||
                            nameLower.contains("messenger") ||
                            nameLower.contains("telegram")
                }

                AppCategory.ALL -> true
            } 
        }
    }
}

enum class AppCategory(val displayName: String) {
    ALL("Toutes les apps"),
    SOCIAL("Réseaux sociaux"),
    VIDEO("Vidéos"),
    GAMING("Jeux"),
    MESSAGING("Messagerie")
}