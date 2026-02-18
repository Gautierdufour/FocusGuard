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
import androidx.compose.ui.res.stringResource
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
    val resources = context.resources

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
                        text = stringResource(R.string.settings),
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = AppColors.Primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.reset),
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
                    title = stringResource(R.string.access_duration_after_challenge),
                    icon = Icons.Filled.Star,
                    description = stringResource(R.string.access_time_once_challenge_completed),
                    accentColor = AppColors.Primary
                ) {
                    DarkPresetSelector(
                        label = stringResource(R.string.duration_label),
                        value = accessDuration,
                        options = PresetDurations.ACCESS_DURATIONS,
                        formatValue = { "${it} ${resources.getString(R.string.min_abbr)}" },
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
                    title = stringResource(R.string.challenge_configuration),
                    icon = Icons.Filled.Settings,
                    description = stringResource(R.string.customize_difficulty),
                    accentColor = AppColors.Secondary
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        DarkPresetSelector(
                            label = stringResource(R.string.pushups_required),
                            value = pushupCount,
                            options = PresetDurations.PUSHUP_COUNTS,
                            formatValue = { resources.getQuantityString(R.plurals.pushup_count, it, it) },
                            onValueChange = {
                                pushupCount = it
                                AppPreferences.setPushupCount(context, it)
                            },
                            accentColor = AppColors.Success
                        )

                        DarkPresetSelector(
                            label = stringResource(R.string.waiting_duration),
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
                            label = stringResource(R.string.breathing_duration),
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
                    title = stringResource(R.string.cognitive_challenges),
                    icon = Icons.Filled.Star,
                    description = stringResource(R.string.configure_mental_challenges),
                    accentColor = AppColors.Info
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        DarkPresetSelector(
                            label = stringResource(R.string.quiz_questions),
                            value = quizCount,
                            options = PresetDurations.QUIZ_COUNTS,
                            formatValue = { resources.getQuantityString(R.plurals.question_count, it, it) },
                            onValueChange = {
                                quizCount = it
                                AppPreferences.setQuizCount(context, it)
                            },
                            accentColor = AppColors.Info
                        )

                        DarkPresetSelector(
                            label = stringResource(R.string.math_calculations),
                            value = mathCount,
                            options = PresetDurations.MATH_COUNTS,
                            formatValue = { resources.getQuantityString(R.plurals.calc_count, it, it) },
                            onValueChange = {
                                mathCount = it
                                AppPreferences.setMathCount(context, it)
                            },
                            accentColor = AppColors.Success
                        )

                        DarkPresetSelector(
                            label = stringResource(R.string.logic_puzzles),
                            value = puzzleCount,
                            options = PresetDurations.PUZZLE_COUNTS,
                            formatValue = { resources.getQuantityString(R.plurals.puzzle_count, it, it) },
                            onValueChange = {
                                puzzleCount = it
                                AppPreferences.setPuzzleCount(context, it)
                            },
                            accentColor = AppColors.Warning
                        )

                        DarkPresetSelector(
                            label = stringResource(R.string.meditation_duration),
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
                val blocksPerDayStr = stringResource(R.string.blocks_per_day)
                DarkSettingsCard(
                    title = stringResource(R.string.daily_goals),
                    icon = Icons.Filled.Favorite,
                    description = stringResource(R.string.set_daily_goal),
                    accentColor = AppColors.Accent
                ) {
                    DarkPresetSelector(
                        label = stringResource(R.string.daily_goal),
                        value = dailyGoal,
                        options = PresetDurations.DAILY_GOALS,
                        formatValue = { "$it $blocksPerDayStr" },
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
                    title = stringResource(R.string.notifications),
                    icon = Icons.Filled.Info,
                    description = stringResource(R.string.daily_reports_and_encouragement),
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
                            text = stringResource(R.string.notifications_enabled),
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
                    stringResource(R.string.reset_settings_title),
                    color = AppColors.OnSurface
                )
            },
            text = {
                Text(
                    stringResource(R.string.reset_settings_message),
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
                    Text(stringResource(R.string.reset), color = AppColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.cancel), color = AppColors.OnSurfaceVariant)
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
                text = stringResource(R.string.customize_your_experience),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.adjust_settings_to_level),
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
    val resources = androidx.compose.ui.platform.LocalContext.current.resources
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
                text = stringResource(R.string.current_configuration),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DarkSummaryItem(stringResource(R.string.access_after_challenge), "$accessDuration ${resources.getString(R.string.min_abbr)}", AppColors.Primary)
            DarkSummaryItem(stringResource(R.string.pushup_label), resources.getQuantityString(R.plurals.pushup_count, pushupCount, pushupCount), AppColors.Success)
            DarkSummaryItem(stringResource(R.string.waiting), PresetDurations.formatDuration(waitingDuration), AppColors.Warning)
            DarkSummaryItem(stringResource(R.string.breathing), PresetDurations.formatDuration(breathingDuration), AppColors.Info)
            DarkSummaryItem(stringResource(R.string.quiz_label), resources.getQuantityString(R.plurals.question_count, quizCount, quizCount), AppColors.Info)
            DarkSummaryItem(stringResource(R.string.math_label), resources.getQuantityString(R.plurals.calc_count, mathCount, mathCount), AppColors.Success)
            DarkSummaryItem(stringResource(R.string.puzzles), resources.getQuantityString(R.plurals.puzzle_count, puzzleCount, puzzleCount), AppColors.Warning)
            DarkSummaryItem(stringResource(R.string.meditation), PresetDurations.formatDuration(meditationDuration), AppColors.Primary)
            DarkSummaryItem(stringResource(R.string.daily_goal), "$dailyGoal ${resources.getString(R.string.block_suffix)}", AppColors.Accent)
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
