package com.focusguard.app

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestion centralisée de tous les paramètres de l'application
 * Toutes les valeurs par défaut sont définies ici
 */
object AppPreferences {
    private const val PREF_NAME = "app_blocker_settings"

    // Clés des préférences
    private const val KEY_ACCESS_DURATION = "access_duration_minutes"
    private const val KEY_PUSHUP_COUNT = "pushup_count"
    private const val KEY_WAITING_DURATION = "waiting_duration_seconds"
    private const val KEY_BREATHING_DURATION = "breathing_duration_seconds"
    private const val KEY_QUIZ_COUNT = "quiz_question_count"
    private const val KEY_MATH_COUNT = "math_problem_count"
    private const val KEY_PUZZLE_COUNT = "puzzle_count"
    private const val KEY_MEDITATION_DURATION = "meditation_duration_seconds"
    private const val KEY_SELECTED_APPS = "selected_apps"
    private const val KEY_SCHEDULE_ENABLED = "schedule_enabled"
    private const val KEY_SCHEDULE_START_HOUR = "schedule_start_hour"
    private const val KEY_SCHEDULE_START_MINUTE = "schedule_start_minute"
    private const val KEY_SCHEDULE_END_HOUR = "schedule_end_hour"
    private const val KEY_SCHEDULE_END_MINUTE = "schedule_end_minute"
    private const val KEY_SCHEDULE_DAYS = "schedule_days" // Bitmap: 1111111 = tous les jours
    private const val KEY_PAUSE_UNTIL = "pause_until_timestamp"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_DAILY_GOAL_BLOCKS = "daily_goal_blocks"
    private const val KEY_NO_COOLDOWN = "no_cooldown_mode" // NOUVEAU

    // Valeurs par défaut
    private const val DEFAULT_ACCESS_DURATION = 30 // minutes
    private const val DEFAULT_PUSHUP_COUNT = 10
    private const val DEFAULT_WAITING_DURATION = 120 // secondes
    private const val DEFAULT_BREATHING_DURATION = 120 // secondes
    private const val DEFAULT_QUIZ_COUNT = 3
    private const val DEFAULT_MATH_COUNT = 3
    private const val DEFAULT_PUZZLE_COUNT = 2
    private const val DEFAULT_MEDITATION_DURATION = 60 // secondes
    private const val DEFAULT_SCHEDULE_START_HOUR = 9
    private const val DEFAULT_SCHEDULE_START_MINUTE = 0
    private const val DEFAULT_SCHEDULE_END_HOUR = 18
    private const val DEFAULT_SCHEDULE_END_MINUTE = 0
    private const val DEFAULT_SCHEDULE_DAYS = 0b1111100 // Lun-Ven
    private const val DEFAULT_DAILY_GOAL = 5
    private const val DEFAULT_NO_COOLDOWN = false // Par défaut, cooldown activé

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // ==================== DÉFIS ====================

    fun getAccessDuration(context: Context): Int {
        return getPrefs(context).getInt(KEY_ACCESS_DURATION, DEFAULT_ACCESS_DURATION)
    }

    fun setAccessDuration(context: Context, minutes: Int) {
        getPrefs(context).edit().putInt(KEY_ACCESS_DURATION, minutes.coerceIn(15, 120)).apply()
    }

    fun getPushupCount(context: Context): Int {
        return getPrefs(context).getInt(KEY_PUSHUP_COUNT, DEFAULT_PUSHUP_COUNT)
    }

    fun setPushupCount(context: Context, count: Int) {
        getPrefs(context).edit().putInt(KEY_PUSHUP_COUNT, count.coerceIn(5, 50)).apply()
    }

    fun getWaitingDuration(context: Context): Int {
        return getPrefs(context).getInt(KEY_WAITING_DURATION, DEFAULT_WAITING_DURATION)
    }

    fun setWaitingDuration(context: Context, seconds: Int) {
        getPrefs(context).edit().putInt(KEY_WAITING_DURATION, seconds.coerceIn(30, 300)).apply()
    }

    fun getBreathingDuration(context: Context): Int {
        return getPrefs(context).getInt(KEY_BREATHING_DURATION, DEFAULT_BREATHING_DURATION)
    }

    fun setBreathingDuration(context: Context, seconds: Int) {
        getPrefs(context).edit().putInt(KEY_BREATHING_DURATION, seconds.coerceIn(60, 300)).apply()
    }

    // Nouveaux défis cognitifs

    fun getQuizCount(context: Context): Int {
        return getPrefs(context).getInt(KEY_QUIZ_COUNT, DEFAULT_QUIZ_COUNT)
    }

    fun setQuizCount(context: Context, count: Int) {
        getPrefs(context).edit().putInt(KEY_QUIZ_COUNT, count.coerceIn(1, 10)).apply()
    }

    fun getMathCount(context: Context): Int {
        return getPrefs(context).getInt(KEY_MATH_COUNT, DEFAULT_MATH_COUNT)
    }

    fun setMathCount(context: Context, count: Int) {
        getPrefs(context).edit().putInt(KEY_MATH_COUNT, count.coerceIn(1, 10)).apply()
    }

    fun getPuzzleCount(context: Context): Int {
        return getPrefs(context).getInt(KEY_PUZZLE_COUNT, DEFAULT_PUZZLE_COUNT)
    }

    fun setPuzzleCount(context: Context, count: Int) {
        getPrefs(context).edit().putInt(KEY_PUZZLE_COUNT, count.coerceIn(1, 5)).apply()
    }

    fun getMeditationDuration(context: Context): Int {
        return getPrefs(context).getInt(KEY_MEDITATION_DURATION, DEFAULT_MEDITATION_DURATION)
    }

    fun setMeditationDuration(context: Context, seconds: Int) {
        getPrefs(context).edit().putInt(KEY_MEDITATION_DURATION, seconds.coerceIn(30, 300)).apply()
    }

    // ==================== MODE SANS COOLDOWN ====================

    /**
     * Retourne true si le mode "sans cooldown" est activé
     * Dans ce mode, l'app est bloquée à chaque lancement sans période d'accès
     */
    fun isNoCooldownMode(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_NO_COOLDOWN, DEFAULT_NO_COOLDOWN)
    }

    /**
     * Active/désactive le mode "sans cooldown"
     */
    fun setNoCooldownMode(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_NO_COOLDOWN, enabled).apply()
    }

    // ==================== APPS SÉLECTIONNÉES ====================

    fun getSelectedApps(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_SELECTED_APPS, emptySet()) ?: emptySet()
    }

    fun setSelectedApps(context: Context, apps: Set<String>) {
        getPrefs(context).edit().putStringSet(KEY_SELECTED_APPS, apps).apply()
    }

    // ==================== HORAIRES ====================

    fun isScheduleEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_SCHEDULE_ENABLED, false)
    }

    fun setScheduleEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SCHEDULE_ENABLED, enabled).apply()
    }

    data class TimeRange(val startHour: Int, val startMinute: Int, val endHour: Int, val endMinute: Int)

    fun getScheduleTime(context: Context): TimeRange {
        val prefs = getPrefs(context)
        return TimeRange(
            startHour = prefs.getInt(KEY_SCHEDULE_START_HOUR, DEFAULT_SCHEDULE_START_HOUR),
            startMinute = prefs.getInt(KEY_SCHEDULE_START_MINUTE, DEFAULT_SCHEDULE_START_MINUTE),
            endHour = prefs.getInt(KEY_SCHEDULE_END_HOUR, DEFAULT_SCHEDULE_END_HOUR),
            endMinute = prefs.getInt(KEY_SCHEDULE_END_MINUTE, DEFAULT_SCHEDULE_END_MINUTE)
        )
    }

    fun setScheduleTime(context: Context, timeRange: TimeRange) {
        getPrefs(context).edit()
            .putInt(KEY_SCHEDULE_START_HOUR, timeRange.startHour)
            .putInt(KEY_SCHEDULE_START_MINUTE, timeRange.startMinute)
            .putInt(KEY_SCHEDULE_END_HOUR, timeRange.endHour)
            .putInt(KEY_SCHEDULE_END_MINUTE, timeRange.endMinute)
            .apply()
    }

    fun getScheduleDays(context: Context): Int {
        return getPrefs(context).getInt(KEY_SCHEDULE_DAYS, DEFAULT_SCHEDULE_DAYS)
    }

    fun setScheduleDays(context: Context, daysBitmap: Int) {
        getPrefs(context).edit().putInt(KEY_SCHEDULE_DAYS, daysBitmap).apply()
    }

    fun isDayEnabled(context: Context, dayOfWeek: Int): Boolean {
        // dayOfWeek: 1=Dimanche, 2=Lundi, ..., 7=Samedi (Calendar.DAY_OF_WEEK)
        val bitmap = getScheduleDays(context)
        val bitPosition = (dayOfWeek + 5) % 7 // Conversion pour avoir Lun=0, Mar=1, etc.
        return (bitmap and (1 shl bitPosition)) != 0
    }

    fun isCurrentlyInSchedule(context: Context): Boolean {
        if (!isScheduleEnabled(context)) return true // Si désactivé, toujours actif

        val calendar = java.util.Calendar.getInstance()
        val currentDay = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(java.util.Calendar.MINUTE)

        // Vérifier le jour
        if (!isDayEnabled(context, currentDay)) return false

        // Vérifier l'heure
        val timeRange = getScheduleTime(context)
        val currentTime = currentHour * 60 + currentMinute
        val startTime = timeRange.startHour * 60 + timeRange.startMinute
        val endTime = timeRange.endHour * 60 + timeRange.endMinute

        return currentTime in startTime..endTime
    }

    // ==================== PAUSE TEMPORAIRE ====================

    fun getPauseUntil(context: Context): Long {
        return getPrefs(context).getLong(KEY_PAUSE_UNTIL, 0L)
    }

    fun setPauseUntil(context: Context, timestamp: Long) {
        getPrefs(context).edit().putLong(KEY_PAUSE_UNTIL, timestamp).apply()
    }

    fun isPaused(context: Context): Boolean {
        val pauseUntil = getPauseUntil(context)
        return System.currentTimeMillis() < pauseUntil
    }

    fun clearPause(context: Context) {
        setPauseUntil(context, 0L)
    }

    // ==================== NOTIFICATIONS ====================

    fun areNotificationsEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    // ==================== OBJECTIFS ====================

    fun getDailyGoal(context: Context): Int {
        return getPrefs(context).getInt(KEY_DAILY_GOAL_BLOCKS, DEFAULT_DAILY_GOAL)
    }

    fun setDailyGoal(context: Context, goal: Int) {
        getPrefs(context).edit().putInt(KEY_DAILY_GOAL_BLOCKS, goal.coerceIn(1, 50)).apply()
    }

    // ==================== HELPER ====================

    /**
     * Réinitialise tous les paramètres aux valeurs par défaut
     */
    fun resetToDefaults(context: Context) {
        val selectedApps = getSelectedApps(context) // Garder les apps sélectionnées
        getPrefs(context).edit().clear().apply()
        setSelectedApps(context, selectedApps)
    }

    /**
     * Récupère un résumé de la configuration actuelle
     */
    fun getConfigSummary(context: Context): String {
        return buildString {
            appendLine("Mode: ${if (isNoCooldownMode(context)) "Sans cooldown (défi à chaque lancement)" else "Avec cooldown"}")
            if (!isNoCooldownMode(context)) {
                appendLine("Accès après défi: ${getAccessDuration(context)}min")
            }
            appendLine("Pompes requises: ${getPushupCount(context)}")
            appendLine("Attente: ${getWaitingDuration(context)}s")
            appendLine("Respiration: ${getBreathingDuration(context)}s")
            appendLine("Horaires: ${if (isScheduleEnabled(context)) "Activés" else "Désactivés"}")
            appendLine("Objectif quotidien: ${getDailyGoal(context)} blocages")
        }
    }
}

/**
 * Constantes pour les durées prédéfinies
 */
object PresetDurations {
    val ACCESS_DURATIONS = listOf(15, 30, 60, 120) // minutes
    val PUSHUP_COUNTS = listOf(5, 10, 15, 20, 30)
    val WAITING_DURATIONS = listOf(30, 60, 120, 180, 300) // secondes
    val BREATHING_DURATIONS = listOf(60, 120, 180, 240) // secondes
    val QUIZ_COUNTS = listOf(1, 3, 5, 7, 10)
    val MATH_COUNTS = listOf(1, 3, 5, 7, 10)
    val PUZZLE_COUNTS = listOf(1, 2, 3, 5)
    val MEDITATION_DURATIONS = listOf(30, 60, 90, 120, 180) // secondes
    val DAILY_GOALS = listOf(3, 5, 10, 15, 20)

    fun formatDuration(seconds: Int): String {
        return when {
            seconds < 60 -> "${seconds}s"
            seconds % 60 == 0 -> "${seconds / 60}min"
            else -> "${seconds / 60}min ${seconds % 60}s"
        }
    }
}
