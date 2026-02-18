package com.focusguard.app.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.AppColors

@Composable
fun DarkPermissionsCard(context: Context) {
    GlassCard(accentColor = AppColors.Secondary) {
        Text(
            text = "Permissions requises",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        DarkPermissionItem(
            icon = Icons.Filled.Info,
            title = "Statistiques d'usage",
            onClick = {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        DarkPermissionItem(
            icon = Icons.Filled.Place,
            title = "Superposition d'écran",
            onClick = {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun DarkPermissionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    GlassButton(
        onClick = onClick,
        accentColor = AppColors.Primary,
        isPrimary = false
    ) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = AppColors.Primary)
        Spacer(Modifier.width(12.dp))
        Text(title, modifier = Modifier.weight(1f), color = AppColors.OnSurface)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = AppColors.OnSurfaceVariant)
    }
}
