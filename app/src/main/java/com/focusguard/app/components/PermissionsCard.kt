package com.focusguard.app.components

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.focusguard.app.AppColors
import com.focusguard.app.R

@Composable
fun DarkPermissionsCard(context: Context) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshTick by remember { mutableStateOf(0) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshTick++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val hasUsageStats = remember(refreshTick) { hasUsageStatsPermission(context) }
    val hasOverlay = remember(refreshTick) { Settings.canDrawOverlays(context) }
    val isBatteryOptimized = remember(refreshTick) { isBatteryOptimized(context) }
    val allGranted = hasUsageStats && hasOverlay && !isBatteryOptimized

    GlassCard(accentColor = if (allGranted) AppColors.Success else AppColors.Secondary) {
        Text(
            text = stringResource(R.string.required_permissions),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        DarkPermissionItem(
            icon = Icons.Filled.Info,
            title = stringResource(R.string.usage_statistics),
            isGranted = hasUsageStats,
            onClick = {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        DarkPermissionItem(
            icon = Icons.Filled.Place,
            title = stringResource(R.string.screen_overlay),
            isGranted = hasOverlay,
            onClick = {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        DarkPermissionItem(
            icon = Icons.Filled.Warning,
            title = stringResource(R.string.battery_optimization),
            isGranted = !isBatteryOptimized,
            actionText = stringResource(R.string.battery_optimization_action),
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }
            }
        )
    }
}

@Composable
fun DarkPermissionItem(
    icon: ImageVector,
    title: String,
    isGranted: Boolean,
    actionText: String = stringResource(R.string.permission_required_action),
    onClick: () -> Unit
) {
    val accentColor = if (isGranted) AppColors.Success else AppColors.Warning

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.08f))
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
    ) {
        if (isGranted) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = AppColors.Success)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    color = AppColors.OnSurface,
                    fontSize = 14.sp
                )
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = stringResource(R.string.permission_granted),
                    tint = AppColors.Success,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.permission_granted),
                    fontSize = 12.sp,
                    color = AppColors.Success,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            GlassButton(
                onClick = onClick,
                accentColor = AppColors.Warning,
                isPrimary = false
            ) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = AppColors.Warning)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = AppColors.OnSurface, fontSize = 14.sp)
                    Text(
                        text = actionText,
                        fontSize = 11.sp,
                        color = AppColors.Warning
                    )
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = AppColors.Warning)
            }
        }
    }
}

private fun hasUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

private fun isBatteryOptimized(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return !pm.isIgnoringBatteryOptimizations(context.packageName)
}
