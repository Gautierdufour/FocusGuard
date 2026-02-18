package com.focusguard.app.challenges

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.AppColors
import com.focusguard.app.MathChallenge
import com.focusguard.app.MeditationChallenge
import com.focusguard.app.PuzzleChallenge
import com.focusguard.app.QuizChallenge
import com.focusguard.app.components.GlassCard
import com.focusguard.app.components.GlassIconBadge

@Composable
fun RedesignedLockScreen(
    appName: String,
    delaySeconds: Int,
    breathingDuration: Int,
    pushupCount: Int,
    onValidate: (String) -> Unit,
    onCancel: () -> Unit
) {
    var selectedChallenge by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (selectedChallenge) {
            "breathing" -> {
                RedesignedBreathingChallenge(
                    duration = breathingDuration,
                    onComplete = { onValidate("breathing") },
                    onBack = { selectedChallenge = null }
                )
            }
            "pushups" -> {
                RedesignedPushUpChallenge(
                    targetCount = pushupCount,
                    onComplete = { onValidate("pushups") },
                    onBack = { selectedChallenge = null }
                )
            }
            "waiting" -> {
                RedesignedWaitingChallenge(
                    delaySeconds = delaySeconds,
                    onComplete = { onValidate("waiting") },
                    onCancel = onCancel,
                    onBack = { selectedChallenge = null }
                )
            }
            "quiz" -> {
                QuizChallenge(
                    onComplete = { onValidate("quiz") },
                    onBack = { selectedChallenge = null }
                )
            }
            "math" -> {
                MathChallenge(
                    onComplete = { onValidate("math") },
                    onBack = { selectedChallenge = null }
                )
            }
            "puzzle" -> {
                PuzzleChallenge(
                    onComplete = { onValidate("puzzle") },
                    onBack = { selectedChallenge = null }
                )
            }
            "meditation" -> {
                MeditationChallenge(
                    onComplete = { onValidate("meditation") },
                    onBack = { selectedChallenge = null }
                )
            }
            else -> {
                RedesignedChallengeMenu(
                    appName = appName,
                    breathingDuration = breathingDuration,
                    pushupCount = pushupCount,
                    waitingDuration = delaySeconds,
                    onChallengeSelected = { challenge ->
                        selectedChallenge = challenge
                    },
                    onCancel = onCancel
                )
            }
        }
    }
}

@Composable
fun RedesignedChallengeMenu(
    appName: String,
    breathingDuration: Int,
    pushupCount: Int,
    waitingDuration: Int,
    onChallengeSelected: (String) -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        GlassIconBadge(
            icon = Icons.Filled.Lock,
            accentColor = AppColors.Error,
            size = 100.dp,
            iconSize = 48.dp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "$appName bloquée",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choisissez une activité saine",
            fontSize = 15.sp,
            color = AppColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            RedesignedChallengeCard(
                icon = Icons.Filled.Favorite,
                title = "Respiration",
                subtitle = "Exercice de pleine conscience",
                duration = "${breathingDuration / 60} min",
                accentColor = AppColors.Info,
                onClick = { onChallengeSelected("breathing") }
            )

            RedesignedChallengeCard(
                icon = Icons.Filled.Star,
                title = "Sport",
                subtitle = "Activité physique",
                duration = "$pushupCount pompes",
                accentColor = AppColors.Success,
                onClick = { onChallengeSelected("pushups") }
            )

            RedesignedChallengeCard(
                icon = Icons.Filled.Info,
                title = "Patience",
                subtitle = "Temps de réflexion",
                duration = "${waitingDuration / 60} min",
                accentColor = AppColors.Warning,
                onClick = { onChallengeSelected("waiting") }
            )

            RedesignedChallengeCard(
                icon = Icons.Filled.Star,
                title = "Quiz Culture G",
                subtitle = "Testez vos connaissances",
                duration = "1 question",
                accentColor = AppColors.Info,
                onClick = { onChallengeSelected("quiz") }
            )

            RedesignedChallengeCard(
                icon = Icons.Filled.Star,
                title = "Calcul Mental",
                subtitle = "Activité mathématique",
                duration = "1 calcul",
                accentColor = AppColors.Success,
                onClick = { onChallengeSelected("math") }
            )

            RedesignedChallengeCard(
                icon = Icons.Filled.Star,
                title = "Puzzle Logique",
                subtitle = "Entraînez votre cerveau",
                duration = "1 énigme",
                accentColor = AppColors.Warning,
                onClick = { onChallengeSelected("puzzle") }
            )

            RedesignedChallengeCard(
                icon = Icons.Filled.Favorite,
                title = "Méditation",
                subtitle = "Relaxation guidée",
                duration = "1 min",
                accentColor = AppColors.Primary,
                onClick = { onChallengeSelected("meditation") }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(
            onClick = onCancel,
            colors = ButtonDefaults.textButtonColors(
                contentColor = AppColors.OnSurfaceVariant
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retour à l'accueil")
        }
    }
}

@Composable
fun RedesignedChallengeCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    duration: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.height(100.dp),
        accentColor = accentColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconBadge(
                icon = icon,
                accentColor = accentColor,
                size = 56.dp,
                iconSize = 28.dp
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = duration,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
