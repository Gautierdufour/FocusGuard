package com.focusguard.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.AppColors

@Composable
fun DarkWarningCard(onRequestExemption: () -> Unit) {
    GlassCard(accentColor = AppColors.Warning) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlassIconBadge(
                icon = Icons.Filled.Warning,
                accentColor = AppColors.Warning,
                size = 44.dp,
                iconSize = 24.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Action requise",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Désactivez l'optimisation batterie pour un fonctionnement continu",
            fontSize = 14.sp,
            color = AppColors.OnSurfaceVariant,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        GlassButton(
            onClick = onRequestExemption,
            accentColor = AppColors.Warning
        ) {
            Icon(Icons.Filled.Settings, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Configurer", fontWeight = FontWeight.Bold)
        }
    }
}
