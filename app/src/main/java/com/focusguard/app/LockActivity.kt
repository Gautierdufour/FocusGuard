package com.focusguard.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusguard.app.challenges.RedesignedLockScreen
import com.focusguard.app.components.GlassBackground
import com.focusguard.app.components.GlassButton
import com.focusguard.app.components.GlassCard
import com.focusguard.app.ui.theme.FocusGuardTheme
import com.focusguard.app.viewmodel.ChallengeResult
import com.focusguard.app.viewmodel.LockViewModel
import kotlinx.coroutines.delay

class LockActivity : ComponentActivity() {

    private val viewModel: LockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val targetPkg = intent.getStringExtra("pkg") ?: ""
        val delaySeconds = AppPreferences.getWaitingDuration(this)
        val breathingDuration = AppPreferences.getBreathingDuration(this)
        val pushupCount = AppPreferences.getPushupCount(this)

        viewModel.recordBlock(targetPkg)

        setContent {
            BackHandler(enabled = true) { /* Bloquer retour */ }

            FocusGuardTheme {
                GlassBackground(modifier = Modifier.fillMaxSize()) {
                    val challengeResult by viewModel.challengeResult.collectAsStateWithLifecycle()

                    AnimatedContent(
                        targetState = challengeResult,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.92f, animationSpec = tween(400))) togetherWith
                                    fadeOut(animationSpec = tween(200))
                        },
                        label = "lock_challenge_transition"
                    ) { result ->
                        if (result == null) {
                            RedesignedLockScreen(
                                appName = viewModel.getAppDisplayName(targetPkg),
                                delaySeconds = delaySeconds,
                                breathingDuration = breathingDuration,
                                pushupCount = pushupCount,
                                onValidate = { challengeType ->
                                    viewModel.recordChallengeCompleted(challengeType, targetPkg)
                                    // L'accès sera accordé après que PostChallengeScreen appelle onDone
                                },
                                onCancel = {
                                    goHome()
                                    finish()
                                }
                            )
                        } else {
                            PostChallengeScreen(
                                result = result,
                                getLevelTitle = { viewModel.getLevelTitle(it) },
                                onDone = {
                                    viewModel.clearChallengeResult()
                                    finishAndGrantAccess(targetPkg)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun finishAndGrantAccess(packageName: String) {
        viewModel.grantTemporaryAccess(packageName)
        finish()
    }

    private fun goHome() {
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}

// ==================== POST-CHALLENGE SCREENS ====================

private enum class PostChallengePhase { LEVEL_UP, BADGES, DONE }

@Composable
private fun PostChallengeScreen(
    result: ChallengeResult,
    getLevelTitle: (Int) -> String,
    onDone: () -> Unit
) {
    val phases = buildList {
        if (result.newLevel != null) add(PostChallengePhase.LEVEL_UP)
        if (result.newBadges.isNotEmpty()) add(PostChallengePhase.BADGES)
    }

    if (phases.isEmpty()) {
        // Aucune récompense — terminer directement
        LaunchedEffect(Unit) { onDone() }
        return
    }

    var phaseIndex by remember { mutableStateOf(0) }

    val advance: () -> Unit = {
        if (phaseIndex + 1 < phases.size) {
            phaseIndex++
        } else {
            onDone()
        }
    }

    when (phases.getOrNull(phaseIndex)) {
        PostChallengePhase.LEVEL_UP -> {
            result.newLevel?.let { level ->
                LevelUpScreen(level = level, title = getLevelTitle(level), onDismiss = advance)
            }
        }
        PostChallengePhase.BADGES -> {
            BadgeUnlockScreen(badges = result.newBadges, onContinue = advance)
        }
        else -> LaunchedEffect(Unit) { onDone() }
    }
}

@Composable
private fun LevelUpScreen(level: Int, title: String, onDismiss: () -> Unit) {
    // Auto-dismiss après 1.5s
    LaunchedEffect(Unit) {
        delay(1500)
        onDismiss()
    }

    var triggered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { triggered = true }

    val scale by animateFloatAsState(
        targetValue = if (triggered) 1f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "level_scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .padding(32.dp)
                .scale(scale),
            accentColor = AppColors.Warning
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "🏆", fontSize = 64.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Niveau $level !",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Warning,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    color = AppColors.OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BadgeUnlockScreen(
    badges: List<com.focusguard.app.GamificationManager.Badge>,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier.padding(24.dp),
            accentColor = AppColors.Success
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Badge débloqué !",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Success,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                badges.forEach { badge ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = badge.icon, fontSize = 36.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = badge.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = badge.color
                            )
                            Text(
                                text = badge.description,
                                fontSize = 13.sp,
                                color = AppColors.OnSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                GlassButton(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                    accentColor = AppColors.Success
                ) {
                    Text("Super !", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
