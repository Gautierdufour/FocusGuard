package com.focusguard.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import com.focusguard.app.ui.theme.FocusGuardTheme
import com.focusguard.app.components.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.launch

class AppSettingsActivity : ComponentActivity() {
    private val prefs by lazy {
        getSharedPreferences("app_blocker_settings", Context.MODE_PRIVATE)
    }

    private val mode by lazy {
        intent.getStringExtra("mode") ?: "default"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusGuardTheme {
                GlassBackground {
                    DarkAppSettingsScreen(
                        onSave = { selectedApps ->
                            saveSelectedApps(selectedApps)
                            finish()
                        },
                        onBack = { finish() },
                        initialSelectedApps = getSelectedApps(),
                        mode = mode
                    )
                }
            }
        }
    }

    private fun saveSelectedApps(selectedApps: Set<String>) {
        val key = when (mode) {
            "focus" -> "selected_apps_focus"
            "location" -> "selected_apps_location"
            else -> "selected_apps"
        }
        prefs.edit()
            .putStringSet(key, selectedApps)
            .apply()
    }

    private fun getSelectedApps(): Set<String> {
        val key = when (mode) {
            "focus" -> "selected_apps_focus"
            "location" -> "selected_apps_location"
            else -> "selected_apps"
        }
        val apps = prefs.getStringSet(key, null)
        // Si pas de liste spécifique, utiliser la liste globale
        return apps ?: (prefs.getStringSet("selected_apps", emptySet()) ?: emptySet())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkAppSettingsScreen(
    onSave: (Set<String>) -> Unit,
    onBack: () -> Unit,
    initialSelectedApps: Set<String>,
    mode: String = "default"
) {
    val modeTitle = when (mode) {
        "focus" -> "Mode Focus"
        "location" -> "Mode Géographique"
        else -> "Sélection des apps"
    }

    val modeDescription = when (mode) {
        "focus" -> "Apps bloquées en mode focus"
        "location" -> "Apps bloquées dans les zones"
        else -> null
    }
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedApps by remember { mutableStateOf(initialSelectedApps) }
    var allApps by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AppCategory.ALL) }
    var showSystemApps by remember { mutableStateOf(false) }

    LaunchedEffect(showSystemApps) {
        isLoading = true
        allApps = AppScanner.getAllInstalledApps(context, showSystemApps)
        isLoading = false
    }

    val filteredApps = remember(allApps, searchQuery, selectedCategory) {
        var filtered = allApps

        if (selectedCategory != AppCategory.ALL) {
            filtered = AppScanner.filterByCategory(filtered, selectedCategory)
        }

        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter { app ->
                app.appName.contains(searchQuery, ignoreCase = true) ||
                        app.packageName.contains(searchQuery, ignoreCase = true)
            }
        }

        filtered
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = modeTitle,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.OnSurface,
                            fontSize = 18.sp
                        )
                        if (modeDescription != null) {
                            Text(
                                text = modeDescription,
                                fontSize = 12.sp,
                                color = AppColors.OnSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = AppColors.Primary
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { onSave(selectedApps) }) {
                        Text(
                            text = "Sauvegarder",
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Success
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
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AppColors.Primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Scan des applications...",
                            color = AppColors.OnSurfaceVariant
                        )
                    }
                }
            } else {
                DarkSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                DarkCategoryFilters(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                DarkSelectionSummaryBar(
                    selectedCount = selectedApps.size,
                    totalCount = filteredApps.size,
                    totalAvailable = allApps.size,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        DarkSystemAppsToggle(
                            showSystemApps = showSystemApps,
                            onToggle = { showSystemApps = it }
                        )
                    }

                    if (filteredApps.isEmpty()) {
                        item {
                            DarkEmptyStateCard(searchQuery, showSystemApps)
                        }
                    } else {
                        items(filteredApps) { app ->
                            DarkAppItemCard(
                                app = app,
                                isSelected = selectedApps.contains(app.packageName),
                                onSelectionChanged = { isSelected ->
                                    selectedApps = if (isSelected) {
                                        selectedApps + app.packageName
                                    } else {
                                        selectedApps - app.packageName
                                    }
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun DarkSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Rechercher...", color = AppColors.OnSurfaceVariant) },
        leadingIcon = {
            Icon(Icons.Filled.Search, null, tint = AppColors.Primary)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, null, tint = AppColors.OnSurfaceVariant)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.Primary,
            unfocusedBorderColor = AppColors.GlassBorderLight,
            focusedContainerColor = AppColors.GlassBg,
            unfocusedContainerColor = AppColors.GlassBg,
            focusedTextColor = AppColors.OnSurface,
            unfocusedTextColor = AppColors.OnSurface
        )
    )
}

@Composable
fun DarkCategoryFilters(
    selectedCategory: AppCategory,
    onCategorySelected: (AppCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppCategory.values().forEach { category ->
            val isSelected = selectedCategory == category

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        category.displayName,
                        color = if (isSelected) AppColors.Primary else AppColors.OnSurfaceVariant
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.Primary.copy(alpha = 0.2f),
                    containerColor = AppColors.GlassBgSubtle
                ),
                border = if (isSelected) {
                    FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = true,
                        borderColor = AppColors.Primary.copy(alpha = 0.4f),
                        borderWidth = 1.dp
                    )
                } else {
                    FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = false,
                        borderColor = AppColors.GlassBorderLight
                    )
                }
            )
        }
    }
}

@Composable
fun DarkSelectionSummaryBar(
    selectedCount: Int,
    totalCount: Int,
    totalAvailable: Int,
    modifier: Modifier = Modifier
) {
    val accentColor = if (selectedCount > 0) AppColors.Success else AppColors.OnSurfaceVariant

    GlassCard(
        modifier = modifier,
        accentColor = accentColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (selectedCount > 0) Icons.Filled.CheckCircle else Icons.Filled.Info,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "$selectedCount sélectionnée(s)",
                    fontWeight = FontWeight.Bold,
                    color = if (selectedCount > 0) AppColors.Success else AppColors.OnSurface,
                    fontSize = 15.sp
                )
                Text(
                    text = "$totalCount affichée(s) sur $totalAvailable",
                    fontSize = 11.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DarkSystemAppsToggle(
    showSystemApps: Boolean,
    onToggle: (Boolean) -> Unit
) {
    GlassCard(
        accentColor = AppColors.Info,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Apps système",
                    fontWeight = FontWeight.Medium,
                    color = AppColors.OnSurface
                )
                Text(
                    text = "Chrome, YouTube, Gmail, Maps...",
                    fontSize = 11.sp,
                    color = AppColors.OnSurfaceVariant
                )
            }
            Switch(
                checked = showSystemApps,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = AppColors.Info,
                    uncheckedThumbColor = AppColors.OnSurfaceLight,
                    uncheckedTrackColor = AppColors.SurfaceVariant
                )
            )
        }
    }
}

@Composable
fun DarkAppItemCard(
    app: InstalledApp,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    GlassCard(
        accentColor = if (isSelected) AppColors.Primary else AppColors.OnSurfaceVariant,
        shape = RoundedCornerShape(14.dp),
        onClick = { onSelectionChanged(!isSelected) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            app.icon?.let { drawable ->
                Image(
                    bitmap = drawable.toBitmap(64, 64).asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } ?: Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.GlassBgSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = null,
                    tint = AppColors.OnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.OnSurface
                )
                Text(
                    text = app.packageName,
                    fontSize = 11.sp,
                    color = AppColors.OnSurfaceVariant
                )
                if (app.isSystemApp) {
                    Text(
                        text = "Système",
                        fontSize = 10.sp,
                        color = AppColors.Warning,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChanged,
                colors = CheckboxDefaults.colors(
                    checkedColor = AppColors.Primary,
                    uncheckedColor = AppColors.OnSurfaceLight
                )
            )
        }
    }
}

@Composable
fun DarkEmptyStateCard(searchQuery: String, showSystemApps: Boolean) {
    GlassCard(
        accentColor = AppColors.Warning,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = AppColors.Warning
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (searchQuery.isEmpty())
                    "Aucune application"
                else "Aucun résultat pour \"$searchQuery\"",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.OnSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = when {
                    searchQuery.isNotEmpty() -> "Essayez une autre recherche"
                    !showSystemApps -> "Activez 'Apps système'"
                    else -> "Aucune app disponible"
                },
                fontSize = 13.sp,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
