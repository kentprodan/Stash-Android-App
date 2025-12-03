package com.example.stash.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.stash.repository.SceneItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneDetailScreen(navController: NavController, sceneId: String) {
    // TODO: Fetch scene details from repository
    val scene = remember { SceneItem(sceneId, "Sample Scene", null, null, 0.0, 0, 0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(scene.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = scene.thumbnail,
                contentDescription = scene.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
            Text("Duration: ${(scene.duration / 60).toInt()} min", style = MaterialTheme.typography.bodyLarge)
            Text("Rating: ${scene.rating ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
            Text("O-Count: ${scene.oCount ?: 0}", style = MaterialTheme.typography.bodyLarge)
            Button(onClick = { /* TODO: Play scene */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Play")
            }
        }
    }
}
