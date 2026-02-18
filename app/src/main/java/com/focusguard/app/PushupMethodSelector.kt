package com.focusguard.app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import com.focusguard.app.components.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class PushupMethod {
    AUTO,           // Accéléromètre seul
    HYBRID,         // Accéléromètre + Proximité
    PROXIMITY,      // Proximité seul
    SHAKE,          // Secousses rythmées
    MANUAL          // Manuel
}

@Composable
fun PushupMethodSelector(
    onMethodSelected: (PushupMethod) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val availableMethods = remember { detectAvailableSensors(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choisissez une méthode",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Comment voulez-vous compter vos pompes ?",
            fontSize = 14.sp,
            color = AppColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PushupMethodCard(
                    method = PushupMethod.AUTO,
                    title = "Automatique",
                    subtitle = "Détection par capteurs",
                    description = "Posez le téléphone au sol",
                    icon = Icons.Filled.Star,
                    isRecommended = true,
                    isAvailable = availableMethods.contains(PushupMethod.AUTO),
                    onClick = { onMethodSelected(PushupMethod.AUTO) }
                )
            }

            item {
                PushupMethodCard(
                    method = PushupMethod.HYBRID,
                    title = "Hybride",
                    subtitle = "Double vérification",
                    description = "Capteurs multiples pour plus de précision",
                    icon = Icons.Filled.CheckCircle,
                    isRecommended = false,
                    isAvailable = availableMethods.contains(PushupMethod.HYBRID),
                    onClick = { onMethodSelected(PushupMethod.HYBRID) }
                )
            }

            item {
                PushupMethodCard(
                    method = PushupMethod.MANUAL,
                    title = "Manuel",
                    subtitle = "Comptage manuel",
                    description = "Appuyez sur +1 après chaque pompe",
                    icon = Icons.Filled.TouchApp,
                    isRecommended = false,
                    isAvailable = availableMethods.contains(PushupMethod.MANUAL),
                    onClick = { onMethodSelected(PushupMethod.MANUAL) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onBack) {
            Text("← Retour", color = AppColors.OnSurfaceVariant)
        }
    }
}

@Composable
fun PushupMethodCard(
    method: PushupMethod,
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    isRecommended: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit
) {
    val alphaValue = if (isAvailable) 1f else 0.5f
    val accentColor = if (isRecommended) AppColors.Success else AppColors.Primary

    GlassCard(
        accentColor = accentColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.alpha(alphaValue),
        onClick = if (isAvailable) onClick else null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconBadge(
                icon = icon,
                accentColor = accentColor,
                size = 56.dp,
                iconSize = 28.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface.copy(alpha = alphaValue)
                    )
                    if (isRecommended) {
                        Spacer(modifier = Modifier.width(8.dp))
                        GlassChip(label = "★", accentColor = AppColors.Success)
                    }
                }

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = AppColors.OnSurfaceVariant.copy(alpha = alphaValue)
                )
            }

            if (isAvailable) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = AppColors.OnSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = AppColors.Error,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isAvailable) {
                description
            } else {
                if (method == PushupMethod.MANUAL) {
                    "Bloqué - Capteur de proximité détecté"
                } else {
                    "Capteur non disponible"
                }
            },
            fontSize = 13.sp,
            color = if (isAvailable)
                AppColors.OnSurfaceVariant.copy(alpha = alphaValue)
            else
                AppColors.Error.copy(alpha = alphaValue),
            lineHeight = 18.sp
        )
    }
}

fun detectAvailableSensors(context: Context): List<PushupMethod> {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val available = mutableListOf<PushupMethod>()

    // Vérifier l'accéléromètre
    if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
        available.add(PushupMethod.AUTO)
        available.add(PushupMethod.SHAKE)
    }

    // Vérifier le capteur de proximité
    val hasProximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null
    
    if (hasProximitySensor) {
        available.add(PushupMethod.PROXIMITY)

        // Hybride nécessite les deux
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            available.add(PushupMethod.HYBRID)
        }
    }

    // ⚠️ Mode manuel BLOQUÉ si capteur de proximité disponible
    // Cela force l'utilisateur à utiliser la détection automatique
    if (!hasProximitySensor) {
        available.add(PushupMethod.MANUAL)
    }

    return available
}

@Composable
fun PushupInstructions(method: PushupMethod) {
    GlassCard(
        accentColor = AppColors.Info,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconBadge(
                icon = Icons.Filled.Info,
                accentColor = AppColors.Info,
                size = 40.dp,
                iconSize = 20.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = getInstructionTitle(method),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )
                Text(
                    text = getInstructionText(method),
                    fontSize = 12.sp,
                    color = AppColors.OnSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

fun getInstructionTitle(method: PushupMethod): String {
    return when (method) {
        PushupMethod.AUTO -> "Placez le téléphone au sol"
        PushupMethod.HYBRID -> "Position optimale"
        PushupMethod.PROXIMITY -> "Téléphone devant vous"
        PushupMethod.SHAKE -> "Tenez le téléphone"
        PushupMethod.MANUAL -> "Comptage manuel"
    }
}

fun getInstructionText(method: PushupMethod): String {
    return when (method) {
        PushupMethod.AUTO -> "Face vers le haut, sous votre torse. Le téléphone détectera vos mouvements verticaux."
        PushupMethod.HYBRID -> "Au sol avec le capteur de proximité vers le haut. Double vérification activée."
        PushupMethod.PROXIMITY -> "Le téléphone doit détecter votre visage à chaque descente."
        PushupMethod.SHAKE -> "Secouez rythmiquement à chaque pompe. Gardez un rythme régulier."
        PushupMethod.MANUAL -> "Appuyez sur le bouton +1 après chaque pompe effectuée."
    }
}

fun getMethodDisplayName(method: PushupMethod): String {
    return when (method) {
        PushupMethod.AUTO -> "Mode automatique"
        PushupMethod.HYBRID -> "Mode hybride"
        PushupMethod.PROXIMITY -> "Mode proximité"
        PushupMethod.SHAKE -> "Mode secousses"
        PushupMethod.MANUAL -> "Mode manuel"
    }
}
