package com.example.stash.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stash.ui.viewmodel.SettingsViewModel
import com.example.stash.ui.viewmodel.UiState
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    val url by viewModel.serverUrl.collectAsState()
    val api by viewModel.apiKey.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val connected = (url?.isNotBlank() == true) && (api?.isNotBlank() == true)

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ListItem(
            headlineContent = { Text("Connection Status") },
            supportingContent = { Text(if (connected) "Connected to ${url}" else "Not configured") },
            leadingContent = { Badge { Text(if (connected) "OK" else "ERR") } }
        )

        Text("Server Stats", style = MaterialTheme.typography.headlineLarge)
        when (stats) {
            is UiState.Success -> {
                val data = (stats as UiState.Success).data
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Scenes: ${data.totalScenes}")
                        Text("Images: ${data.totalImages}")
                        Text("Performers: ${data.totalPerformers}")
                        Text("Total Playtime: ${(data.totalPlaytime / 3600).roundToInt()}h")
                        Text("O-Count: ${data.totalOCount}")
                    }
                }
            }
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Error -> Text((stats as UiState.Error).message)
        }

        Text("About", style = MaterialTheme.typography.headlineLarge)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Stash Android Client", style = MaterialTheme.typography.titleMedium)
                Text("Built with Material 3 Expressive", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
