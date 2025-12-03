package com.example.stash.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.stash.repository.SceneItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneDetailsSheet(
    scene: SceneItem,
    navController: NavController,
    onDismiss: () -> Unit,
    playCountOverride: Int? = null,
    playDurationSecondsOverride: Double? = null,
    viewModel: com.example.stash.ui.viewmodel.ReelsViewModel
) {
    // Observe the live scene state to get updated tags
    val scenesState = viewModel.scenes.collectAsState()
    val currentScene = remember(scene.id, scenesState.value) {
        if (scenesState.value is com.example.stash.ui.viewmodel.UiState.Success) {
            (scenesState.value as com.example.stash.ui.viewmodel.UiState.Success).data.find { it.id == scene.id } ?: scene
        } else {
            scene
        }
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title
            Text(
                text = scene.title,
                style = MaterialTheme.typography.headlineMedium
            )
            
            HorizontalDivider()
            
            // Performers (placed above the first divider)
            if (scene.performers.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Performers", style = MaterialTheme.typography.titleMedium)
                }
                scene.performers.forEach { performer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("performer/${performer.id}") }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = performer.image,
                            contentDescription = performer.name,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(performer.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                // Divider after performers, before video information
                HorizontalDivider()
            }

            // Video Information (moved below Performers)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.HighQuality, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Video Information", style = MaterialTheme.typography.titleMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text("Duration", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    val mins = (scene.duration / 60).toInt()
                    val secs = (scene.duration % 60).toInt()
                    Text("${mins}:${String.format("%02d", secs)}", style = MaterialTheme.typography.bodyMedium)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.HighQuality, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text("Resolution", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    val w = scene.width ?: 0
                    val h = scene.height ?: 0
                    val resLabel = when {
                        w == 0 || h == 0 -> "Unknown"
                        w >= 3840 || h >= 2160 -> "4K"
                        w >= 2560 || h >= 1440 -> "QHD"
                        w >= 1920 || h >= 1080 -> "Full HD"
                        w >= 1280 || h >= 720 -> "HD"
                        else -> "SD"
                    }
                    val raw = if (w > 0 && h > 0) " (${w}×${h})" else ""
                    Text(resLabel + raw, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Play Statistics (with Rating & O-Count above rows)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Play Statistics", style = MaterialTheme.typography.titleMedium)
            }
            // Rating & O-Count moved into Play Statistics section
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Outlined.StarBorder, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text("Rating", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    scene.rating?.let { rating ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row {
                                (1..5).forEach { star ->
                                    Icon(
                                        imageVector = if (star <= rating / 20) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = null,
                                        tint = if (star <= rating / 20) Color(0xFFFFD700) else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text("${rating / 20}", style = MaterialTheme.typography.bodyMedium)
                        }
                    } ?: Text("Not rated", style = MaterialTheme.typography.bodyMedium)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.WaterDrop, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text("O-Count", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    Text("${scene.oCount ?: 0}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Switch places: first Total Play Time, then Play Count
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.Timer, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text("Total Play Time", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    val totalSecondsBase = scene.playDuration ?: 0.0
                    val totalSeconds = (playDurationSecondsOverride ?: totalSecondsBase)
                    val totalMins = (totalSeconds / 60).toInt()
                    Text("$totalMins min", style = MaterialTheme.typography.bodyMedium)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text("Play Count", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    val playCount = playCountOverride ?: (scene.playCount ?: 0)
                    Text("$playCount times", style = MaterialTheme.typography.bodyMedium)
                }
            }
            
            // Tags (ensure at bottom)
            HorizontalDivider()
            var showAddTagDialog by remember { mutableStateOf(false) }
            var availableTags by remember { mutableStateOf<List<com.example.stash.repository.TagItem>>(emptyList()) }
            val scope = rememberCoroutineScope()

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Tags", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = { showAddTagDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add tag", tint = MaterialTheme.colorScheme.primary)
                }
            }
            if (currentScene.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentScene.tags.forEach { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp, top = 6.dp, bottom = 6.dp, end = 4.dp)
                            ) {
                                Text(
                                    text = tag.name,
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Spacer(Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            viewModel.removeTagFromScene(currentScene.id, currentScene.tags, tag)
                                        }
                                    },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "Remove ${tag.name}",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            } else{
                Text("No tags", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }

            if (showAddTagDialog) {
                LaunchedEffect(currentScene.tags) {
                    availableTags = viewModel.fetchAllTags().filter { availableTag ->
                        currentScene.tags.none { it.id == availableTag.id }
                    }
                }
                var newTagName by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showAddTagDialog = false },
                    title = { Text("Add Tag") },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // New tag creation section
                            Text("Create new tag", style = MaterialTheme.typography.labelMedium)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newTagName,
                                    onValueChange = { newTagName = it },
                                    label = { Text("Tag name") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Button(
                                    onClick = {
                                        if (newTagName.isNotBlank()) {
                                            android.util.Log.d("SceneDetailsSheet", "Create button clicked: tagName=${newTagName.trim()}, sceneId=${currentScene.id}")
                                            scope.launch {
                                                try {
                                                    android.util.Log.d("SceneDetailsSheet", "Starting createAndAddTag coroutine")
                                                    viewModel.createAndAddTag(currentScene.id, currentScene.tags, newTagName.trim())
                                                    android.util.Log.d("SceneDetailsSheet", "createAndAddTag completed, closing dialog")
                                                    showAddTagDialog = false
                                                } catch (e: Exception) {
                                                    android.util.Log.e("SceneDetailsSheet", "Error in createAndAddTag", e)
                                                }
                                            }
                                        }
                                    },
                                    enabled = newTagName.isNotBlank()
                                ) {
                                    Text("Create")
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(8.dp))
                            // Existing tags list
                            Text("Or select existing", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(8.dp))
                            if (availableTags.isEmpty()) {
                                Text("Loading tags...", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                availableTags.forEach { tag ->
                                    TextButton(
                                        onClick = {
                                            android.util.Log.d("SceneDetailsSheet", "Existing tag clicked: ${tag.name}, sceneId=${currentScene.id}")
                                            scope.launch {
                                                try {
                                                    android.util.Log.d("SceneDetailsSheet", "Starting addTagToScene coroutine")
                                                    viewModel.addTagToScene(currentScene.id, currentScene.tags, tag)
                                                    android.util.Log.d("SceneDetailsSheet", "addTagToScene completed, closing dialog")
                                                    showAddTagDialog = false
                                                } catch (e: Exception) {
                                                    android.util.Log.e("SceneDetailsSheet", "Error in addTagToScene", e)
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(tag.name, modifier = Modifier.fillMaxWidth())
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showAddTagDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            
            Spacer(Modifier.height(8.dp))
        }
    }
}
