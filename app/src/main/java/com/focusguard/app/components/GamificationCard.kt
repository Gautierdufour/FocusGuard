package com.focusguard.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.AppColors
import com.focusguard.app.GamificationManager

@Composable
fun DarkGamificationCard(refreshTick: Int = 0) {
    val context = LocalContext.current
    val level = GamificationManager.getCurrentLevel(context)
    val xp = GamificationManager.getTotalXP(context)
    val xpProgress = GamificationManager.getXPProgress(context)
    val xpToNext = GamificationManager.getXPToNextLevel(context)
    val currentStreak = GamificationManager.getCurrentStreak(context)
    val levelTitle = GamificationManager.getLevelTitle(level)
    val levelColor = GamificationManager.getLevelColor(level)

    GlassCard(accentColor = levelColor) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Badge niveau en verre
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            levelColor.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .border(1.dp, levelColor.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = level.toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = levelColor
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = levelTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = levelColor
                    )
                    Text(
                        text = "Niveau $level",
                        fontSize = 12.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                }
            }

            if (currentStreak > 0) {
                GlassChip(
                    label = currentStreak.toString(),
                    accentColor = AppColors.Error,
                    icon = null
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$xp XP",
                    fontSize = 12.sp,
                    color = AppColors.OnSurfaceVariant
                )
                Text(
                    text = "$xpToNext XP restants",
                    fontSize = 12.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            GlassProgressBar(
                progress = xpProgress,
                accentColor = levelColor,
                height = 12.dp
            )
        }
    }
}
