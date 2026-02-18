package com.focusguard.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.AppColors

@Composable
fun SmartPlanningCard(
    onOpenPlanning: () -> Unit
) {
    GlassCard(
        accentColor = AppColors.Primary,
        onClick = onOpenPlanning
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassIconBadge(
                    icon = Icons.Filled.Star,
                    accentColor = AppColors.Primary,
                    size = 56.dp,
                    iconSize = 28.dp
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Planification Intelligente",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Horaires / Focus / Pause / Géolocalisation",
                        fontSize = 13.sp,
                        color = AppColors.OnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = AppColors.Primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
