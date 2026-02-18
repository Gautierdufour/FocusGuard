package com.focusguard.app

import android.content.Context

/**
 * Utilitaire pour réinitialiser complètement les préférences de l'application
 * À utiliser en cas de problème ou pour recommencer à zéro
 */
object PreferencesReset {

    /**
     * Réinitialise TOUTES les préférences de l'application
     * ⚠️ Attention : supprime toutes les données sauvegardées
     */
    fun resetAll(context: Context) {
        // Réinitialiser les apps sélectionnées
        val appBlockerPrefs = context.getSharedPreferences("app_blocker_settings", Context.MODE_PRIVATE)
        appBlockerPrefs.edit().clear().apply()

        // Réinitialiser les autorisations temporaires
        val lockPrefs = context.getSharedPreferences("lock", Context.MODE_PRIVATE)
        lockPrefs.edit().clear().apply()

        // Réinitialiser les statistiques
        val statsPrefs = context.getSharedPreferences("app_blocker_stats", Context.MODE_PRIVATE)
        statsPrefs.edit().clear().apply()

        android.util.Log.d("PreferencesReset", "✅ Toutes les préférences ont été réinitialisées")
    }

    /**
     * Réinitialise uniquement la sélection des apps
     * (garde les stats et paramètres)
     */
    fun resetSelectedApps(context: Context) {
        val prefs = context.getSharedPreferences("app_blocker_settings", Context.MODE_PRIVATE)
        prefs.edit()
            .remove("selected_apps")
            .apply()

        android.util.Log.d("PreferencesReset", "✅ Sélection d'apps réinitialisée")
    }

    /**
     * Réinitialise uniquement les statistiques
     * (garde les apps sélectionnées et paramètres)
     */
    fun resetStats(context: Context) {
        val statsPrefs = context.getSharedPreferences("app_blocker_stats", Context.MODE_PRIVATE)
        statsPrefs.edit().clear().apply()

        android.util.Log.d("PreferencesReset", "✅ Statistiques réinitialisées")
    }

    /**
     * Nettoie les autorisations temporaires expirées
     */
    fun cleanExpiredPermissions(context: Context) {
        val lockPrefs = context.getSharedPreferences("lock", Context.MODE_PRIVATE)
        val editor = lockPrefs.edit()
        val currentTime = System.currentTimeMillis()
        var cleaned = 0

        lockPrefs.all.forEach { (key, value) ->
            if (key.startsWith("allow_until_") && value is Long) {
                if (value < currentTime) {
                    editor.remove(key)
                    cleaned++
                }
            }
        }

        editor.apply()
        android.util.Log.d("PreferencesReset", "✅ $cleaned autorisation(s) expirée(s) nettoyée(s)")
    }
}

// Extension pour faciliter l'utilisation dans MainActivity
fun Context.resetAppPreferences() {
    PreferencesReset.resetAll(this)
}