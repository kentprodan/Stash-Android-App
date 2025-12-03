package com.example.stash.ui.screens

import androidx.compose.foundation.layout.*
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
    val connected = (url?.isNotBlank() == true) && (api?.isNotBlank() == true)
    var showServerDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Connection", style = MaterialTheme.typography.headlineLarge)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ListItem(
                    headlineContent = { Text("Status") },
                    supportingContent = { Text(if (connected) "Connected to $url" else "Not configured") },
                    leadingContent = { 
                        Icon(
                            if (connected) Icons.Filled.CheckCircle else Icons.Filled.Error,
                            contentDescription = null,
                            tint = if (connected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                )
                
                when (versionInfo) {
                    is UiState.Success -> {
                        val version = (versionInfo as UiState.Success).data
                        ListItem(
                            headlineContent = { Text("Server Version") },
                            supportingContent = { 
                                Column {
                                    Text(version.currentVersion)
                                    if (version.updateAvailable && version.latestVersion != null) {
                                        Text(
                                            "Update available: ${version.latestVersion}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                            leadingContent = { Icon(Icons.Filled.Info, contentDescription = null) }
                        )
                    }
                    is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    is UiState.Error -> {}
                }
                
                Button(
                    onClick = { showServerDialog = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Change Server")
                }
            }
        }

        Text("Server Stats", style = MaterialTheme.typography.headlineLarge)
        when (stats) {
            is UiState.Success -> {
                val data = (stats as UiState.Success).data
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ListItem(
                            headlineContent = { Text("Scenes") },
                            supportingContent = { Text("${data.totalScenes}") },
                            leadingContent = { Icon(Icons.Filled.PlayArrow, contentDescription = null) }
                        )
                        ListItem(
                            headlineContent = { Text("Images") },
                            supportingContent = { Text("${data.totalImages}") },
                            leadingContent = { Icon(Icons.Filled.Image, contentDescription = null) }
                        )
                        ListItem(
                            headlineContent = { Text("Performers") },
                            supportingContent = { Text("${data.totalPerformers}") },
                            leadingContent = { Icon(Icons.Filled.Person, contentDescription = null) }
                        )
                        ListItem(
                            headlineContent = { Text("Total Playtime") },
                            supportingContent = { Text("${(data.totalPlaytime / 3600).roundToInt()} hours") },
                            leadingContent = { Icon(Icons.Filled.Timer, contentDescription = null) }
                        )
                        ListItem(
                            headlineContent = { Text("O-Count") },
                            supportingContent = { Text("${data.totalOCount}") },
                            leadingContent = { Icon(Icons.Filled.WaterDrop, contentDescription = null) }
                        )
                    }
                }
            }
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Error -> Text((stats as UiState.Error).message)
        }

        Text("About", style = MaterialTheme.typography.headlineLarge)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ListItem(
                    headlineContent = { Text("Stash Android Client") },
                    supportingContent = { Text("Built with Material 3 Expressive") },
                    leadingContent = { Icon(Icons.Filled.Android, contentDescription = null) }
                )
                ListItem(
                    headlineContent = { Text("App Version") },
                    supportingContent = { Text(BuildConfig.VERSION_NAME) },
                    leadingContent = { Icon(Icons.Filled.Info, contentDescription = null) }
                )
            }
        }
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
