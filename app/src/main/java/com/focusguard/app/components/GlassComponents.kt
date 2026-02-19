package com.focusguard.app.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.AppColors

/**
 * Fond global avec 3 orbes flottantes animées.
 * Enveloppe tout le contenu d'un écran.
 */
@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")

    val offsetCyan by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cyan"
    )

    val offsetViolet by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -25f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "violet"
    )

    val offsetPink by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pink"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.BackgroundGlass)
    ) {
        // Orbe cyan (haut-gauche)
        Box(
            modifier = Modifier
                .offset(x = (-40).dp + offsetCyan.dp, y = 60.dp + (offsetCyan * 0.5f).dp)
                .size(220.dp)
                .blur(100.dp)
                .background(AppColors.OrbCyan, CircleShape)
        )

        // Orbe violet (centre-droite)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 50.dp + offsetViolet.dp, y = (-80).dp + (offsetViolet * 0.6f).dp)
                .size(200.dp)
                .blur(100.dp)
                .background(AppColors.OrbViolet, CircleShape)
        )

        // Orbe pink (bas-gauche)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 30.dp + offsetPink.dp, y = (-40).dp)
                .size(180.dp)
                .blur(100.dp)
                .background(AppColors.OrbPink, CircleShape)
        )

        // Contenu
        content()
    }
}

/**
 * Carte en verre dépoli avec bordure subtile et highlight en haut.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    accentColor: Color = AppColors.Primary,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickModifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppColors.GlassBg)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppColors.GlassHighlightEdge,
                        accentColor.copy(alpha = 0.08f),
                        AppColors.GlassBorderLight
                    )
                ),
                shape = shape
            )
            // Highlight top edge
            .drawBehind {
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            AppColors.GlassHighlightEdge,
                            Color.Transparent
                        )
                    ),
                    start = Offset(size.width * 0.1f, 0f),
                    end = Offset(size.width * 0.9f, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .then(clickModifier)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

/**
 * Bouton glassmorphism — translucide ou solide.
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = AppColors.Primary,
    isPrimary: Boolean = true,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val bgColor = if (isPrimary) {
        accentColor.copy(alpha = 0.2f)
    } else {
        AppColors.GlassBgSubtle
    }
    val borderColor = if (isPrimary) {
        accentColor.copy(alpha = 0.4f)
    } else {
        AppColors.GlassBorder
    }
    val contentColor = if (isPrimary) accentColor else AppColors.OnSurface

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = contentColor,
            disabledContainerColor = bgColor.copy(alpha = 0.1f),
            disabledContentColor = contentColor.copy(alpha = 0.4f)
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(borderColor, borderColor.copy(alpha = 0.2f))
            )
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        content = content
    )
}

/**
 * Cercle d'icône en verre givré.
 */
@Composable
fun GlassIconBadge(
    icon: ImageVector,
    accentColor: Color = AppColors.Primary,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                accentColor.copy(alpha = 0.15f),
                CircleShape
            )
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.3f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Champ texte givré glassmorphism.
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    accentColor: Color = AppColors.Primary,
    singleLine: Boolean = true
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = TextStyle(
            color = AppColors.OnSurface,
            fontSize = 16.sp
        ),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.GlassBgSubtle)
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = AppColors.OnSurfaceLight,
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

/**
 * Chip/tag en verre.
 */
@Composable
fun GlassChip(
    label: String,
    modifier: Modifier = Modifier,
    accentColor: Color = AppColors.Primary,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.12f))
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .then(clickModifier)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = accentColor
        )
    }
}

/**
 * Barre de progression translucide glassmorphism.
 */
@Composable
fun GlassProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    accentColor: Color = AppColors.Primary,
    height: Dp = 10.dp,
    trackColor: Color = AppColors.GlassBgSubtle
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(trackColor)
            .border(
                width = 0.5.dp,
                color = AppColors.GlassBorderLight,
                shape = RoundedCornerShape(height / 2)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.7f),
                            accentColor
                        )
                    )
                )
        )
    }
}
