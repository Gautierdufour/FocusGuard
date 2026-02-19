package com.focusguard.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusguard.app.components.*
import com.focusguard.app.data.StatsRepository
import com.focusguard.app.ui.theme.FocusGuardTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AppStats(
    val packageName: String,
    val displayName: String,
    val icon: ImageVector,
    val blockedCount: Int,
    val totalTimeBlocked: Long,
    val addictionScore: Int = 0,
    val averageTimePerBlock: Float = 0f
)

data class ChallengeStats(
    val type: String,
    val displayName: String,
    val icon: ImageVector,
    val completedCount: Int
)

data class DailyStats(
    val date: String,
    val displayDate: String,
    val blocksCount: Int
)

class StatisticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusGuardTheme {
                GlassBackground {
                    DarkStatisticsScreen(onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkStatisticsScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val statsRepository = remember { StatsRepository.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    var totalBlocks by remember { mutableStateOf(0) }
    var totalTimeSaved by remember { mutableStateOf(0) }
    var appStats by remember { mutableStateOf<List<AppStats>>(emptyList()) }
    var challengeStats by remember { mutableStateOf<List<ChallengeStats>>(emptyList()) }
    var weeklyStats by remember { mutableStateOf<List<DailyStats>>(emptyList()) }

    fun loadStats() {
        coroutineScope.launch {
            try {
                totalBlocks = statsRepository.getTotalBlocks()
                totalTimeSaved = statsRepository.getTotalTimeSaved()
                appStats = loadAppStatsFromRoom(statsRepository)
                challengeStats = loadChallengeStatsFromRoom(statsRepository)
                weeklyStats = loadWeeklyStatsFromRoom(statsRepository)
            } catch (e: Exception) {
                android.util.Log.e("Statistics", "Erreur lors du chargement des statistiques", e)
                totalBlocks = 0
                totalTimeSaved = 0
                appStats = emptyList()
                challengeStats = emptyList()
                weeklyStats = emptyList()
            }
        }
    }

    LaunchedEffect(Unit) {
        loadStats()
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlassIconBadge(
                            icon = Icons.Filled.Info,
                            accentColor = AppColors.Primary,
                            size = 32.dp,
                            iconSize = 18.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Statistiques",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = AppColors.OnSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                statsRepository.resetAllStats()
                                loadStats()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Réinitialiser",
                            tint = AppColors.Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.GlassBgSubtle,
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

            item { DarkStatsHeader() }

            item { StatsGamificationCard() }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DarkStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Blocages",
                        value = totalBlocks.toString(),
                        icon = Icons.Filled.Clear,
                        color = AppColors.Primary
                    )

                    DarkStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Temps économisé",
                        value = "${totalTimeSaved}min",
                        icon = Icons.Filled.Star,
                        color = AppColors.Success
                    )
                }
            }

            if (appStats.isNotEmpty()) {
                item {
                    DarkStatsCard(
                        title = "Applications les plus bloquées",
                        icon = Icons.Filled.Phone
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            appStats.take(5).forEach { app ->
                                DarkAppStatItem(app)
                            }
                        }
                    }
                }

                item {
                    DarkAddictionScoreChart(appStats)
                }

                item {
                    DarkAverageTimeCard(appStats)
                }
            }

            if (challengeStats.isNotEmpty()) {
                item {
                    DarkStatsCard(
                        title = "Défis complétés",
                        icon = Icons.Filled.Star
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            challengeStats.forEach { challenge ->
                                DarkChallengeStatItem(
                                    challenge = challenge,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            if (weeklyStats.isNotEmpty()) {
                item {
                    DarkWeeklyGraphCard(weeklyStats)
                }
            }

            item {
                DarkMotivationCard(totalBlocks, totalTimeSaved)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun DarkStatsHeader() {
    GlassCard(accentColor = AppColors.Primary) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlassIconBadge(
                icon = Icons.Filled.Info,
                accentColor = AppColors.Primary,
                size = 60.dp,
                iconSize = 30.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Votre progression",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Analyse de votre usage conscient",
                fontSize = 13.sp,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DarkStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    GlassCard(
        modifier = modifier,
        accentColor = color,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlassIconBadge(
                icon = icon,
                accentColor = color,
                size = 50.dp,
                iconSize = 24.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DarkStatsCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    GlassCard(accentColor = AppColors.Primary) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            GlassIconBadge(
                icon = icon,
                accentColor = AppColors.Primary,
                size = 40.dp,
                iconSize = 20.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
        }

        content()
    }
}

@Composable
fun DarkAppStatItem(app: AppStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.GlassBgSubtle)
            .border(
                width = 1.dp,
                color = AppColors.GlassBorderLight,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassIconBadge(
            icon = app.icon,
            accentColor = AppColors.Primary,
            size = 36.dp,
            iconSize = 18.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.OnSurface
            )
            Text(
                text = "${app.totalTimeBlocked}min économisées",
                fontSize = 12.sp,
                color = AppColors.OnSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(AppColors.GlassBgElevated, CircleShape)
                .border(1.dp, AppColors.Primary.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = app.blockedCount.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary
            )
        }
    }
}

@Composable
fun DarkAddictionScoreChart(appStats: List<AppStats>) {
    if (appStats.isEmpty()) return

    GlassCard(accentColor = AppColors.Error) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            GlassIconBadge(
                icon = Icons.Filled.Warning,
                accentColor = AppColors.Error,
                size = 40.dp,
                iconSize = 20.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Degré d'addictivité",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
        }

        Text(
            text = "Score basé sur la fréquence et la durée des blocages",
            fontSize = 12.sp,
            color = AppColors.OnSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        appStats.take(5).forEach { app ->
            DarkAddictionScoreBar(app)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DarkAddictionScoreBar(app: AppStats) {
    val scoreColor = when {
        app.addictionScore >= 80 -> AppColors.Error
        app.addictionScore >= 60 -> AppColors.Warning
        app.addictionScore >= 40 -> AppColors.Primary
        else -> AppColors.Success
    }

    val scoreLabel = when {
        app.addictionScore >= 80 -> "Très élevé"
        app.addictionScore >= 60 -> "Élevé"
        app.addictionScore >= 40 -> "Modéré"
        else -> "Faible"
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassIconBadge(
                    icon = app.icon,
                    accentColor = scoreColor,
                    size = 28.dp,
                    iconSize = 14.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = app.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.OnSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = scoreLabel,
                    fontSize = 11.sp,
                    color = scoreColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${app.addictionScore}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        GlassProgressBar(
            progress = app.addictionScore / 100f,
            accentColor = scoreColor,
            height = 10.dp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${app.blockedCount} blocages • ${app.totalTimeBlocked}min économisées",
            fontSize = 11.sp,
            color = AppColors.OnSurfaceVariant
        )
    }
}

@Composable
fun DarkAverageTimeCard(appStats: List<AppStats>) {
    if (appStats.isEmpty()) return

    val totalBlocks = appStats.sumOf { it.blockedCount }
    val totalTime = appStats.sumOf { it.totalTimeBlocked }
    val overallAverage = if (totalBlocks > 0) totalTime.toFloat() / totalBlocks else 0f

    GlassCard(accentColor = AppColors.Info) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            GlassIconBadge(
                icon = Icons.Filled.Info,
                accentColor = AppColors.Info,
                size = 40.dp,
                iconSize = 20.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Temps moyen par blocage",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.GlassBgSubtle)
                .border(
                    width = 1.dp,
                    color = AppColors.GlassBorderLight,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Moyenne générale",
                        fontSize = 12.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                    Text(
                        text = String.format("%.1f min", overallAverage),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Info
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total",
                        fontSize = 11.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                    Text(
                        text = "$totalBlocks",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                    Text(
                        text = "blocages",
                        fontSize = 11.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Par application",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        appStats.take(5).forEach { app ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.GlassBgSubtle)
                    .border(
                        width = 1.dp,
                        color = AppColors.GlassBorderLight,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    GlassIconBadge(
                        icon = app.icon,
                        accentColor = AppColors.Primary,
                        size = 24.dp,
                        iconSize = 12.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = app.displayName,
                        fontSize = 13.sp,
                        color = AppColors.OnSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format("%.1f min", app.averageTimePerBlock),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                    Text(
                        text = "${app.blockedCount} fois",
                        fontSize = 10.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                }
            }

            if (app != appStats.take(5).last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DarkWeeklyGraphCard(weeklyStats: List<DailyStats>) {
    val maxBlocks = weeklyStats.maxOfOrNull { it.blocksCount } ?: 1

    GlassCard(accentColor = AppColors.Success) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            GlassIconBadge(
                icon = Icons.Filled.Info,
                accentColor = AppColors.Success,
                size = 40.dp,
                iconSize = 20.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Activité des 7 derniers jours",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
        }

        // Zone des barres : hauteur fixe avec zone de barres isolée du texte
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weeklyStats.forEach { day ->
                val barHeightFraction = if (maxBlocks > 0) {
                    (day.blocksCount.toFloat() / maxBlocks.toFloat()).coerceIn(0f, 1f)
                } else 0f
                val maxBarDp = 90.dp

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Texte du compteur (toujours présent pour réserver l'espace)
                    Text(
                        text = if (day.blocksCount > 0) day.blocksCount.toString() else "",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Success,
                        modifier = Modifier.height(16.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Zone barre : hauteur fixe, barre alignée en bas
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(maxBarDp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        val barH = (barHeightFraction * maxBarDp.value).coerceAtLeast(
                            if (day.blocksCount > 0) 6f else 0f
                        ).dp
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(barH)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (day.blocksCount > 0)
                                        AppColors.Success.copy(alpha = 0.25f)
                                    else
                                        AppColors.GlassBgSubtle
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (day.blocksCount > 0)
                                        AppColors.Success.copy(alpha = 0.4f)
                                    else
                                        AppColors.GlassBorderLight,
                                    shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Libellé du jour
                    Text(
                        text = day.displayDate,
                        fontSize = 10.sp,
                        color = if (day.blocksCount > 0)
                            AppColors.OnSurface
                        else
                            AppColors.OnSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = weeklyStats.sumOf { it.blocksCount }.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
                Text(
                    text = "Total",
                    fontSize = 12.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val avg = weeklyStats.sumOf { it.blocksCount } / 7f
                Text(
                    text = String.format("%.1f", avg),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Success
                )
                Text(
                    text = "Moyenne/jour",
                    fontSize = 12.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val activesDays = weeklyStats.count { it.blocksCount > 0 }
                Text(
                    text = activesDays.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Info
                )
                Text(
                    text = "Jours actifs",
                    fontSize = 12.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatsGamificationCard() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val level = GamificationManager.getCurrentLevel(context)
    val xp = GamificationManager.getTotalXP(context)
    val xpProgress = GamificationManager.getXPProgress(context)
    val xpToNext = GamificationManager.getXPToNextLevel(context)
    val currentStreak = GamificationManager.getCurrentStreak(context)
    @Suppress("UNUSED_VARIABLE")
    val bestStreak = GamificationManager.getBestStreak(context)
    val unlockedBadges = GamificationManager.getUnlockedBadgesList(context)
    val lockedBadges = GamificationManager.getLockedBadgesList(context)
    val badgeProgress = GamificationManager.getBadgeProgress(context)
    val levelTitle = GamificationManager.getLevelTitle(level)
    val levelColor = GamificationManager.getLevelColor(level)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Carte niveau + XP
        GlassCard(accentColor = levelColor) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                levelColor.copy(alpha = 0.15f),
                                CircleShape
                            )
                            .border(2.dp, levelColor.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = level.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = levelColor
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = levelTitle,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = levelColor
                        )
                        Text(
                            text = "Niveau $level",
                            fontSize = 12.sp,
                            color = AppColors.OnSurfaceVariant
                        )
                    }
                }

                if (currentStreak > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                AppColors.Error.copy(alpha = 0.12f),
                                RoundedCornerShape(8.dp)
                            )
                            .border(1.dp, AppColors.Error.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "streak",
                            fontSize = 10.sp,
                            color = AppColors.Error
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentStreak.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$xp XP",
                        fontSize = 12.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                    Text(
                        text = "$xpToNext XP restants",
                        fontSize = 12.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                GlassProgressBar(
                    progress = xpProgress,
                    accentColor = levelColor,
                    height = 10.dp
                )
            }
        }

        // Carte badges
        GlassCard(accentColor = AppColors.Warning) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlassIconBadge(
                        icon = Icons.Filled.Star,
                        accentColor = AppColors.Warning,
                        size = 32.dp,
                        iconSize = 16.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Badges",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                }
                Text(
                    text = "${unlockedBadges.size}/${GamificationManager.ALL_BADGES.size}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Warning
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            GlassProgressBar(
                progress = badgeProgress,
                accentColor = AppColors.Warning,
                height = 8.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (unlockedBadges.isNotEmpty()) {
                Text(
                    text = "Débloqués",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(unlockedBadges.size) { index ->
                        DarkBadgeItem(unlockedBadges[index], isUnlocked = true)
                    }
                }
            }

            if (lockedBadges.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Prochains à débloquer",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lockedBadges.take(5).size) { index ->
                        DarkBadgeItem(lockedBadges[index], isUnlocked = false)
                    }
                }
            }
        }
    }
}

@Composable
fun DarkBadgeItem(badge: GamificationManager.Badge, isUnlocked: Boolean) {
    val alpha = if (isUnlocked) 1f else 0.4f

    Box(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isUnlocked)
                    badge.color.copy(alpha = 0.10f)
                else
                    AppColors.GlassBgSubtle
            )
            .border(
                width = 1.dp,
                color = if (isUnlocked) badge.color.copy(alpha = 0.25f) else AppColors.GlassBorderLight,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = badge.icon,
                fontSize = 32.sp,
                modifier = Modifier.alpha(alpha)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = badge.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) badge.color else AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha)
            )
            Text(
                text = badge.description,
                fontSize = 9.sp,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 11.sp,
                modifier = Modifier.alpha(alpha)
            )
        }
    }
}

@Composable
fun DarkChallengeStatItem(
    challenge: ChallengeStats,
    modifier: Modifier = Modifier
) {
    val challengeColor = when (challenge.type) {
        "breathing" -> AppColors.Info
        "pushups" -> AppColors.Success
        "waiting" -> AppColors.Warning
        else -> AppColors.Primary
    }

    GlassCard(
        modifier = modifier,
        accentColor = challengeColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlassIconBadge(
                icon = challenge.icon,
                accentColor = challengeColor,
                size = 40.dp,
                iconSize = 20.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = challenge.completedCount.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = challengeColor
            )

            Text(
                text = challenge.displayName,
                fontSize = 11.sp,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DarkMotivationCard(totalBlocks: Int, totalTimeSaved: Int) {
    val (message, icon, color) = when {
        totalBlocks == 0 -> Triple(
            "Commencez votre parcours !",
            Icons.Filled.PlayArrow,
            AppColors.Primary
        )
        totalBlocks < 10 -> Triple(
            "Premiers pas accomplis !",
            Icons.Filled.Favorite,
            AppColors.Success
        )
        totalBlocks < 50 -> Triple(
            "Vous prenez le contrôle !",
            Icons.Filled.Settings,
            AppColors.Primary
        )
        totalTimeSaved < 60 -> Triple(
            "Plus d'une heure économisée !",
            Icons.Filled.Star,
            AppColors.Warning
        )
        else -> Triple(
            "Maître du temps d'écran !",
            Icons.Filled.Star,
            AppColors.Success
        )
    }

    GlassCard(accentColor = color) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconBadge(
                icon = icon,
                accentColor = color,
                size = 50.dp,
                iconSize = 26.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = message,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Fonctions de chargement des statistiques depuis Room
private suspend fun loadAppStatsFromRoom(repo: StatsRepository): List<AppStats> {
    return try {
        val topApps = repo.getTopBlockedApps(10)
        topApps.map { app ->
            val blockedCount = app.blockCount
            val timeBlocked = app.totalTime ?: 0L
            val avgTime = if (blockedCount > 0) timeBlocked.toFloat() / blockedCount else 0f
            val addictionScore = calculateAddictionScore(blockedCount, timeBlocked)

            AppStats(
                packageName = app.packageName,
                displayName = app.appName,
                icon = getIconForApp(app.packageName),
                blockedCount = blockedCount,
                totalTimeBlocked = timeBlocked,
                addictionScore = addictionScore,
                averageTimePerBlock = avgTime
            )
        }.sortedByDescending { it.addictionScore }
    } catch (e: Exception) {
        android.util.Log.e("Statistics", "Erreur chargement stats apps", e)
        emptyList()
    }
}

private fun getIconForApp(packageName: String): ImageVector {
    return when {
        packageName.contains("instagram") -> Icons.Filled.Star
        packageName.contains("youtube") -> Icons.Filled.PlayArrow
        packageName.contains("musically") || packageName.contains("tiktok") -> Icons.Filled.Phone
        packageName.contains("facebook") -> Icons.Filled.Person
        packageName.contains("snapchat") -> Icons.Filled.Star
        packageName.contains("twitter") -> Icons.Filled.Email
        else -> Icons.Filled.Phone
    }
}

private fun calculateAddictionScore(blockedCount: Int, timeBlocked: Long): Int {
    val frequencyScore = (blockedCount * 2).coerceAtMost(70)
    val timeScore = (timeBlocked / 10).toInt().coerceAtMost(30)
    return (frequencyScore + timeScore).coerceIn(0, 100)
}

private suspend fun loadChallengeStatsFromRoom(repo: StatsRepository): List<ChallengeStats> {
    return try {
        val counts = repo.getChallengeCounts()
        counts.map { count ->
            val (displayName, icon) = when (count.challengeType) {
                "breathing" -> "Respiration" to Icons.Filled.Favorite
                "pushups" -> "Sport" to Icons.Filled.Star
                "waiting" -> "Patience" to Icons.Filled.Info
                "quiz" -> "Quiz" to Icons.Filled.Star
                "math" -> "Maths" to Icons.Filled.Star
                "puzzle" -> "Puzzle" to Icons.Filled.Star
                "meditation" -> "Méditation" to Icons.Filled.Favorite
                else -> count.challengeType to Icons.Filled.Star
            }
            ChallengeStats(count.challengeType, displayName, icon, count.count)
        }.filter { it.completedCount > 0 }
    } catch (e: Exception) {
        android.util.Log.e("Statistics", "Erreur chargement stats challenges", e)
        emptyList()
    }
}

private suspend fun loadWeeklyStatsFromRoom(repo: StatsRepository): List<DailyStats> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()

    return try {
        val startCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6) }
        val startDate = dateFormat.format(startCal.time)
        val endDate = dateFormat.format(Date())
        val dailyCounts = repo.getDailyBlockCountsRange(startDate, endDate)
        val countsMap = dailyCounts.associate { it.date to it.blockCount }

        (6 downTo 0).map { daysAgo ->
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

            val dateKey = dateFormat.format(calendar.time)
            val displayDate = when (daysAgo) {
                0 -> "Auj"
                1 -> "Hier"
                else -> {
                    val dayName = calendar.getDisplayName(
                        Calendar.DAY_OF_WEEK,
                        Calendar.SHORT,
                        Locale.FRENCH
                    )
                    dayName ?: "J-$daysAgo"
                }
            }

            DailyStats(
                date = dateKey,
                displayDate = displayDate,
                blocksCount = countsMap[dateKey] ?: 0
            )
        }
    } catch (e: Exception) {
        android.util.Log.e("Statistics", "Erreur chargement stats hebdo", e)
        emptyList()
    }
}
