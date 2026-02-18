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
import com.focusguard.app.AppColors

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
                    text = "Protection active",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Success
                )
                Text(
                    text = "$appCount app${if (appCount > 1) "s" else ""} surveillée${if (appCount > 1) "s" else ""}",
                    fontSize = 14.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }
        }
    }
}
