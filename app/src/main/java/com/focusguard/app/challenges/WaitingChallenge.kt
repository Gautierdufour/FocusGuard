package com.focusguard.app.challenges

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.AppColors
import com.focusguard.app.components.GlassButton
import kotlinx.coroutines.delay

@Composable
fun RedesignedWaitingChallenge(
    delaySeconds: Int,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(delaySeconds) }
    var isFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        isFinished = true
    }

    val glowColor = if (isFinished) AppColors.Success else AppColors.Warning
    val progress = 1f - (timeLeft.toFloat() / delaySeconds.toFloat())

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Temps de réflexion",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(220.dp),
                    color = glowColor,
                    strokeWidth = 12.dp,
                    trackColor = AppColors.GlassBgElevated
                )

                Text(
                    text = "${timeLeft / 60}:${String.format("%02d", timeLeft % 60)}",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = glowColor
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (isFinished) {
                GlassButton(
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(64.dp),
                    accentColor = AppColors.Success
                ) {
                    Icon(Icons.Filled.Check, null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Continuer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    GlassButton(
                        onClick = onCancel,
                        accentColor = AppColors.Error,
                        isPrimary = false
                    ) {
                        Icon(Icons.Filled.Home, null, modifier = Modifier.size(18.dp), tint = AppColors.Error)
                        Spacer(Modifier.width(8.dp))
                        Text("Retour à l'accueil", color = AppColors.Error)
                    }

                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("\u2190 Changer d'activité", color = AppColors.OnSurfaceVariant)
                    }
                }
            }
        }
    }
}
