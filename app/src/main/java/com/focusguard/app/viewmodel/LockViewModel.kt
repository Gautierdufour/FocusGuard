package com.focusguard.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focusguard.app.AppPreferences
import com.focusguard.app.GamificationManager
import com.focusguard.app.data.StatsRepository
import com.focusguard.app.utils.AppDisplayNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LockViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication()
    private val lockPrefs by lazy {
        context.getSharedPreferences("lock", Context.MODE_PRIVATE)
    }
    private val statsRepository by lazy { StatsRepository.getInstance(context) }

    fun getAppDisplayName(packageName: String): String {
        return AppDisplayNames.getDisplayName(context, packageName)
    }

    fun recordBlock(packageName: String) {
        viewModelScope.launch {
            try {
                statsRepository.recordBlock(packageName)
            } catch (e: Exception) {
                android.util.Log.e("LockViewModel", "Erreur Room recordBlock", e)
            }
        }
    }

    /**
     * Records challenge completion, awards XP, updates streak.
     * Returns the new level (or null if no level up).
     */
    fun recordChallengeCompleted(challengeType: String, packageName: String): Int? {
        val levelBefore = GamificationManager.getCurrentLevel(context)

        val timeSavedMinutes = when (challengeType) {
            "breathing" -> AppPreferences.getBreathingDuration(context) / 60
            "pushups" -> 3
            "waiting" -> AppPreferences.getWaitingDuration(context) / 60
            else -> 1
        }

        val xpAmount = when (challengeType) {
            "breathing" -> GamificationManager.XP_BREATHING
            "pushups" -> GamificationManager.XP_PUSHUPS
            "waiting" -> GamificationManager.XP_WAITING
            "quiz" -> 15
            "math" -> 15
            "puzzle" -> 20
            "meditation" -> 15
            else -> 10
        }
        GamificationManager.addXP(context, xpAmount)
        GamificationManager.updateStreak(context)

        viewModelScope.launch {
            try {
                statsRepository.recordChallenge(challengeType, packageName, xpAmount, timeSavedMinutes)
            } catch (e: Exception) {
                android.util.Log.e("LockViewModel", "Erreur Room recordChallenge", e)
            }
            // Vérification des badges en background (évite runBlocking sur UI)
            withContext(Dispatchers.IO) {
                try {
                    GamificationManager.checkNewBadges(context)
                } catch (e: Exception) {
                    android.util.Log.e("LockViewModel", "Erreur checkNewBadges", e)
                }
            }
        }

        val levelAfter = GamificationManager.getCurrentLevel(context)
        return if (levelAfter > levelBefore) levelAfter else null
    }

    fun grantTemporaryAccess(packageName: String) {
        val accessMinutes = AppPreferences.getAccessDuration(context)
        val until = System.currentTimeMillis() + accessMinutes * 60_000L
        lockPrefs.edit().putLong("allow_until_$packageName", until).apply()
    }

    fun getLevelTitle(level: Int): String = GamificationManager.getLevelTitle(level)
}
