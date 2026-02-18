package com.focusguard.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.focusguard.app.AppColors

// Palette FocusGuard Dark
private val FocusGuardDarkScheme = darkColorScheme(
    primary = AppColors.Primary,
    onPrimary = Color.Black,
    primaryContainer = AppColors.PrimaryDark,
    onPrimaryContainer = AppColors.PrimaryLight,
    secondary = AppColors.Secondary,
    onSecondary = Color.Black,
    secondaryContainer = AppColors.SecondaryDark,
    onSecondaryContainer = AppColors.SecondaryLight,
    tertiary = AppColors.Accent,
    onTertiary = Color.Black,
    tertiaryContainer = AppColors.AccentDark,
    onTertiaryContainer = AppColors.AccentLight,
    error = AppColors.Error,
    onError = Color.Black,
    errorContainer = AppColors.ErrorDark,
    onErrorContainer = AppColors.ErrorLight,
    background = AppColors.Background,
    onBackground = AppColors.OnSurface,
    surface = AppColors.Surface,
    onSurface = AppColors.OnSurface,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.OnSurfaceVariant,
    outline = AppColors.OnSurfaceLight,
    inverseSurface = AppColors.OnSurface,
    inverseOnSurface = AppColors.Background,
    inversePrimary = AppColors.PrimaryDark
)

@Composable
fun FocusGuardTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }
        else -> FocusGuardDarkScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    FocusGuardTheme(dynamicColor = dynamicColor, content = content)
}
