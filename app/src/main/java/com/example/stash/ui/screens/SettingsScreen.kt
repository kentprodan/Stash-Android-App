package com.example.stash.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stash.BuildConfig
import com.example.stash.ui.viewmodel.SettingsViewModel
import com.example.stash.ui.viewmodel.UiState
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    val url by viewModel.serverUrl.collectAsState()
    val api by viewModel.apiKey.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val versionInfo by viewModel.versionInfo.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val themeOptions = listOf("system", "light", "dark")
    var showServerDialog by remember { mutableStateOf(false) }
    val connected = (url?.isNotBlank() == true) && (api?.isNotBlank() == true)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp), 
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        // Theme Selection Section
        Text("Theme", style = MaterialTheme.typography.titleLarge)
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("App Theme", style = MaterialTheme.typography.titleMedium)
                themeOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = themeMode == option,
                            onClick = { viewModel.setThemeMode(option) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            when (option) {
                                "system" -> "System Default"
                                "light" -> "Light"
                                "dark" -> "Dark"
                                else -> option
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
        
        // Connection Section
        Text("Connection", style = MaterialTheme.typography.titleLarge)
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ListItem(
                    headlineContent = { Text("Status", style = MaterialTheme.typography.titleMedium) },
                    supportingContent = { 
                        Text(
                            if (connected) "Connected to $url" else "Not configured",
                            style = MaterialTheme.typography.bodyMedium
                        ) 
                    },
                    leadingContent = { 
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = if (connected) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.errorContainer
                        ) {
                            Icon(
                                if (connected) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                contentDescription = null,
                                tint = if (connected) 
                                    MaterialTheme.colorScheme.onPrimaryContainer 
                                else 
                                    MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                )
                
                HorizontalDivider()
                
                when (versionInfo) {
                    is UiState.Success -> {
                        val version = (versionInfo as UiState.Success).data
                        ListItem(
                            headlineContent = { Text("Server Version", style = MaterialTheme.typography.titleMedium) },
                            supportingContent = { 
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(version.currentVersion, style = MaterialTheme.typography.bodyMedium)
                                    if (version.updateAvailable && version.latestVersion != null) {
                                        Surface(
                                            shape = MaterialTheme.shapes.extraSmall,
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                "Update available: ${version.latestVersion}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            leadingContent = { 
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Icon(
                                        Icons.Filled.Info, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        )
                    }
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                    is UiState.Error -> {}
                }
                
                HorizontalDivider()
                
                FilledTonalButton(
                    onClick = { showServerDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Change Server")
                }
            }
        }

        // Server Stats Section
        Text("Server Stats", style = MaterialTheme.typography.titleLarge)
        when (stats) {
            is UiState.Success -> {
                val data = (stats as UiState.Success).data
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ListItem(
                            headlineContent = { Text("Scenes", style = MaterialTheme.typography.titleMedium) },
                            supportingContent = { Text("${data.totalScenes}", style = MaterialTheme.typography.bodyLarge) },
                            leadingContent = { 
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Icon(
                                        Icons.Filled.PlayArrow, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("Images", style = MaterialTheme.typography.titleMedium) },
                            supportingContent = { Text("${data.totalImages}", style = MaterialTheme.typography.bodyLarge) },
                            leadingContent = { 
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Icon(
                                        Icons.Filled.Image, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("Performers", style = MaterialTheme.typography.titleMedium) },
                            supportingContent = { Text("${data.totalPerformers}", style = MaterialTheme.typography.bodyLarge) },
                            leadingContent = { 
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Icon(
                                        Icons.Filled.Person, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("Total Playtime", style = MaterialTheme.typography.titleMedium) },
                            supportingContent = { Text("${(data.totalPlaytime / 3600).roundToInt()} hours", style = MaterialTheme.typography.bodyLarge) },
                            leadingContent = { 
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Icon(
                                        Icons.Filled.Timer, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("O-Count", style = MaterialTheme.typography.titleMedium) },
                            supportingContent = { Text("${data.totalOCount}", style = MaterialTheme.typography.bodyLarge) },
                            leadingContent = { 
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Icon(
                                        Icons.Filled.WaterDrop, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        (stats as UiState.Error).message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // About Section
        Text("About", style = MaterialTheme.typography.titleLarge)
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ListItem(
                    headlineContent = { Text("Stash Android Client", style = MaterialTheme.typography.titleMedium) },
                    supportingContent = { Text("Built with Material 3 Expressive", style = MaterialTheme.typography.bodyMedium) },
                    leadingContent = { 
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Icon(
                                Icons.Filled.Android, 
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("App Version", style = MaterialTheme.typography.titleMedium) },
                    supportingContent = { Text(BuildConfig.VERSION_NAME, style = MaterialTheme.typography.bodyLarge) },
                    leadingContent = { 
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                Icons.Filled.Info, 
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                )
            }
        }
        
        // Add some bottom padding for better scrolling
        Spacer(Modifier.height(16.dp))
    }
    
    if (showServerDialog) {
        AlertDialog(
            onDismissRequest = { showServerDialog = false },
            title = { Text("Change Server") },
            text = { Text("Server configuration change is not implemented yet. Please reinstall the app to change servers.") },
            confirmButton = {
                TextButton(onClick = { showServerDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
