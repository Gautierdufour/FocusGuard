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
import androidx.compose.ui.res.stringResource
import com.focusguard.app.AppColors
import com.focusguard.app.R

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
            text = stringResource(R.string.service_control),
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
                    stringResource(R.string.select_apps),
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
                    text = if (serviceRunning) stringResource(R.string.stop) else stringResource(R.string.start),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
