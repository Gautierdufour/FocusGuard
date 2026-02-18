package com.focusguard.app.challenges

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.AppColors
import com.focusguard.app.R
import kotlinx.coroutines.delay

@Composable
fun RedesignedBreathingChallenge(
    duration: Int,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(duration) }
    var breathingPhase by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
            if (timeLeft % 4 == 0) {
                breathingPhase = !breathingPhase
            }
        }
        onComplete()
    }

    val glowColor = if (breathingPhase) AppColors.Info else AppColors.Primary

    // Animation fonctionnelle de respiration — conservée
    val scale by rememberInfiniteTransition(label = "scale").animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.breathing_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        glowColor.copy(alpha = 0.10f),
                        CircleShape
                    )
                    .border(2.dp, glowColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${timeLeft / 60}:${String.format("%02d", timeLeft % 60)}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = glowColor
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Animation fonctionnelle de respiration — cercle qui pulse
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(if (breathingPhase) scale else 1f / scale)
                    .background(
                        glowColor.copy(alpha = 0.10f),
                        CircleShape
                    )
                    .border(1.5.dp, glowColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (breathingPhase)
                            Icons.Filled.KeyboardArrowUp
                        else
                            Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = glowColor,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (breathingPhase) stringResource(R.string.inhale) else stringResource(R.string.exhale),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = glowColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            TextButton(onClick = onBack) {
                Text(stringResource(R.string.change_activity), color = AppColors.OnSurfaceVariant)
            }
        }
    }
}
