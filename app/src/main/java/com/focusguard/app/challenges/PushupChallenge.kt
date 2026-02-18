package com.focusguard.app.challenges

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.AppColors
import com.focusguard.app.HybridPushupDetector
import com.focusguard.app.ProximityPushupDetector
import com.focusguard.app.PushupDetector
import com.focusguard.app.PushupInstructions
import com.focusguard.app.PushupMethod
import com.focusguard.app.PushupMethodSelector
import com.focusguard.app.ShakePushupDetector
import com.focusguard.app.getMethodDisplayName
import com.focusguard.app.components.GlassButton
import com.focusguard.app.components.GlassCard
import com.focusguard.app.components.GlassChip
import kotlinx.coroutines.delay

@Composable
fun RedesignedPushUpChallenge(
    targetCount: Int,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf<PushupMethod?>(null) }
    var showMethodSelector by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            showMethodSelector -> {
                PushupMethodSelector(
                    onMethodSelected = { method ->
                        selectedMethod = method
                        showMethodSelector = false
                    },
                    onBack = onBack
                )
            }
            else -> {
                selectedMethod?.let { method ->
                    UniversalPushupChallenge(
                        method = method,
                        targetCount = targetCount,
                        onComplete = onComplete,
                        onBack = {
                            showMethodSelector = true
                            selectedMethod = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UniversalPushupChallenge(
    method: PushupMethod,
    targetCount: Int,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val detector = remember(method) {
        when (method) {
            PushupMethod.AUTO -> PushupDetector(context)
            PushupMethod.HYBRID -> HybridPushupDetector(context)
            PushupMethod.PROXIMITY -> ProximityPushupDetector(context)
            PushupMethod.SHAKE -> ShakePushupDetector(context)
            PushupMethod.MANUAL -> null
        }
    }

    var manualCount by remember { mutableStateOf(0) }
    var showDebug by remember { mutableStateOf(true) }

    val pushUpCount = when (method) {
        PushupMethod.MANUAL -> manualCount
        PushupMethod.AUTO -> detector?.let { (it as PushupDetector).pushupCount.collectAsState().value } ?: 0
        PushupMethod.HYBRID -> detector?.let { (it as HybridPushupDetector).pushupCount.collectAsState().value } ?: 0
        PushupMethod.PROXIMITY -> detector?.let { (it as ProximityPushupDetector).pushupCount.collectAsState().value } ?: 0
        PushupMethod.SHAKE -> detector?.let { (it as ShakePushupDetector).pushupCount.collectAsState().value } ?: 0
    }

    val feedbackMessage = if (method == PushupMethod.AUTO) {
        (detector as? PushupDetector)?.feedbackMessage?.collectAsState()?.value ?: ""
    } else ""

    val currentPhase = if (method == PushupMethod.AUTO) {
        (detector as? PushupDetector)?.currentPhase?.collectAsState()?.value ?: "REPOS"
    } else "N/A"

    val zAxisValue = if (method == PushupMethod.AUTO) {
        (detector as? PushupDetector)?.zAxisValue?.collectAsState()?.value ?: 0f
    } else 0f

    val confidence = if (method == PushupMethod.HYBRID) {
        (detector as? HybridPushupDetector)?.confidence?.collectAsState()?.value ?: 0
    } else 100

    LaunchedEffect(method) {
        when (method) {
            PushupMethod.AUTO -> (detector as? PushupDetector)?.start()
            PushupMethod.HYBRID -> (detector as? HybridPushupDetector)?.start()
            PushupMethod.PROXIMITY -> (detector as? ProximityPushupDetector)?.start()
            PushupMethod.SHAKE -> (detector as? ShakePushupDetector)?.start()
            else -> {}
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            when (method) {
                PushupMethod.AUTO -> (detector as? PushupDetector)?.stop()
                PushupMethod.HYBRID -> (detector as? HybridPushupDetector)?.stop()
                PushupMethod.PROXIMITY -> (detector as? ProximityPushupDetector)?.stop()
                PushupMethod.SHAKE -> (detector as? ShakePushupDetector)?.stop()
                else -> {}
            }
        }
    }

    LaunchedEffect(pushUpCount) {
        if (pushUpCount >= targetCount) {
            delay(1500)
            onComplete()
        }
    }

    val isComplete = pushUpCount >= targetCount
    val progress = (pushUpCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Défi sportif",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = getMethodDisplayName(method),
                fontSize = 13.sp,
                color = AppColors.OnSurfaceVariant
            )

            if (method == PushupMethod.AUTO) {
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = showDebug,
                    onCheckedChange = { showDebug = it },
                    modifier = Modifier.scale(0.7f),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AppColors.Info,
                        checkedTrackColor = AppColors.Info.copy(alpha = 0.5f)
                    )
                )
                Text(
                    text = "Debug",
                    fontSize = 10.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isComplete && method != PushupMethod.MANUAL) {
            PushupInstructions(method)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (method == PushupMethod.AUTO && showDebug && !isComplete) {
            DebugInfoCard(
                phase = currentPhase,
                zValue = zAxisValue
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(contentAlignment = Alignment.Center) {
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "progress"
            )

            CircularProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.size(200.dp),
                color = AppColors.Success,
                strokeWidth = 14.dp,
                trackColor = AppColors.GlassBgElevated
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$pushUpCount",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Success
                )
                Text(
                    text = "/ $targetCount",
                    fontSize = 20.sp,
                    color = AppColors.OnSurfaceVariant
                )

                if (method == PushupMethod.AUTO && !isComplete && showDebug) {
                    Spacer(modifier = Modifier.height(8.dp))
                    PhaseIndicator(currentPhase)
                }

                if (method == PushupMethod.HYBRID && !isComplete && confidence > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Confiance: $confidence%",
                        fontSize = 12.sp,
                        color = when {
                            confidence >= 70 -> AppColors.Success
                            confidence >= 40 -> AppColors.Warning
                            else -> AppColors.Error
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!isComplete) {
            when (method) {
                PushupMethod.AUTO -> {
                    if (feedbackMessage.isNotEmpty()) {
                        GlassCard(
                            accentColor = when {
                                feedbackMessage.contains("\u2705") -> AppColors.Success
                                feedbackMessage.contains("\u26A0\uFE0F") -> AppColors.Warning
                                feedbackMessage.contains("\u274C") -> AppColors.Error
                                else -> AppColors.Primary
                            }
                        ) {
                            Text(
                                text = feedbackMessage,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = when {
                                    feedbackMessage.contains("\u2705") -> AppColors.Success
                                    feedbackMessage.contains("\u26A0\uFE0F") -> AppColors.Warning
                                    feedbackMessage.contains("\u274C") -> AppColors.Error
                                    feedbackMessage.contains("\u2B07\uFE0F") -> AppColors.Warning
                                    feedbackMessage.contains("\u2B06\uFE0F") -> AppColors.Info
                                    feedbackMessage.contains("\uD83D\uDD3B") -> AppColors.Error
                                    else -> AppColors.OnSurface
                                }
                            )
                        }
                    }
                }

                PushupMethod.MANUAL -> {
                    GlassButton(
                        onClick = { manualCount++ },
                        modifier = Modifier.size(110.dp),
                        accentColor = AppColors.Success
                    ) {
                        Text(
                            text = "+1",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                else -> {
                    // Animation fonctionnelle capteur — conservée
                    val infiniteTransition = rememberInfiniteTransition(label = "detecting")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.15f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .scale(scale)
                            .background(
                                AppColors.Success.copy(alpha = 0.15f),
                                CircleShape
                            )
                            .border(1.5.dp, AppColors.Success.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = AppColors.Success,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            }
        } else {
            SuccessAnimation(targetCount)
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (!isComplete) {
            TextButton(onClick = onBack) {
                Text("\u2190 Méthode", color = AppColors.OnSurfaceVariant, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun DebugInfoCard(phase: String, zValue: Float) {
    GlassCard(accentColor = AppColors.Info) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Phase", fontSize = 10.sp, color = AppColors.OnSurfaceVariant)
                Text(
                    text = phase,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (phase) {
                        "REPOS" -> AppColors.Primary
                        "DESCENTE" -> AppColors.Warning
                        "BAS" -> AppColors.Error
                        "MONTEE" -> AppColors.Success
                        else -> AppColors.OnSurface
                    }
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp),
                color = AppColors.GlassBorder
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Axe Z", fontSize = 10.sp, color = AppColors.OnSurfaceVariant)
                Text(
                    text = String.format("%.2f", zValue),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        zValue < 7f || zValue > 12f -> AppColors.Error
                        zValue < 8.5f -> AppColors.Warning
                        else -> AppColors.Success
                    }
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp),
                color = AppColors.GlassBorder
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "État", fontSize = 10.sp, color = AppColors.OnSurfaceVariant)
                Icon(
                    imageVector = when {
                        zValue < 7f || zValue > 12f -> Icons.Filled.Warning
                        else -> Icons.Filled.CheckCircle
                    },
                    contentDescription = null,
                    tint = when {
                        zValue < 7f || zValue > 12f -> AppColors.Error
                        else -> AppColors.Success
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun PhaseIndicator(phase: String) {
    val phaseColor = when (phase) {
        "REPOS" -> AppColors.Primary
        "DESCENTE" -> AppColors.Warning
        "BAS" -> AppColors.Error
        "MONTEE" -> AppColors.Success
        else -> AppColors.OnSurface
    }

    val phaseIcon = when (phase) {
        "REPOS" -> "\u2501"
        "DESCENTE" -> "\u2193"
        "BAS" -> "\u25BC"
        "MONTEE" -> "\u2191"
        else -> "?"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = phaseIcon, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = phaseColor)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = phase, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = phaseColor)
    }
}

@Composable
fun SuccessAnimation(count: Int) {
    // Animation fonctionnelle de succès — conservée
    val infiniteTransition = rememberInfiniteTransition(label = "success")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = AppColors.Success,
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer(rotationZ = rotation)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Excellent travail !",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Success
        )
        Text(
            text = "$count pompes complétées",
            fontSize = 14.sp,
            color = AppColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
