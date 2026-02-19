package com.focusguard.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focusguard.app.AppPreferences
import com.focusguard.app.GamificationManager
import com.focusguard.app.data.StatsRepository
import com.focusguard.app.utils.AppDisplayNames
import androidx.glance.appwidget.updateAll
import com.focusguard.app.widget.FocusGuardWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChallengeResult(
    val newLevel: Int?,
    val newBadges: List<GamificationManager.Badge>
)

class LockViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication()
    private val lockPrefs by lazy {
        context.getSharedPreferences("lock", Context.MODE_PRIVATE)
    }
    private val statsRepository by lazy { StatsRepository.getInstance(context) }

    private val _challengeResult = MutableStateFlow<ChallengeResult?>(null)
    val challengeResult: StateFlow<ChallengeResult?> = _challengeResult.asStateFlow()

    fun clearChallengeResult() {
        _challengeResult.value = null
    }

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
     * Enregistre la complétion d'un défi, attribue l'XP et met à jour le streak.
     * Le résultat (montée de niveau + badges) est exposé via challengeResult.
     */
    fun recordChallengeCompleted(challengeType: String, packageName: String) {
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

        val levelAfter = GamificationManager.getCurrentLevel(context)
        val newLevel = if (levelAfter > levelBefore) levelAfter else null

        viewModelScope.launch {
            try {
                statsRepository.recordChallenge(challengeType, packageName, xpAmount, timeSavedMinutes)
            } catch (e: Exception) {
                android.util.Log.e("LockViewModel", "Erreur Room recordChallenge", e)
            }
            val newBadges = withContext(Dispatchers.IO) {
                try {
                    GamificationManager.checkNewBadges(context)
                } catch (e: Exception) {
                    android.util.Log.e("LockViewModel", "Erreur checkNewBadges", e)
                    emptyList()
                }
            }
            _challengeResult.value = ChallengeResult(newLevel, newBadges)

            // Mettre à jour le widget après le défi
            try {
                FocusGuardWidget().updateAll(context)
            } catch (e: Exception) {
                android.util.Log.e("LockViewModel", "Erreur mise à jour widget", e)
            }
        }
    }

    fun grantTemporaryAccess(packageName: String) {
        val accessMinutes = AppPreferences.getAccessDuration(context)
        val until = System.currentTimeMillis() + accessMinutes * 60_000L
        lockPrefs.edit().putLong("allow_until_$packageName", until).apply()
    }

    fun getLevelTitle(level: Int): String = GamificationManager.getLevelTitle(level)
}
