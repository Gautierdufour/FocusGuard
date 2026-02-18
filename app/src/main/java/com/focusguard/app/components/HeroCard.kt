package com.focusguard.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.focusguard.app.AppColors
import com.focusguard.app.R

@Composable
fun DarkHeroCard() {
    GlassCard(
        modifier = Modifier.height(240.dp),
        accentColor = AppColors.Primary,
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlassIconBadge(
                    icon = Icons.Filled.Lock,
                    accentColor = AppColors.Primary,
                    size = 72.dp,
                    iconSize = 34.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.take_back_control),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Value proposition : 3 étapes concrètes
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeroStep(emoji = "🔒", label = stringResource(R.string.hero_step_block), color = AppColors.Primary)
                    Text("→", fontSize = 14.sp, color = AppColors.OnSurfaceVariant)
                    HeroStep(emoji = "🏋️", label = stringResource(R.string.hero_step_challenge), color = AppColors.Success)
                    Text("→", fontSize = 14.sp, color = AppColors.OnSurfaceVariant)
                    HeroStep(emoji = "⭐", label = stringResource(R.string.hero_step_level_up), color = AppColors.Warning)
                }
            }
        }
    }
}

@Composable
private fun HeroStep(emoji: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(emoji, fontSize = 18.sp)
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}
