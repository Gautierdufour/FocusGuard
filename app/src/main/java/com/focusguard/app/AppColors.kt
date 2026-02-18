package com.focusguard.app

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppColors {
    // 🌙 DARK THEME - Couleurs de base
    val Background = Color(0xFF0A0E27) // Bleu nuit très foncé
    val Surface = Color(0xFF151932) // Surface principale
    val SurfaceVariant = Color(0xFF1E2440) // Surface secondaire
    val SurfaceCard = Color(0xFF1A1F3A) // Cartes
    val SurfaceElevated = Color(0xFF252B47) // Cartes surélevées

    // 🎨 Couleurs néon/accent
    val Primary = Color(0xFF00D9FF) // Cyan électrique
    val PrimaryDark = Color(0xFF00B8D4)
    val PrimaryLight = Color(0xFF4DFFFF)

    val Secondary = Color(0xFFBB86FC) // Violet néon
    val SecondaryDark = Color(0xFF9965F4)
    val SecondaryLight = Color(0xFFD7BBFF)

    val Accent = Color(0xFFFF4081) // Rose néon
    val AccentDark = Color(0xFFE91E63)
    val AccentLight = Color(0xFFFF6EA0)

    // ✨ Couleurs de glow/brillance
    val GlowCyan = Color(0xFF00D9FF).copy(alpha = 0.3f)
    val GlowPurple = Color(0xFFBB86FC).copy(alpha = 0.3f)
    val GlowPink = Color(0xFFFF4081).copy(alpha = 0.3f)

    // 📝 Couleurs de texte
    val OnSurface = Color(0xFFE8EAF6) // Texte principal
    val OnSurfaceVariant = Color(0xFFB0B3C7) // Texte secondaire
    val OnSurfaceLight = Color(0xFF7A7E94) // Texte désactivé

    // 🎯 Couleurs sémantiques néon
    val Success = Color(0xFF00E676) // Vert néon
    val SuccessLight = Color(0xFF69F0AE)
    val SuccessDark = Color(0xFF00C853)

    val Warning = Color(0xFFFFD600) // Jaune néon
    val WarningLight = Color(0xFFFFFF00)
    val WarningDark = Color(0xFFFFC400)

    val Error = Color(0xFFFF3D00) // Rouge néon
    val ErrorLight = Color(0xFFFF6E40)
    val ErrorDark = Color(0xFFDD2C00)

    val Info = Color(0xFF00B0FF) // Bleu néon
    val InfoLight = Color(0xFF40C4FF)
    val InfoDark = Color(0xFF0091EA)

    // 🌈 Gradients modernes dark
    val GradientPrimary = Brush.linearGradient(
        colors = listOf(
            Primary,
            Secondary
        )
    )

    val GradientSuccess = Brush.linearGradient(
        colors = listOf(
            Success,
            Color(0xFF00BFA5)
        )
    )

    val GradientWarning = Brush.linearGradient(
        colors = listOf(
            Warning,
            Color(0xFFFFAB00)
        )
    )

    val GradientError = Brush.linearGradient(
        colors = listOf(
            Error,
            Accent
        )
    )

    val GradientPurple = Brush.linearGradient(
        colors = listOf(
            Secondary,
            Accent
        )
    )

    val GradientCyan = Brush.linearGradient(
        colors = listOf(
            Primary,
            Info
        )
    )

    // 🎴 Gradients pour cartes (plus subtils)
    val GradientCardPrimary = Brush.verticalGradient(
        colors = listOf(
            Primary.copy(alpha = 0.2f),
            Primary.copy(alpha = 0.05f)
        )
    )

    val GradientCardSuccess = Brush.verticalGradient(
        colors = listOf(
            Success.copy(alpha = 0.2f),
            Success.copy(alpha = 0.05f)
        )
    )

    val GradientCardWarning = Brush.verticalGradient(
        colors = listOf(
            Warning.copy(alpha = 0.2f),
            Warning.copy(alpha = 0.05f)
        )
    )

    val GradientCardError = Brush.verticalGradient(
        colors = listOf(
            Error.copy(alpha = 0.2f),
            Error.copy(alpha = 0.05f)
        )
    )

    // 🌟 Gradient Hero (pour la carte principale)
    val GradientHero = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1A1F3A),
            Color(0xFF0A0E27),
            Color(0xFF1E2440)
        )
    )

    val GradientHeroNeon = Brush.linearGradient(
        colors = listOf(
            Primary.copy(alpha = 0.4f),
            Secondary.copy(alpha = 0.4f),
            Accent.copy(alpha = 0.4f)
        )
    )

    // 🔆 Ombres et effets
    val Shadow = Color(0xFF000000).copy(alpha = 0.6f)
    val Overlay = Color(0xFF000000).copy(alpha = 0.7f)
    val Glow = Color(0xFF00D9FF).copy(alpha = 0.2f)

    // 🪟 Glassmorphism tokens
    val BackgroundGlass = Color(0xFF080B1A)

    // Fonds de verre semi-transparents
    val GlassBg = Color.White.copy(alpha = 0.08f)
    val GlassBgElevated = Color.White.copy(alpha = 0.12f)
    val GlassBgSubtle = Color.White.copy(alpha = 0.05f)

    // Bordures subtiles
    val GlassBorder = Color.White.copy(alpha = 0.15f)
    val GlassBorderLight = Color.White.copy(alpha = 0.10f)

    // Highlight edge (effet lumière en haut des cartes)
    val GlassHighlightEdge = Color.White.copy(alpha = 0.12f)

    // Orbes flottantes de fond
    val OrbCyan = Color(0xFF00D9FF).copy(alpha = 0.15f)
    val OrbViolet = Color(0xFFBB86FC).copy(alpha = 0.12f)
    val OrbPink = Color(0xFFFF4081).copy(alpha = 0.10f)
}