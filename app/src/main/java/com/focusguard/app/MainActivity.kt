package com.focusguard.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusguard.app.components.*
import com.focusguard.app.ui.theme.FocusGuardTheme
import com.focusguard.app.utils.requestBatteryOptimizationExemption
import com.focusguard.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FocusGuardTheme {
                GlassBackground {
                    DarkHomeScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkHomeScreen(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val serviceRunning by viewModel.serviceRunning.collectAsStateWithLifecycle()
    val batteryOptimized by viewModel.batteryOptimized.collectAsStateWithLifecycle()
    val selectedApps by viewModel.selectedApps.collectAsStateWithLifecycle()
    val todayBlockCount by viewModel.todayBlockCount.collectAsStateWithLifecycle()
    val dailyGoal by viewModel.dailyGoal.collectAsStateWithLifecycle()
    val refreshTick by viewModel.refreshTick.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cleanupInvalidPreferences()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = AppColors.BackgroundGlass.copy(alpha = 0f),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlassIconBadge(
                            icon = Icons.Filled.Lock,
                            accentColor = AppColors.Primary,
                            size = 32.dp,
                            iconSize = 18.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "FocusGuard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = AppColors.OnSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Paramètres",
                            tint = AppColors.Primary
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

            item { DarkHeroCard() }

            item { DarkGamificationCard(refreshTick) }

            item { DailyGoalCard(todayBlocks = todayBlockCount, dailyGoal = dailyGoal) }

            item {
                DarkAppsCard(
                    selectedApps = selectedApps,
                    onConfigureClick = {
                        context.startActivity(Intent(context, AppSettingsActivity::class.java))
                    },
                    onStatsClick = {
                        context.startActivity(Intent(context, StatisticsActivity::class.java))
                    }
                )
            }

            item {
                SmartPlanningCard(
                    onOpenPlanning = {
                        context.startActivity(Intent(context, SmartPlanningActivity::class.java))
                    }
                )
            }

            item {
                DarkControlCard(
                    selectedApps = selectedApps,
                    serviceRunning = serviceRunning,
                    onServiceToggle = { shouldStart ->
                        if (shouldStart) {
                            if (!viewModel.startService()) {
                                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                                context.startActivity(intent)
                            }
                        } else {
                            viewModel.stopService()
                        }
                    },
                    onConfigureApps = {
                        context.startActivity(Intent(context, AppSettingsActivity::class.java))
                    }
                )
            }

            if (batteryOptimized && serviceRunning) {
                item {
                    DarkWarningCard(
                        onRequestExemption = {
                            requestBatteryOptimizationExemption(context)
                        }
                    )
                }
            }

            item {
                DarkPermissionsCard(context)
            }

            if (serviceRunning) {
                item {
                    DarkStatusCard(selectedApps.size)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
