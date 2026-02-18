package com.focusguard.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.focusguard.app.components.*
import com.focusguard.app.ui.theme.FocusGuardTheme
import java.text.SimpleDateFormat
import java.util.*

class SmartPlanningActivity : ComponentActivity() {
    private var permissionRefreshKey by mutableStateOf(0)

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (!fineGranted && !coarseGranted) {
            android.widget.Toast.makeText(
                this,
                getString(R.string.location_permission_toast),
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
        permissionRefreshKey++
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusGuardTheme {
                GlassBackground {
                    SmartPlanningScreen(
                        onBack = { finish() },
                        permissionRefreshKey = permissionRefreshKey,
                        onRequestLocationPermission = {
                            locationPermissionRequest.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

data class TimeSchedule(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val days: Int, // Bitmap des jours
    val enabled: Boolean = true
)

data class LocationZone(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Int, // en metres
    val blockingEnabled: Boolean = true
)

data class FocusSession(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val duration: Int, // en minutes
    val startTime: Long = 0L,
    val isActive: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartPlanningScreen(
    onBack: () -> Unit,
    permissionRefreshKey: Int = 0,
    onRequestLocationPermission: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        Triple(stringResource(R.string.schedules_tab), Icons.Filled.DateRange, AppColors.Primary),
        Triple(stringResource(R.string.focus_tab), Icons.Filled.Favorite, AppColors.Error),
        Triple(stringResource(R.string.break_tab), Icons.Filled.Settings, AppColors.Warning),
        Triple(stringResource(R.string.locations_tab), Icons.Filled.Place, AppColors.Info)
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlassIconBadge(
                            icon = Icons.Filled.Star,
                            accentColor = AppColors.Primary,
                            size = 32.dp,
                            iconSize = 18.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.smart_planning_title),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = AppColors.OnSurface
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Onglets personnalises
            SmartPlanningTabs(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // Contenu selon l'onglet selectionne
            when (selectedTab) {
                0 -> SchedulesTab(context)
                1 -> FocusTab(context)
                2 -> PauseTab(context)
                3 -> LocationTab(context, permissionRefreshKey, onRequestLocationPermission)
            }
        }
    }
}

@Composable
fun SmartPlanningTabs(
    tabs: List<Triple<String, ImageVector, Color>>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = AppColors.OnSurface,
        indicator = {},
        divider = {},
        edgePadding = 16.dp
    ) {
        tabs.forEachIndexed { index, (title, icon, color) ->
            val isSelected = selectedTab == index

            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) color.copy(alpha = 0.2f)
                        else AppColors.GlassBgSubtle
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) color.copy(alpha = 0.4f)
                        else AppColors.GlassBorderLight,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) color else AppColors.OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) color else AppColors.OnSurfaceVariant
                    )
                }
            }
        }
    }
}

// ==================== ONGLET HORAIRES ====================

@Composable
fun SchedulesTab(context: Context) {
    var schedules by remember { mutableStateOf(loadSchedules(context)) }
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SchedulesHeaderCard()
        }

        if (schedules.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Filled.DateRange,
                    title = stringResource(R.string.no_schedule_defined),
                    description = stringResource(R.string.create_time_slots),
                    color = AppColors.Primary
                )
            }
        } else {
            items(schedules) { schedule ->
                ScheduleCard(
                    schedule = schedule,
                    onToggle = { enabled ->
                        val updated = schedule.copy(enabled = enabled)
                        schedules = schedules.map { if (it.id == schedule.id) updated else it }
                        saveSchedules(context, schedules)
                    },
                    onDelete = {
                        schedules = schedules.filter { it.id != schedule.id }
                        saveSchedules(context, schedules)
                    }
                )
            }
        }

        item {
            GlassButton(
                onClick = { showAddDialog = true },
                accentColor = AppColors.Primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    stringResource(R.string.add_schedule),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    if (showAddDialog) {
        AddScheduleDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newSchedule ->
                schedules = schedules + newSchedule
                saveSchedules(context, schedules)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SchedulesHeaderCard() {
    GlassCard(accentColor = AppColors.Primary) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlassIconBadge(
                icon = Icons.Filled.DateRange,
                accentColor = AppColors.Primary,
                size = 40.dp,
                iconSize = 22.dp
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.custom_schedules),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )
                Text(
                    text = stringResource(R.string.block_apps_by_schedule),
                    fontSize = 13.sp,
                    color = AppColors.OnSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun ScheduleCard(
    schedule: TimeSchedule,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val isCurrentlyActive = isScheduleActive(schedule)
    val accentColor = if (isCurrentlyActive && schedule.enabled) AppColors.Success else AppColors.OnSurfaceVariant

    GlassCard(accentColor = accentColor) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isCurrentlyActive && schedule.enabled)
                        Icons.Filled.CheckCircle
                    else
                        Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = schedule.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = formatTimeRange(schedule.startHour, schedule.startMinute,
                                              schedule.endHour, schedule.endMinute),
                        fontSize = 13.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                }
            }

            Switch(
                checked = schedule.enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = AppColors.Success,
                    uncheckedThumbColor = AppColors.OnSurfaceVariant,
                    uncheckedTrackColor = AppColors.SurfaceVariant
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DaysDisplay(schedule.days)

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = AppColors.Error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DaysDisplay(daysBitmap: Int) {
    val days = listOf("L", "M", "M", "J", "V", "S", "D")
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        days.forEachIndexed { index, day ->
            val isEnabled = (daysBitmap and (1 shl index)) != 0
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        if (isEnabled) AppColors.Primary.copy(alpha = 0.2f) else AppColors.GlassBgSubtle,
                        CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = if (isEnabled) AppColors.Primary.copy(alpha = 0.4f) else AppColors.GlassBorderLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    fontSize = 11.sp,
                    fontWeight = if (isEnabled) FontWeight.Bold else FontWeight.Normal,
                    color = if (isEnabled) AppColors.Primary else AppColors.OnSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onAdd: (TimeSchedule) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf(9) }
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(18) }
    var endMinute by remember { mutableStateOf(0) }
    var selectedDays by remember { mutableStateOf(0b1111100) } // Lun-Ven par defaut

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppColors.GlassBgElevated,
        title = {
            Text(stringResource(R.string.new_schedule), color = AppColors.OnSurface, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name_label)) },
                    placeholder = { Text(stringResource(R.string.eg_work_studies)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Primary,
                        focusedLabelColor = AppColors.Primary,
                        cursorColor = AppColors.Primary,
                        focusedContainerColor = AppColors.GlassBg,
                        unfocusedContainerColor = AppColors.GlassBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(stringResource(R.string.start_time), fontWeight = FontWeight.Bold, color = AppColors.OnSurface)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TimePickerField(
                        value = startHour,
                        onValueChange = { startHour = it.coerceIn(0, 23) },
                        label = "H",
                        modifier = Modifier.weight(1f)
                    )
                    TimePickerField(
                        value = startMinute,
                        onValueChange = { startMinute = it.coerceIn(0, 59) },
                        label = "M",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(stringResource(R.string.end_time), fontWeight = FontWeight.Bold, color = AppColors.OnSurface)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TimePickerField(
                        value = endHour,
                        onValueChange = { endHour = it.coerceIn(0, 23) },
                        label = "H",
                        modifier = Modifier.weight(1f)
                    )
                    TimePickerField(
                        value = endMinute,
                        onValueChange = { endMinute = it.coerceIn(0, 59) },
                        label = "M",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(stringResource(R.string.days_label), fontWeight = FontWeight.Bold, color = AppColors.OnSurface)
                DaySelector(
                    selectedDays = selectedDays,
                    onDaysChanged = { selectedDays = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(
                            TimeSchedule(
                                name = name,
                                startHour = startHour,
                                startMinute = startMinute,
                                endHour = endHour,
                                endMinute = endMinute,
                                days = selectedDays
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary.copy(alpha = 0.2f),
                    contentColor = AppColors.Primary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(AppColors.Primary.copy(alpha = 0.4f), AppColors.Primary.copy(alpha = 0.2f))
                    )
                )
            ) {
                Text(stringResource(R.string.add), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = AppColors.OnSurface)
            }
        }
    )
}

@Composable
fun TimePickerField(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value.toString().padStart(2, '0'),
        onValueChange = {
            it.toIntOrNull()?.let { num -> onValueChange(num) }
        },
        label = { Text(label) },
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.Primary,
            focusedLabelColor = AppColors.Primary,
            cursorColor = AppColors.Primary,
            focusedContainerColor = AppColors.GlassBg,
            unfocusedContainerColor = AppColors.GlassBg
        ),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    )
}

@Composable
fun DaySelector(
    selectedDays: Int,
    onDaysChanged: (Int) -> Unit
) {
    val days = listOf(
        stringResource(R.string.mon),
        stringResource(R.string.tue),
        stringResource(R.string.wed),
        stringResource(R.string.thu),
        stringResource(R.string.fri),
        stringResource(R.string.sat),
        stringResource(R.string.sun)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEachIndexed { index, day ->
            val isSelected = (selectedDays and (1 shl index)) != 0
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected)
                            AppColors.Primary.copy(alpha = 0.2f)
                        else
                            AppColors.GlassBgSubtle
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) AppColors.Primary.copy(alpha = 0.4f)
                        else AppColors.GlassBorderLight,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        onDaysChanged(selectedDays xor (1 shl index))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) AppColors.Primary else AppColors.OnSurfaceVariant
                )
            }
        }
    }
}

// ==================== ONGLET FOCUS ====================

@Composable
fun FocusTab(context: Context) {
    var currentSession by remember { mutableStateOf(loadCurrentFocusSession(context)) }
    var showStartDialog by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableStateOf(0) }

    LaunchedEffect(currentSession) {
        if (currentSession?.isActive == true) {
            while (remainingTime > 0) {
                kotlinx.coroutines.delay(1000)
                val elapsed = (System.currentTimeMillis() - (currentSession?.startTime ?: 0)) / 1000
                val total = (currentSession?.duration ?: 0) * 60
                remainingTime = (total - elapsed).toInt().coerceAtLeast(0)

                if (remainingTime == 0) {
                    endFocusSession(context)
                    currentSession = null
                }
            }
        } else {
            remainingTime = 0
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FocusHeaderCard()
        }

        if (currentSession?.isActive == true) {
            item {
                ActiveFocusCard(
                    session = currentSession!!,
                    remainingTime = remainingTime,
                    onCancel = {
                        endFocusSession(context)
                        currentSession = null
                    }
                )
            }
        } else {
            item {
                EmptyStateCard(
                    icon = Icons.Filled.Favorite,
                    title = stringResource(R.string.no_active_session),
                    description = stringResource(R.string.start_focus_session),
                    color = AppColors.Error
                )
            }

            item {
                FocusPresetsCard(
                    onStartSession = { duration, name ->
                        currentSession = startFocusSession(context, duration, name)
                        remainingTime = duration * 60
                    }
                )
            }

            item {
                GlassButton(
                    onClick = { showStartDialog = true },
                    accentColor = AppColors.Error
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        stringResource(R.string.custom_session),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    if (showStartDialog) {
        CustomFocusDialog(
            onDismiss = { showStartDialog = false },
            onStart = { duration, name ->
                currentSession = startFocusSession(context, duration, name)
                remainingTime = duration * 60
                showStartDialog = false
            }
        )
    }
}

@Composable
fun FocusHeaderCard() {
    val context = LocalContext.current
    val focusAppsCount = remember {
        val prefs = context.getSharedPreferences("app_blocker_settings", Context.MODE_PRIVATE)
        val apps = prefs.getStringSet("selected_apps_focus", null)
        if (apps == null) {
            // Si aucune liste focus, utiliser la liste globale
            val globalApps = prefs.getStringSet("selected_apps", emptySet()) ?: emptySet()
            globalApps.size
        } else {
            apps.size
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GlassCard(accentColor = AppColors.Error) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassIconBadge(
                    icon = Icons.Filled.Favorite,
                    accentColor = AppColors.Error,
                    size = 40.dp,
                    iconSize = 22.dp
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.focus_mode),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = stringResource(R.string.focus_mode_subtitle),
                        fontSize = 13.sp,
                        color = AppColors.OnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        ConfigureAppsButton(
            mode = "focus",
            appsCount = focusAppsCount,
            icon = Icons.Filled.Phone,
            label = stringResource(R.string.apps_to_block),
            description = stringResource(R.string.configure_focus_apps),
            color = AppColors.Error
        )
    }
}

@Composable
fun ActiveFocusCard(
    session: FocusSession,
    remainingTime: Int,
    onCancel: () -> Unit
) {
    val progress = remainingTime.toFloat() / (session.duration * 60)

    GlassCard(accentColor = AppColors.Error) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = session.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Error
            )

            Spacer(Modifier.height(24.dp))

            Box(contentAlignment = Alignment.Center) {
                // Cercle de fond
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            AppColors.GlassBgSubtle,
                            CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = AppColors.Error.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )

                // Texte du temps restant
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatTimeRemaining(remainingTime),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Error
                    )
                    Text(
                        text = stringResource(R.string.remaining),
                        fontSize = 14.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            GlassProgressBar(
                progress = progress,
                accentColor = AppColors.Error,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            GlassButton(
                onClick = onCancel,
                accentColor = AppColors.Error,
                isPrimary = true
            ) {
                Icon(Icons.Filled.Clear, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.stop_session), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FocusPresetsCard(
    onStartSession: (Int, String) -> Unit
) {
    GlassCard(accentColor = AppColors.Error) {
        Text(
            text = stringResource(R.string.quick_sessions),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface
        )

        Spacer(Modifier.height(16.dp))

        val presets = listOf(
            Triple(15, "Sprint", "⚡"),
            Triple(25, "Pomodoro", "\uD83C\uDF45"),
            Triple(45, "Etude", "\uD83D\uDCDA"),
            Triple(90, "Deep Work", "\uD83C\uDFAF")
        )

        presets.forEach { (duration, name, emoji) ->
            FocusPresetButton(
                emoji = emoji,
                name = name,
                duration = duration,
                onClick = { onStartSession(duration, name) }
            )
            if (presets.last() != Triple(duration, name, emoji)) {
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun FocusPresetButton(
    emoji: String,
    name: String,
    duration: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.GlassBgSubtle
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                colors = listOf(AppColors.GlassBorderLight, AppColors.GlassBorderLight)
            )
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = emoji,
                    fontSize = 28.sp
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = "$duration minutes",
                        fontSize = 12.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                }
            }

            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = AppColors.Error,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFocusDialog(
    onDismiss: () -> Unit,
    onStart: (Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(30) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppColors.GlassBgElevated,
        title = {
            Text(stringResource(R.string.new_focus_session), color = AppColors.OnSurface, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.session_name)) },
                    placeholder = { Text(stringResource(R.string.eg_revision_project)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Error,
                        focusedLabelColor = AppColors.Error,
                        cursorColor = AppColors.Error,
                        focusedContainerColor = AppColors.GlassBg,
                        unfocusedContainerColor = AppColors.GlassBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = duration.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { num ->
                            duration = num.coerceIn(5, 180)
                        }
                    },
                    label = { Text(stringResource(R.string.duration_minutes)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Error,
                        focusedLabelColor = AppColors.Error,
                        cursorColor = AppColors.Error,
                        focusedContainerColor = AppColors.GlassBg,
                        unfocusedContainerColor = AppColors.GlassBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onStart(duration, name)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Error.copy(alpha = 0.2f),
                    contentColor = AppColors.Error
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(AppColors.Error.copy(alpha = 0.4f), AppColors.Error.copy(alpha = 0.2f))
                    )
                )
            ) {
                Text(stringResource(R.string.start), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = AppColors.OnSurface)
            }
        }
    )
}

// ==================== ONGLET PAUSE ====================

@Composable
fun PauseTab(context: Context) {
    var pauseUntil by remember { mutableStateOf(AppPreferences.getPauseUntil(context)) }
    var isPaused by remember { mutableStateOf(AppPreferences.isPaused(context)) }
    var showPauseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            pauseUntil = AppPreferences.getPauseUntil(context)
            isPaused = AppPreferences.isPaused(context)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PauseHeaderCard()
        }

        if (isPaused) {
            item {
                ActivePauseCard(
                    pauseUntil = pauseUntil,
                    onCancel = {
                        AppPreferences.clearPause(context)
                        isPaused = false
                    }
                )
            }
        } else {
            item {
                EmptyStateCard(
                    icon = Icons.Filled.Settings,
                    title = stringResource(R.string.system_active),
                    description = stringResource(R.string.pause_monitoring_temporarily),
                    color = AppColors.Warning
                )
            }

            item {
                PausePresetsCard(
                    onPause = { minutes ->
                        val until = System.currentTimeMillis() + (minutes * 60 * 1000)
                        AppPreferences.setPauseUntil(context, until)
                        isPaused = true
                    }
                )
            }

            item {
                GlassButton(
                    onClick = { showPauseDialog = true },
                    accentColor = AppColors.Warning
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        stringResource(R.string.custom_pause),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    if (showPauseDialog) {
        CustomPauseDialog(
            onDismiss = { showPauseDialog = false },
            onPause = { minutes ->
                val until = System.currentTimeMillis() + (minutes * 60 * 1000)
                AppPreferences.setPauseUntil(context, until)
                isPaused = true
                showPauseDialog = false
            }
        )
    }
}

@Composable
fun PauseHeaderCard() {
    GlassCard(accentColor = AppColors.Warning) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlassIconBadge(
                icon = Icons.Filled.Settings,
                accentColor = AppColors.Warning,
                size = 40.dp,
                iconSize = 22.dp
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.temporary_break),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )
                Text(
                    text = stringResource(R.string.pause_monitoring),
                    fontSize = 13.sp,
                    color = AppColors.OnSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun ActivePauseCard(
    pauseUntil: Long,
    onCancel: () -> Unit
) {
    val remainingMs = pauseUntil - System.currentTimeMillis()
    val remainingMinutes = (remainingMs / 1000 / 60).toInt()

    GlassCard(accentColor = AppColors.Warning) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlassIconBadge(
                icon = Icons.Filled.Settings,
                accentColor = AppColors.Warning,
                size = 64.dp,
                iconSize = 32.dp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.system_paused),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Warning
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.ends_in_minutes, remainingMinutes),
                fontSize = 16.sp,
                color = AppColors.OnSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(pauseUntil)),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )

            Spacer(Modifier.height(24.dp))

            GlassButton(
                onClick = onCancel,
                accentColor = AppColors.Error
            ) {
                Icon(Icons.Filled.Clear, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.resume_now), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PausePresetsCard(
    onPause: (Int) -> Unit
) {
    GlassCard(accentColor = AppColors.Warning) {
        Text(
            text = stringResource(R.string.quick_durations),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface
        )

        Spacer(Modifier.height(16.dp))

        val presets = listOf(
            Pair(15, "⏱️ Courte"),
            Pair(30, "☕ Moyenne"),
            Pair(60, "\uD83C\uDF55 Une heure"),
            Pair(120, "\uD83C\uDF19 Deux heures")
        )

        presets.forEach { (duration, label) ->
            Button(
                onClick = { onPause(duration) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.GlassBgSubtle
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(AppColors.GlassBorderLight, AppColors.GlassBorderLight)
                    )
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = "$duration min",
                        fontSize = 14.sp,
                        color = AppColors.Warning,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (presets.last() != Pair(duration, label)) {
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPauseDialog(
    onDismiss: () -> Unit,
    onPause: (Int) -> Unit
) {
    var duration by remember { mutableStateOf(30) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppColors.GlassBgElevated,
        title = {
            Text(stringResource(R.string.custom_pause), color = AppColors.OnSurface, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = duration.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { num ->
                            duration = num.coerceIn(5, 480)
                        }
                    },
                    label = { Text(stringResource(R.string.duration_minutes)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Warning,
                        focusedLabelColor = AppColors.Warning,
                        cursorColor = AppColors.Warning,
                        focusedContainerColor = AppColors.GlassBg,
                        unfocusedContainerColor = AppColors.GlassBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.system_will_pause_for_minutes, duration),
                    fontSize = 13.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onPause(duration) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Warning.copy(alpha = 0.2f),
                    contentColor = AppColors.Warning
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(AppColors.Warning.copy(alpha = 0.4f), AppColors.Warning.copy(alpha = 0.2f))
                    )
                )
            ) {
                Text(stringResource(R.string.start), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = AppColors.OnSurface)
            }
        }
    )
}

// ==================== ONGLET LOCALISATION ====================

@Composable
fun LocationTab(
    context: Context,
    permissionRefreshKey: Int = 0,
    onRequestLocationPermission: () -> Unit
) {
    var zones by remember { mutableStateOf(loadLocationZones(context)) }
    var showAddDialog by remember { mutableStateOf(false) }
    val hasPermission = remember(permissionRefreshKey) { hasLocationPermission(context) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LocationHeaderCard()
        }

        if (!hasPermission) {
            item {
                PermissionRequiredCard(
                    onRequestPermission = onRequestLocationPermission
                )
            }
        } else {
            if (zones.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Filled.Place,
                        title = stringResource(R.string.no_location_defined),
                        description = stringResource(R.string.add_zones),
                        color = AppColors.Info
                    )
                }
            } else {
                items(zones) { zone ->
                    LocationZoneCard(
                        zone = zone,
                        onToggle = { enabled ->
                            val updated = zone.copy(blockingEnabled = enabled)
                            zones = zones.map { if (it.id == zone.id) updated else it }
                            saveLocationZones(context, zones)
                        },
                        onDelete = {
                            zones = zones.filter { it.id != zone.id }
                            saveLocationZones(context, zones)
                        }
                    )
                }
            }

            item {
                GlassButton(
                    onClick = { showAddDialog = true },
                    accentColor = AppColors.Info
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        stringResource(R.string.add_location),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    if (showAddDialog && hasPermission) {
        AddLocationDialog(
            context = context,
            onDismiss = { showAddDialog = false },
            onAdd = { newZone ->
                zones = zones + newZone
                saveLocationZones(context, zones)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun LocationHeaderCard() {
    val context = LocalContext.current
    val locationAppsCount = remember {
        val prefs = context.getSharedPreferences("app_blocker_settings", Context.MODE_PRIVATE)
        val apps = prefs.getStringSet("selected_apps_location", null)
        if (apps == null) {
            // Si aucune liste location, utiliser la liste globale
            val globalApps = prefs.getStringSet("selected_apps", emptySet()) ?: emptySet()
            globalApps.size
        } else {
            apps.size
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GlassCard(accentColor = AppColors.Info) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassIconBadge(
                    icon = Icons.Filled.Place,
                    accentColor = AppColors.Info,
                    size = 40.dp,
                    iconSize = 22.dp
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.geographic_blocking),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = stringResource(R.string.block_by_location),
                        fontSize = 13.sp,
                        color = AppColors.OnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        ConfigureAppsButton(
            mode = "location",
            appsCount = locationAppsCount,
            icon = Icons.Filled.Phone,
            label = stringResource(R.string.apps_to_block),
            description = stringResource(R.string.configure_zone_apps),
            color = AppColors.Info
        )
    }
}

@Composable
fun LocationZoneCard(
    zone: LocationZone,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val accentColor = if (zone.blockingEnabled) AppColors.Info else AppColors.OnSurfaceVariant

    GlassCard(accentColor = accentColor) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = null,
                    tint = if (zone.blockingEnabled) AppColors.Info else AppColors.OnSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = zone.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = stringResource(R.string.radius_value_label, zone.radius),
                        fontSize = 13.sp,
                        color = AppColors.OnSurfaceVariant
                    )
                }
            }

            Switch(
                checked = zone.blockingEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = AppColors.Info,
                    uncheckedThumbColor = AppColors.OnSurfaceVariant,
                    uncheckedTrackColor = AppColors.SurfaceVariant
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "\uD83D\uDCCD ${String.format("%.4f", zone.latitude)}, ${String.format("%.4f", zone.longitude)}",
                fontSize = 12.sp,
                color = AppColors.OnSurfaceVariant,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = AppColors.Error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun PermissionRequiredCard(
    onRequestPermission: () -> Unit
) {
    GlassCard(accentColor = AppColors.Warning) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GlassIconBadge(
                icon = Icons.Filled.Place,
                accentColor = AppColors.Warning,
                size = 64.dp,
                iconSize = 32.dp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.location_permission_required),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.allow_location_access),
                fontSize = 14.sp,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(20.dp))

            GlassButton(
                onClick = onRequestPermission,
                accentColor = AppColors.Warning
            ) {
                Icon(Icons.Filled.Settings, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.authorize), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationDialog(
    context: Context,
    onDismiss: () -> Unit,
    onAdd: (LocationZone) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf(100) }
    var currentLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var isLoadingLocation by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppColors.GlassBgElevated,
        title = {
            Text(stringResource(R.string.new_location), color = AppColors.OnSurface, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.location_name)) },
                    placeholder = { Text(stringResource(R.string.eg_home_office)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Info,
                        focusedLabelColor = AppColors.Info,
                        cursorColor = AppColors.Info,
                        focusedContainerColor = AppColors.GlassBg,
                        unfocusedContainerColor = AppColors.GlassBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = radius.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { num ->
                            radius = num.coerceIn(50, 1000)
                        }
                    },
                    label = { Text(stringResource(R.string.radius_meters)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Info,
                        focusedLabelColor = AppColors.Info,
                        cursorColor = AppColors.Info,
                        focusedContainerColor = AppColors.GlassBg,
                        unfocusedContainerColor = AppColors.GlassBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        isLoadingLocation = true
                        getCurrentLocation(context) { lat, lon ->
                            currentLocation = Pair(lat, lon)
                            isLoadingLocation = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoadingLocation,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Info.copy(alpha = 0.2f),
                        contentColor = AppColors.Info
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.dp,
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(AppColors.Info.copy(alpha = 0.4f), AppColors.Info.copy(alpha = 0.2f))
                        )
                    )
                ) {
                    if (isLoadingLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = AppColors.Info
                        )
                    } else {
                        Icon(Icons.Filled.Place, null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (currentLocation != null) stringResource(R.string.location_captured)
                            else stringResource(R.string.use_current_location)
                        )
                    }
                }

                if (currentLocation != null) {
                    Text(
                        text = "\uD83D\uDCCD ${String.format("%.4f", currentLocation!!.first)}, ${String.format("%.4f", currentLocation!!.second)}",
                        fontSize = 12.sp,
                        color = AppColors.Success,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && currentLocation != null) {
                        onAdd(
                            LocationZone(
                                name = name,
                                latitude = currentLocation!!.first,
                                longitude = currentLocation!!.second,
                                radius = radius
                            )
                        )
                    }
                },
                enabled = name.isNotBlank() && currentLocation != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Info.copy(alpha = 0.2f),
                    contentColor = AppColors.Info
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(AppColors.Info.copy(alpha = 0.4f), AppColors.Info.copy(alpha = 0.2f))
                    )
                )
            ) {
                Text(stringResource(R.string.add), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = AppColors.OnSurface)
            }
        }
    )
}

@Composable
fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color
) {
    GlassCard(accentColor = color) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            GlassIconBadge(
                icon = icon,
                accentColor = color,
                size = 64.dp,
                iconSize = 32.dp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun ConfigureAppsButton(
    mode: String,
    appsCount: Int,
    icon: ImageVector,
    label: String,
    description: String,
    color: Color
) {
    val context = LocalContext.current

    GlassCard(
        accentColor = color,
        shape = RoundedCornerShape(14.dp),
        onClick = {
            val intent = android.content.Intent(context, AppSettingsActivity::class.java)
            intent.putExtra("mode", mode)
            context.startActivity(intent)
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                GlassIconBadge(
                    icon = icon,
                    accentColor = color,
                    size = 44.dp,
                    iconSize = 22.dp
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = if (appsCount > 0) stringResource(R.string.apps_configured_count, appsCount) else description,
                        fontSize = 12.sp,
                        color = if (appsCount > 0) color else AppColors.OnSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (appsCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(color.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, color.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = appsCount.toString(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ==================== FONCTIONS UTILITAIRES ====================

private fun formatTimeRange(startH: Int, startM: Int, endH: Int, endM: Int): String {
    return String.format("%02d:%02d - %02d:%02d", startH, startM, endH, endM)
}

private fun formatTimeRemaining(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}

private fun isScheduleActive(schedule: TimeSchedule): Boolean {
    if (!schedule.enabled) return false

    val calendar = Calendar.getInstance()
    val currentDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
    val currentTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    val startTime = schedule.startHour * 60 + schedule.startMinute
    val endTime = schedule.endHour * 60 + schedule.endMinute

    val isDayActive = (schedule.days and (1 shl currentDay)) != 0
    val isTimeActive = currentTime in startTime..endTime

    return isDayActive && isTimeActive
}

private fun hasLocationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

private fun getCurrentLocation(context: Context, onLocation: (Double, Double) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            onLocation(location.latitude, location.longitude)
            locationManager.removeUpdates(this)
        }
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    try {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0L,
            0f,
            listener
        )

        // Fallback sur la derniere position connue
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
            onLocation(it.latitude, it.longitude)
            locationManager.removeUpdates(listener)
        }
    } catch (e: Exception) {
        // Gerer l'erreur
    }
}

// ==================== FONCTIONS DE SAUVEGARDE ====================

private fun loadSchedules(context: Context): List<TimeSchedule> {
    val prefs = context.getSharedPreferences("smart_planning", Context.MODE_PRIVATE)
    val json = prefs.getString("schedules", "[]")
    return try {
        val jsonArray = org.json.JSONArray(json)
        (0 until jsonArray.length()).map { i ->
            val obj = jsonArray.getJSONObject(i)
            TimeSchedule(
                id = obj.getString("id"),
                name = obj.getString("name"),
                startHour = obj.getInt("startHour"),
                startMinute = obj.getInt("startMinute"),
                endHour = obj.getInt("endHour"),
                endMinute = obj.getInt("endMinute"),
                days = obj.getInt("days"),
                enabled = obj.getBoolean("enabled")
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

private fun saveSchedules(context: Context, schedules: List<TimeSchedule>) {
    val prefs = context.getSharedPreferences("smart_planning", Context.MODE_PRIVATE)
    try {
        val jsonArray = org.json.JSONArray()
        schedules.forEach { schedule ->
            val validated = schedule.copy(
                startHour = schedule.startHour.coerceIn(0, 23),
                startMinute = schedule.startMinute.coerceIn(0, 59),
                endHour = schedule.endHour.coerceIn(0, 23),
                endMinute = schedule.endMinute.coerceIn(0, 59),
                days = schedule.days.coerceIn(0, 0b1111111)
            )
            val obj = org.json.JSONObject()
            obj.put("id", validated.id)
            obj.put("name", validated.name)
            obj.put("startHour", validated.startHour)
            obj.put("startMinute", validated.startMinute)
            obj.put("endHour", validated.endHour)
            obj.put("endMinute", validated.endMinute)
            obj.put("days", validated.days)
            obj.put("enabled", validated.enabled)
            jsonArray.put(obj)
        }
        prefs.edit().putString("schedules", jsonArray.toString()).apply()
    } catch (e: Exception) {
        android.util.Log.e("SmartPlanning", "Erreur lors de la sauvegarde des horaires", e)
    }
}

private fun loadLocationZones(context: Context): List<LocationZone> {
    val prefs = context.getSharedPreferences("smart_planning", Context.MODE_PRIVATE)
    val json = prefs.getString("location_zones", "[]")
    return try {
        val jsonArray = org.json.JSONArray(json)
        (0 until jsonArray.length()).map { i ->
            val obj = jsonArray.getJSONObject(i)
            LocationZone(
                id = obj.getString("id"),
                name = obj.getString("name"),
                latitude = obj.getDouble("latitude"),
                longitude = obj.getDouble("longitude"),
                radius = obj.getInt("radius"),
                blockingEnabled = obj.getBoolean("blockingEnabled")
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

private fun saveLocationZones(context: Context, zones: List<LocationZone>) {
    val prefs = context.getSharedPreferences("smart_planning", Context.MODE_PRIVATE)
    try {
        val jsonArray = org.json.JSONArray()
        zones.forEach { zone ->
            val validated = zone.copy(
                latitude = zone.latitude.coerceIn(-90.0, 90.0),
                longitude = zone.longitude.coerceIn(-180.0, 180.0),
                radius = zone.radius.coerceIn(50, 10_000)
            )
            val obj = org.json.JSONObject()
            obj.put("id", validated.id)
            obj.put("name", validated.name)
            obj.put("latitude", validated.latitude)
            obj.put("longitude", validated.longitude)
            obj.put("radius", validated.radius)
            obj.put("blockingEnabled", validated.blockingEnabled)
            jsonArray.put(obj)
        }
        prefs.edit().putString("location_zones", jsonArray.toString()).apply()
    } catch (e: Exception) {
        android.util.Log.e("SmartPlanning", "Erreur lors de la sauvegarde des zones", e)
    }
}

private fun loadCurrentFocusSession(context: Context): FocusSession? {
    val prefs = context.getSharedPreferences("smart_planning", Context.MODE_PRIVATE)
    val isActive = prefs.getBoolean("focus_active", false)
    if (!isActive) return null

    return FocusSession(
        name = prefs.getString("focus_name", "") ?: "",
        duration = prefs.getInt("focus_duration", 0),
        startTime = prefs.getLong("focus_start_time", 0L),
        isActive = true
    )
}

private fun startFocusSession(context: Context, duration: Int, name: String): FocusSession {
    val session = FocusSession(
        name = name,
        duration = duration,
        startTime = System.currentTimeMillis(),
        isActive = true
    )

    val prefs = context.getSharedPreferences("smart_planning", Context.MODE_PRIVATE)
    prefs.edit()
        .putBoolean("focus_active", true)
        .putString("focus_name", name)
        .putInt("focus_duration", duration)
        .putLong("focus_start_time", session.startTime)
        .apply()

    return session
}

private fun endFocusSession(context: Context) {
    val prefs = context.getSharedPreferences("smart_planning", Context.MODE_PRIVATE)
    prefs.edit()
        .putBoolean("focus_active", false)
        .remove("focus_name")
        .remove("focus_duration")
        .remove("focus_start_time")
        .apply()
}
