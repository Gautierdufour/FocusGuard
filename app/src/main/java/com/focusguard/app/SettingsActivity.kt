package com.focusguard.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.focusguard.app.ui.theme.FocusGuardTheme
import com.focusguard.app.components.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusGuardTheme {
                GlassBackground {
                    DarkSettingsScreen(onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkSettingsScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current

    var accessDuration by remember { mutableStateOf(AppPreferences.getAccessDuration(context)) }
    var pushupCount by remember { mutableStateOf(AppPreferences.getPushupCount(context)) }
    var waitingDuration by remember { mutableStateOf(AppPreferences.getWaitingDuration(context)) }
    var breathingDuration by remember { mutableStateOf(AppPreferences.getBreathingDuration(context)) }
    var quizCount by remember { mutableStateOf(AppPreferences.getQuizCount(context)) }
    var mathCount by remember { mutableStateOf(AppPreferences.getMathCount(context)) }
    var puzzleCount by remember { mutableStateOf(AppPreferences.getPuzzleCount(context)) }
    var meditationDuration by remember { mutableStateOf(AppPreferences.getMeditationDuration(context)) }
    var dailyGoal by remember { mutableStateOf(AppPreferences.getDailyGoal(context)) }
    var notificationsEnabled by remember { mutableStateOf(AppPreferences.areNotificationsEnabled(context)) }

    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paramètres",
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = AppColors.Primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Réinitialiser",
                            tint = AppColors.Warning
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.GlassBg,
                    titleContentColor = AppColors.OnSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item { DarkSettingsHeader() }

            item {
                DarkSettingsCard(
                    title = "Durée d'accès après défi",
                    icon = Icons.Filled.Star,
                    description = "Temps d'accès une fois le défi complété",
                    accentColor = AppColors.Primary
                ) {
                    DarkPresetSelector(
                        label = "Durée",
                        value = accessDuration,
                        options = PresetDurations.ACCESS_DURATIONS,
                        formatValue = { "$it min" },
                        onValueChange = {
                            accessDuration = it
                            AppPreferences.setAccessDuration(context, it)
                        },
                        accentColor = AppColors.Primary
                    )
                }
            }

            item {
                DarkSettingsCard(
                    title = "Configuration des défis",
                    icon = Icons.Filled.Settings,
                    description = "Personnalisez la difficulté",
                    accentColor = AppColors.Secondary
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        DarkPresetSelector(
                            label = "Pompes requises",
                            value = pushupCount,
                            options = PresetDurations.PUSHUP_COUNTS,
                            formatValue = { "$it pompes" },
                            onValueChange = {
                                pushupCount = it
                                AppPreferences.setPushupCount(context, it)
                            },
                            accentColor = AppColors.Success
                        )

                        DarkPresetSelector(
                            label = "Durée d'attente",
                            value = waitingDuration,
                            options = PresetDurations.WAITING_DURATIONS,
                            formatValue = { PresetDurations.formatDuration(it) },
                            onValueChange = {
                                waitingDuration = it
                                AppPreferences.setWaitingDuration(context, it)
                            },
                            accentColor = AppColors.Warning
                        )

                        DarkPresetSelector(
                            label = "Durée de respiration",
                            value = breathingDuration,
                            options = PresetDurations.BREATHING_DURATIONS,
                            formatValue = { PresetDurations.formatDuration(it) },
                            onValueChange = {
                                breathingDuration = it
                                AppPreferences.setBreathingDuration(context, it)
                            },
                            accentColor = AppColors.Info
                        )
                    }
                }
            }

            item {
                DarkSettingsCard(
                    title = "Défis Cognitifs",
                    icon = Icons.Filled.Star,
                    description = "Configurez les défis mentaux",
                    accentColor = AppColors.Info
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        DarkPresetSelector(
                            label = "Questions Quiz",
                            value = quizCount,
                            options = PresetDurations.QUIZ_COUNTS,
                            formatValue = { "$it question${if (it > 1) "s" else ""}" },
                            onValueChange = {
                                quizCount = it
                                AppPreferences.setQuizCount(context, it)
                            },
                            accentColor = AppColors.Info
                        )

                        DarkPresetSelector(
                            label = "Calculs Mathématiques",
                            value = mathCount,
                            options = PresetDurations.MATH_COUNTS,
                            formatValue = { "$it calcul${if (it > 1) "s" else ""}" },
                            onValueChange = {
                                mathCount = it
                                AppPreferences.setMathCount(context, it)
                            },
                            accentColor = AppColors.Success
                        )

                        DarkPresetSelector(
                            label = "Puzzles Logiques",
                            value = puzzleCount,
                            options = PresetDurations.PUZZLE_COUNTS,
                            formatValue = { "$it puzzle${if (it > 1) "s" else ""}" },
                            onValueChange = {
                                puzzleCount = it
                                AppPreferences.setPuzzleCount(context, it)
                            },
                            accentColor = AppColors.Warning
                        )

                        DarkPresetSelector(
                            label = "Durée de Méditation",
                            value = meditationDuration,
                            options = PresetDurations.MEDITATION_DURATIONS,
                            formatValue = { PresetDurations.formatDuration(it) },
                            onValueChange = {
                                meditationDuration = it
                                AppPreferences.setMeditationDuration(context, it)
                            },
                            accentColor = AppColors.Primary
                        )
                    }
                }
            }

            item {
                DarkSettingsCard(
                    title = "Objectifs quotidiens",
                    icon = Icons.Filled.Favorite,
                    description = "Définissez votre objectif journalier",
                    accentColor = AppColors.Accent
                ) {
                    DarkPresetSelector(
                        label = "Objectif",
                        value = dailyGoal,
                        options = PresetDurations.DAILY_GOALS,
                        formatValue = { "$it blocages/jour" },
                        onValueChange = {
                            dailyGoal = it
                            AppPreferences.setDailyGoal(context, it)
                        },
                        accentColor = AppColors.Accent
                    )
                }
            }

            item {
                DarkSettingsCard(
                    title = "Notifications",
                    icon = Icons.Filled.Info,
                    description = "Rapports quotidiens et encouragements",
                    accentColor = AppColors.Info
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                AppColors.GlassBgSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                AppColors.GlassBorderLight,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notifications activées",
                            fontSize = 14.sp,
                            color = AppColors.OnSurface,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                notificationsEnabled = it
                                AppPreferences.setNotificationsEnabled(context, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = AppColors.Primary,
                                uncheckedThumbColor = AppColors.OnSurfaceLight,
                                uncheckedTrackColor = AppColors.SurfaceVariant
                            )
                        )
                    }
                }
            }

            item {
                DarkSummaryCard(
                    accessDuration = accessDuration,
                    pushupCount = pushupCount,
                    waitingDuration = waitingDuration,
                    breathingDuration = breathingDuration,
                    quizCount = quizCount,
                    mathCount = mathCount,
                    puzzleCount = puzzleCount,
                    meditationDuration = meditationDuration,
                    dailyGoal = dailyGoal
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = AppColors.GlassBgElevated,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = AppColors.Warning,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    "Réinitialiser les paramètres ?",
                    color = AppColors.OnSurface
                )
            },
            text = {
                Text(
                    "Tous les paramètres seront restaurés aux valeurs par défaut. Les apps sélectionnées seront conservées.",
                    color = AppColors.OnSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        AppPreferences.resetToDefaults(context)
                        accessDuration = AppPreferences.getAccessDuration(context)
                        pushupCount = AppPreferences.getPushupCount(context)
                        waitingDuration = AppPreferences.getWaitingDuration(context)
                        breathingDuration = AppPreferences.getBreathingDuration(context)
                        quizCount = AppPreferences.getQuizCount(context)
                        mathCount = AppPreferences.getMathCount(context)
                        puzzleCount = AppPreferences.getPuzzleCount(context)
                        meditationDuration = AppPreferences.getMeditationDuration(context)
                        dailyGoal = AppPreferences.getDailyGoal(context)
                        notificationsEnabled = AppPreferences.areNotificationsEnabled(context)
                        showResetDialog = false
                    }
                ) {
                    Text("Réinitialiser", color = AppColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Annuler", color = AppColors.OnSurfaceVariant)
                }
            }
        )
    }
}

@Composable
fun DarkSettingsHeader() {
    GlassCard(accentColor = AppColors.Primary) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlassIconBadge(
                icon = Icons.Filled.Settings,
                accentColor = AppColors.Primary,
                size = 60.dp,
                iconSize = 30.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Personnalisez votre expérience",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Ajustez les paramètres selon votre niveau",
                fontSize = 13.sp,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun DarkSettingsCard(
    title: String,
    icon: ImageVector,
    description: String,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    GlassCard(accentColor = accentColor) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            GlassIconBadge(
                icon = icon,
                accentColor = accentColor,
                size = 40.dp,
                iconSize = 20.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }
        }

        content()
    }
}

@Composable
fun <T> DarkPresetSelector(
    label: String,
    value: T,
    options: List<T>,
    formatValue: (T) -> String,
    onValueChange: (T) -> Unit,
    accentColor: Color
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.OnSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == value

                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { onValueChange(option) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected)
                                accentColor.copy(alpha = 0.2f)
                            else
                                AppColors.GlassBgSubtle
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    1.dp,
                                    if (isSelected)
                                        accentColor.copy(alpha = 0.4f)
                                    else
                                        AppColors.GlassBorderLight,
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = formatValue(option),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) accentColor else AppColors.OnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DarkSummaryCard(
    accessDuration: Int,
    pushupCount: Int,
    waitingDuration: Int,
    breathingDuration: Int,
    quizCount: Int,
    mathCount: Int,
    puzzleCount: Int,
    meditationDuration: Int,
    dailyGoal: Int
) {
    GlassCard(accentColor = AppColors.Success) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = AppColors.Success,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Configuration actuelle",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DarkSummaryItem("Accès après défi", "$accessDuration min", AppColors.Primary)
            DarkSummaryItem("Pompes", "$pushupCount", AppColors.Success)
            DarkSummaryItem("Attente", PresetDurations.formatDuration(waitingDuration), AppColors.Warning)
            DarkSummaryItem("Respiration", PresetDurations.formatDuration(breathingDuration), AppColors.Info)
            DarkSummaryItem("Quiz", "$quizCount question${if (quizCount > 1) "s" else ""}", AppColors.Info)
            DarkSummaryItem("Math", "$mathCount calcul${if (mathCount > 1) "s" else ""}", AppColors.Success)
            DarkSummaryItem("Puzzles", "$puzzleCount", AppColors.Warning)
            DarkSummaryItem("Méditation", PresetDurations.formatDuration(meditationDuration), AppColors.Primary)
            DarkSummaryItem("Objectif quotidien", "$dailyGoal blocages", AppColors.Accent)
        }
    }
}

@Composable
fun DarkSummaryItem(label: String, value: String, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                AppColors.GlassBgSubtle,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                accentColor.copy(alpha = 0.15f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = AppColors.OnSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
    }
}
