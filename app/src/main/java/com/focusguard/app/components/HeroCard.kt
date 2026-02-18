package com.focusguard.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        modifier = Modifier.height(220.dp),
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

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.take_back_control),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.turn_distractions_into_opportunities),
                    fontSize = 14.sp,
                    color = AppColors.OnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
