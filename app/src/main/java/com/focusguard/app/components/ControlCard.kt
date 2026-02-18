package com.focusguard.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.AppColors

@Composable
fun DarkControlCard(
    selectedApps: Set<String>,
    serviceRunning: Boolean,
    onServiceToggle: (Boolean) -> Unit,
    onConfigureApps: () -> Unit
) {
    GlassCard(
        accentColor = if (serviceRunning) AppColors.Success else AppColors.OnSurfaceLight
    ) {
        Text(
            text = "Contrôle du service",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedApps.isEmpty()) {
            GlassButton(
                onClick = onConfigureApps,
                accentColor = AppColors.Warning
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Sélectionner des apps",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            GlassButton(
                onClick = { onServiceToggle(!serviceRunning) },
                accentColor = if (serviceRunning) AppColors.Error else AppColors.Success,
                modifier = Modifier.height(60.dp)
            ) {
                Icon(
                    imageVector = if (serviceRunning)
                        Icons.Filled.Clear
                    else
                        Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (serviceRunning) "Arrêter" else "Démarrer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
