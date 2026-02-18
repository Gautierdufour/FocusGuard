package com.focusguard.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun DarkAppsCard(
    selectedApps: Set<String>,
    onConfigureClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    GlassCard(accentColor = AppColors.Primary) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlassIconBadge(
                icon = Icons.Filled.Phone,
                accentColor = AppColors.Primary,
                size = 44.dp,
                iconSize = 22.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.monitored_applications),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (selectedApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.Error.copy(alpha = 0.07f))
                    .border(
                        width = 1.dp,
                        color = AppColors.Error.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = AppColors.Error,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.no_app_selected),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Error
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.protection_requires_apps),
                        fontSize = 13.sp,
                        color = AppColors.OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.Success.copy(alpha = 0.08f))
                    .border(
                        width = 1.dp,
                        color = AppColors.Success.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = AppColors.Success,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${selectedApps.size}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Success
                        )
                        Text(
                            text = stringResource(R.string.apps_under_monitoring),
                            fontSize = 13.sp,
                            color = AppColors.OnSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassButton(
                onClick = onStatsClick,
                modifier = Modifier.weight(1f),
                accentColor = AppColors.Primary,
                isPrimary = false
            ) {
                Icon(Icons.Filled.Info, null, modifier = Modifier.size(18.dp), tint = AppColors.Primary)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.stats), color = AppColors.Primary)
            }

            GlassButton(
                onClick = onConfigureClick,
                modifier = Modifier.weight(1f),
                accentColor = AppColors.Primary,
                isPrimary = true
            ) {
                Icon(Icons.Filled.Settings, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.manage))
            }
        }
    }
}
