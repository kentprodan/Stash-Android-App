package com.example.stash.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.stash.repository.SceneItem
import com.example.stash.ui.viewmodel.ReelsViewModel
import com.example.stash.ui.viewmodel.UiState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReelsScreen(navController: NavController, viewModel: ReelsViewModel = viewModel()) {
    val scenes by viewModel.scenes.collectAsState()
    var showDetailsSheet by remember { mutableStateOf(false) }
    var selectedScene by remember { mutableStateOf<SceneItem?>(null) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var ratingSceneId by remember { mutableStateOf("") }

    when (val state = scenes) {
        is UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message)
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Retry")
                    }
                }
            }
        }
        is UiState.Success -> {
            val sceneList = state.data
            if (sceneList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No scenes found")
                }
            } else {
                val pagerState = rememberPagerState()
                
                Box(Modifier.fillMaxSize()) {
                    VerticalPager(
                        count = sceneList.size,
                        modifier = Modifier.fillMaxSize(),
                        state = pagerState
                    ) { page ->
                        val scene = sceneList[page]
                        ReelItem(
                            scene = scene,
                            viewModel = viewModel,
                            onRatingClick = {
                                ratingSceneId = scene.id
                                showRatingDialog = true
                            },
                            onDetailsClick = {
                                selectedScene = scene
                                showDetailsSheet = true
                            },
                            onIncrementOCount = {
                                viewModel.incrementOCount(scene.id)
                            }
                        )
                    }

                    // Performer info overlay at top left
                    val currentScene = sceneList.getOrNull(pagerState.currentPage)
                    currentScene?.performers?.firstOrNull()?.let { performer ->
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AsyncImage(
                                    model = performer.image,
                                    contentDescription = performer.name,
                                    modifier = Modifier.size(40.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = performer.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }

                }

                // Details bottom sheet
                if (showDetailsSheet && selectedScene != null) {
                    ModalBottomSheet(
                        onDismissRequest = { showDetailsSheet = false }
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(selectedScene!!.title, style = MaterialTheme.typography.headlineSmall)
                            Text("Duration: ${(selectedScene!!.duration / 60).toInt()} min")
                            Text("Rating: ${selectedScene!!.rating?.let { "${it / 20}★ ($it%)" } ?: "Not rated"}")
                            Text("O-Count: ${selectedScene!!.oCount ?: 0}")
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { navController.navigate("scene/${selectedScene!!.id}") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View Full Details")
                            }
                        }
                    }
                }

                // Rating dialog
                if (showRatingDialog) {
                    val currentRating = sceneList.find { it.id == ratingSceneId }?.rating?.div(20) ?: 0
                    var selectedRating by remember { mutableStateOf(currentRating) }
                    
                    AlertDialog(
                        onDismissRequest = { showRatingDialog = false },
                        title = { Text("Rate Scene") },
                        text = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Tap to rate", style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.height(24.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    (1..5).forEach { stars ->
                                        IconButton(
                                            onClick = {
                                                selectedRating = stars
                                                viewModel.updateRating(ratingSceneId, stars * 20)
                                                showRatingDialog = false
                                            },
                                            modifier = Modifier.size(56.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (stars <= selectedRating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                                contentDescription = "$stars stars",
                                                tint = if (stars <= selectedRating) Color(0xFFFFD700) else Color.Gray,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showRatingDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReelItem(
    scene: SceneItem,
    viewModel: ReelsViewModel = viewModel(),
    onRatingClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onIncrementOCount: () -> Unit
) {
    android.util.Log.d("ReelsScreen", "Loading scene: ${scene.title}, streamUrl: ${scene.streamUrl}")
    
    val context = LocalContext.current
    var hasTrackedPlay by remember(scene.id) { mutableStateOf(false) }
    var currentPosition by remember(scene.id) { mutableStateOf(0L) }
    var duration by remember(scene.id) { mutableStateOf(0L) }
    
    // Create ExoPlayer only when streamUrl is available
    val exoPlayer = remember(scene.id, scene.streamUrl) {
        if (scene.streamUrl != null) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(scene.streamUrl))
                prepare()
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ONE
                
                // Add listener to track play and update position
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY && !hasTrackedPlay) {
                            android.util.Log.d("ReelsScreen", "Video started playing: ${scene.title}")
                            viewModel.incrementPlayCount(scene.id)
                            hasTrackedPlay = true
                            duration = this@apply.duration
                        }
                    }
                })
            }
        } else null
    }
    
    // Update current position periodically
    LaunchedEffect(exoPlayer) {
        while (exoPlayer != null) {
            currentPosition = exoPlayer.currentPosition
            kotlinx.coroutines.delay(100)
        }
    }
    
    // Release player when composable leaves composition
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer?.release()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (exoPlayer != null && scene.streamUrl != null) {
            // Video player
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // Bottom controls overlay
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    // Scene title
                    Text(
                        text = scene.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        maxLines = 1
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    // Progress bar
                    Slider(
                        value = if (duration > 0) currentPosition.toFloat() else 0f,
                        onValueChange = { exoPlayer.seekTo(it.toLong()) },
                        valueRange = 0f..duration.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.Gray
                        )
                    )
                    
                    // Time and actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Time display
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = formatTime(currentPosition),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Text(
                                text = "/",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = formatTime(duration),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        
                        // Action buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // O-Count
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(onClick = onIncrementOCount, modifier = Modifier.size(40.dp)) {
                                    Icon(
                                        Icons.Default.WaterDrop,
                                        contentDescription = "O-Count",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                scene.oCount?.let {
                                    Text(
                                        text = it.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }
                            
                            // Rating
                            IconButton(onClick = onRatingClick, modifier = Modifier.size(40.dp)) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Rate",
                                    tint = if (scene.rating != null && scene.rating!! > 0) Color.Yellow else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            // Details
                            IconButton(onClick = onDetailsClick, modifier = Modifier.size(40.dp)) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Details",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else if (scene.thumbnail != null) {
            // Fallback to thumbnail
            AsyncImage(
                model = scene.thumbnail,
                contentDescription = scene.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
                onError = { 
                    android.util.Log.e("ReelsScreen", "Failed to load thumbnail: ${scene.thumbnail}")
                }
            )
        } else {
            // Fallback when neither available
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = scene.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Text(
                    text = "No video or thumbnail available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}
