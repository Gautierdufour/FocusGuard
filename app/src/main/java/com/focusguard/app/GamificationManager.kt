package com.focusguard.app

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import com.focusguard.app.data.StatsRepository
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

/**
 * Gestionnaire de gamification : Niveaux, XP, Badges, Streaks
 */
object GamificationManager {
    private const val PREF_NAME = "gamification_data"
    
    // Clés
    private const val KEY_XP = "total_xp"
    private const val KEY_LEVEL = "current_level"
    private const val KEY_CURRENT_STREAK = "current_streak"
    private const val KEY_BEST_STREAK = "best_streak"
    private const val KEY_LAST_ACTIVITY_DATE = "last_activity_date"
    private const val KEY_UNLOCKED_BADGES = "unlocked_badges"
    
    // XP par action
    const val XP_BREATHING = 30
    const val XP_PUSHUPS = 50
    const val XP_WAITING = 20
    const val XP_STREAK_BONUS = 10 // Par jour de streak
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    // ==================== XP & NIVEAU ====================
    
    fun getTotalXP(context: Context): Int {
        return getPrefs(context).getInt(KEY_XP, 0)
    }
    
    fun addXP(context: Context, amount: Int) {
        val prefs = getPrefs(context)
        val currentXP = getTotalXP(context)
        val newXP = currentXP + amount
        
        prefs.edit().putInt(KEY_XP, newXP).apply()
        
        // Vérifier si level up
        checkLevelUp(context)
        
        // Vérifier nouveaux badges
        checkNewBadges(context)
    }
    
    fun getCurrentLevel(context: Context): Int {
        return getPrefs(context).getInt(KEY_LEVEL, 1)
    }
    
    private fun checkLevelUp(context: Context) {
        val xp = getTotalXP(context)
        val currentLevel = getCurrentLevel(context)
        val newLevel = calculateLevel(xp)
        
        if (newLevel > currentLevel) {
            getPrefs(context).edit().putInt(KEY_LEVEL, newLevel).apply()
        }
    }
    
    fun calculateLevel(xp: Int): Int {
        // Formule : Level = √(XP / 100) + 1
        return (kotlin.math.sqrt(xp / 100.0) + 1).toInt()
    }
    
    fun getXPForLevel(level: Int): Int {
        // XP requis pour atteindre ce level
        return ((level - 1) * (level - 1) * 100)
    }
    
    fun getXPProgress(context: Context): Float {
        val xp = getTotalXP(context)
        val level = getCurrentLevel(context)
        val currentLevelXP = getXPForLevel(level)
        val nextLevelXP = getXPForLevel(level + 1)
        
        val progress = (xp - currentLevelXP).toFloat() / (nextLevelXP - currentLevelXP).toFloat()
        return progress.coerceIn(0f, 1f)
    }
    
    fun getXPToNextLevel(context: Context): Int {
        val xp = getTotalXP(context)
        val level = getCurrentLevel(context)
        val nextLevelXP = getXPForLevel(level + 1)
        return (nextLevelXP - xp).coerceAtLeast(0)
    }
    
    // ==================== STREAKS ====================
    
    fun getCurrentStreak(context: Context): Int {
        return getPrefs(context).getInt(KEY_CURRENT_STREAK, 0)
    }
    
    fun getBestStreak(context: Context): Int {
        return getPrefs(context).getInt(KEY_BEST_STREAK, 0)
    }
    
    fun updateStreak(context: Context) {
        val prefs = getPrefs(context)
        val today = getDateString()
        val lastActivity = prefs.getString(KEY_LAST_ACTIVITY_DATE, "")
        
        if (lastActivity == today) {
            // Déjà actif aujourd'hui
            return
        }
        
        val yesterday = getYesterdayString()
        val currentStreak = getCurrentStreak(context)
        
        val newStreak = if (lastActivity == yesterday) {
            // Continue la série
            currentStreak + 1
        } else {
            // Série brisée, recommence
            1
        }
        
        prefs.edit()
            .putInt(KEY_CURRENT_STREAK, newStreak)
            .putString(KEY_LAST_ACTIVITY_DATE, today)
            .apply()
        
        // Mettre à jour le meilleur streak
        val bestStreak = getBestStreak(context)
        if (newStreak > bestStreak) {
            prefs.edit().putInt(KEY_BEST_STREAK, newStreak).apply()
        }
        
        // Bonus XP pour streak
        if (newStreak > 1) {
            addXP(context, XP_STREAK_BONUS * newStreak)
        }
    }
    
    private fun getDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    
    private fun getYesterdayString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }
    
    // ==================== BADGES ====================
    
    data class Badge(
        val id: String,
        val name: String,
        val description: String,
        val icon: String, // Emoji
        val requirement: (Context) -> Boolean,
        val color: Color
    )
    
    private fun getBlockCount(context: Context): Int {
        return try {
            runBlocking { StatsRepository.getInstance(context).getTotalBlocks() }
        } catch (e: Exception) { 0 }
    }

    private fun getChallengeCount(context: Context, type: String): Int {
        return try {
            runBlocking { StatsRepository.getInstance(context).getChallengeCountByType(type) }
        } catch (e: Exception) { 0 }
    }

    private fun getTotalTimeSavedFromRoom(context: Context): Int {
        return try {
            runBlocking { StatsRepository.getInstance(context).getTotalTimeSaved() }
        } catch (e: Exception) { 0 }
    }

    val ALL_BADGES = listOf(
        Badge(
            "first_block", "Premier pas",
            "Bloquer une app pour la première fois", "🎯",
            { context -> getBlockCount(context) >= 1 },
            AppColors.Info
        ),
        Badge(
            "blocks_10", "Débutant",
            "Bloquer 10 apps", "🌟",
            { context -> getBlockCount(context) >= 10 },
            AppColors.Primary
        ),
        Badge(
            "blocks_50", "Confirmé",
            "Bloquer 50 apps", "⭐",
            { context -> getBlockCount(context) >= 50 },
            AppColors.Success
        ),
        Badge(
            "blocks_100", "Expert",
            "Bloquer 100 apps", "💫",
            { context -> getBlockCount(context) >= 100 },
            AppColors.Warning
        ),
        Badge(
            "blocks_500", "Maître",
            "Bloquer 500 apps", "🏆",
            { context -> getBlockCount(context) >= 500 },
            AppColors.Accent
        ),
        Badge(
            "streak_3", "Régulier",
            "3 jours de suite", "🔥",
            { context -> getCurrentStreak(context) >= 3 },
            AppColors.Error
        ),
        Badge(
            "streak_7", "Persévérant",
            "7 jours de suite", "🔥🔥",
            { context -> getCurrentStreak(context) >= 7 },
            AppColors.Error
        ),
        Badge(
            "streak_30", "Inébranlable",
            "30 jours de suite", "🔥🔥🔥",
            { context -> getCurrentStreak(context) >= 30 },
            AppColors.Error
        ),
        Badge(
            "breathing_master", "Zen",
            "Compléter 20 exercices de respiration", "🧘",
            { context -> getChallengeCount(context, "breathing") >= 20 },
            AppColors.Info
        ),
        Badge(
            "pushup_master", "Athlète",
            "Compléter 50 séries de pompes", "💪",
            { context -> getChallengeCount(context, "pushups") >= 50 },
            AppColors.Success
        ),
        Badge(
            "patient_master", "Patient",
            "Compléter 30 attentes", "⏳",
            { context -> getChallengeCount(context, "waiting") >= 30 },
            AppColors.Warning
        ),
        Badge(
            "level_5", "Niveau 5",
            "Atteindre le niveau 5", "🎖️",
            { context -> getCurrentLevel(context) >= 5 },
            AppColors.Primary
        ),
        Badge(
            "level_10", "Niveau 10",
            "Atteindre le niveau 10", "🥇",
            { context -> getCurrentLevel(context) >= 10 },
            AppColors.Warning
        ),
        Badge(
            "level_20", "Niveau 20",
            "Atteindre le niveau 20", "👑",
            { context -> getCurrentLevel(context) >= 20 },
            AppColors.Accent
        ),
        Badge(
            "time_saver", "Économe",
            "Économiser 5 heures", "⏰",
            { context -> getTotalTimeSavedFromRoom(context) >= 300 },
            AppColors.Success
        )
    )
    
    fun getUnlockedBadges(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_UNLOCKED_BADGES, emptySet()) ?: emptySet()
    }
    
    private fun checkNewBadges(context: Context) {
        val unlocked = getUnlockedBadges(context).toMutableSet()
        var hasNewBadge = false
        
        ALL_BADGES.forEach { badge ->
            if (!unlocked.contains(badge.id) && badge.requirement(context)) {
                unlocked.add(badge.id)
                hasNewBadge = true
            }
        }
        
        if (hasNewBadge) {
            getPrefs(context).edit()
                .putStringSet(KEY_UNLOCKED_BADGES, unlocked)
                .apply()
        }
    }
    
    fun getUnlockedBadgesList(context: Context): List<Badge> {
        val unlockedIds = getUnlockedBadges(context)
        return ALL_BADGES.filter { unlockedIds.contains(it.id) }
    }
    
    fun getLockedBadgesList(context: Context): List<Badge> {
        val unlockedIds = getUnlockedBadges(context)
        return ALL_BADGES.filter { !unlockedIds.contains(it.id) }
    }
    
    fun getBadgeProgress(context: Context): Float {
        val unlocked = getUnlockedBadges(context).size
        val total = ALL_BADGES.size
        return unlocked.toFloat() / total.toFloat()
    }
    
    // ==================== TITRES DE NIVEAU ====================
    
    fun getLevelTitle(level: Int): String {
        return when {
            level >= 30 -> "Légende"
            level >= 25 -> "Maître Suprême"
            level >= 20 -> "Maître"
            level >= 15 -> "Expert"
            level >= 10 -> "Vétéran"
            level >= 7 -> "Confirmé"
            level >= 5 -> "Intermédiaire"
            level >= 3 -> "Apprenti"
            else -> "Débutant"
        }
    }
    
    fun getLevelColor(level: Int): Color {
        return when {
            level >= 20 -> AppColors.Accent
            level >= 10 -> AppColors.Warning
            level >= 5 -> AppColors.Primary
            else -> AppColors.Info
        }
    }
}
