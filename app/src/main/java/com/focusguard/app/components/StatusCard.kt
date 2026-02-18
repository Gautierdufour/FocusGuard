package com.focusguard.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.focusguard.app.AppColors
import com.focusguard.app.R

@Composable
fun DarkStatusCard(appCount: Int) {
    GlassCard(accentColor = AppColors.Success) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconBadge(
                icon = Icons.Filled.CheckCircle,
                accentColor = AppColors.Success,
                size = 48.dp,
                iconSize = 26.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = stringResource(R.string.protection_active),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Success
                )
                Text(
                    text = "$appCount ${if (appCount > 1) stringResource(R.string.apps_monitored) else stringResource(R.string.app_monitored)}",
                    fontSize = 14.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }
        }
    }
}
