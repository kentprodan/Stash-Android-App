package com.example.stash.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
                // Track live playback progress and local play count increments per scene
                val livePositions = remember { mutableStateMapOf<String, Long>() }
                val extraPlayCounts = remember { mutableStateMapOf<String, Int>() }
                
                Box(Modifier.fillMaxSize()) {
                    VerticalPager(
                        count = sceneList.size,
                        modifier = Modifier.fillMaxSize(),
                        state = pagerState
                    ) { page ->
                        val scene = sceneList[page]
                        ReelItem(
                            scene = scene,
                            isActive = page == pagerState.currentPage,
                            viewModel = viewModel,
                            onProgress = { id, positionMs -> livePositions[id] = positionMs },
                            onPlayTracked = { id -> 
                                extraPlayCounts[id] = (extraPlayCounts[id] ?: 0) + 1
                            },
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
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = performer.image,
                                contentDescription = performer.name,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        navController.navigate("performer/${performer.id}")
                                    },
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

                // Details bottom sheet
                if (showDetailsSheet && selectedScene != null) {
                    SceneDetailsSheet(
                        scene = selectedScene!!,
                        navController = navController,
                        onDismiss = { showDetailsSheet = false },
                        playCountOverride = ((selectedScene?.playCount ?: 0) + (extraPlayCounts[selectedScene!!.id] ?: 0)),
                        playDurationSecondsOverride = ((selectedScene?.playDuration ?: 0.0) + ((livePositions[selectedScene!!.id] ?: 0L) / 1000.0)),
                        viewModel = viewModel
                    )
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
    isActive: Boolean,
    viewModel: ReelsViewModel = viewModel(),
    onProgress: (sceneId: String, positionMs: Long) -> Unit,
    onPlayTracked: (sceneId: String) -> Unit,
    onRatingClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onIncrementOCount: () -> Unit
) {
    android.util.Log.d("ReelsScreen", "Loading scene: ${scene.title}, streamUrl: ${scene.streamUrl}")
    
    val context = LocalContext.current
    var hasTrackedPlayForThisView by remember { mutableStateOf(false) }
    var currentPosition by remember(scene.id) { mutableStateOf(0L) }
    var duration by remember(scene.id) { mutableStateOf(0L) }
    var isPlaying by remember(scene.id) { mutableStateOf(true) }
    var sessionAccumMs by remember(scene.id) { mutableStateOf(0L) }
    var lastPosMs by remember(scene.id) { mutableStateOf(0L) }
    
    // Create ExoPlayer only when streamUrl is available
    val exoPlayer = remember(scene.id, scene.streamUrl) {
        if (scene.streamUrl != null) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(scene.streamUrl))
                prepare()
                playWhenReady = false  // Don't auto-play, wait for isActive
                repeatMode = Player.REPEAT_MODE_ONE
                
                // Add listener to get duration
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            duration = this@apply.duration
                        }
                    }
                })
            }
        } else null
    }
    
    // Track play count when scene becomes active
    LaunchedEffect(isActive) {
        if (isActive && !hasTrackedPlayForThisView) {
            android.util.Log.d("ReelsScreen", "Scene became active: ${scene.title}")
            viewModel.incrementPlayCount(scene.id)
            hasTrackedPlayForThisView = true
            onPlayTracked(scene.id)
        }
    }
    
    // Pause/Play based on active page
    LaunchedEffect(isActive, exoPlayer) {
        if (exoPlayer != null) {
            if (isActive) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            } else {
                exoPlayer.playWhenReady = false
                exoPlayer.pause()
            }
        }
    }

    // Update current position periodically
    LaunchedEffect(exoPlayer) {
        while (exoPlayer != null) {
            currentPosition = exoPlayer.currentPosition
            onProgress(scene.id, currentPosition)
            kotlinx.coroutines.delay(100)
        }
    }

    // Accumulate watched time considering loops
    LaunchedEffect(exoPlayer, isPlaying) {
        while (exoPlayer != null) {
            if (isPlaying) {
                val pos = exoPlayer.currentPosition
                val delta = if (pos >= lastPosMs) pos - lastPosMs else (duration - lastPosMs) + pos
                if (delta > 0) {
                    sessionAccumMs += delta
                    lastPosMs = pos
                }
            } else {
                lastPosMs = exoPlayer.currentPosition
            }
            kotlinx.coroutines.delay(1000)
        }
    }
    
    // Release player when composable leaves composition
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer?.release()
            val baseSeconds = scene.playDuration ?: 0.0
            val additionalSeconds = sessionAccumMs / 1000.0
            if (additionalSeconds > 0) {
                viewModel.appendPlayDuration(scene.id, baseSeconds, additionalSeconds)
            }
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
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if (isPlaying) {
                                    exoPlayer.pause()
                                } else {
                                    exoPlayer.play()
                                }
                                isPlaying = !isPlaying
                            }
                        )
                    }
            )
            
            // Action buttons - vertical stack on bottom right
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // O-Count
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = onIncrementOCount, modifier = Modifier.size(48.dp)) {
                        Icon(
                            Icons.Default.WaterDrop,
                            contentDescription = "O-Count",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = onRatingClick, modifier = Modifier.size(48.dp)) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rate",
                            tint = if (scene.rating != null && scene.rating!! > 0) Color.Yellow else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    scene.rating?.let {
                        if (it > 0) {
                            Text(
                                text = (it / 20).toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
                
                // Details
                IconButton(onClick = onDetailsClick, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Details",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            // Bottom seekbar with time on sides
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Current time on left
                Text(
                    text = formatTime(currentPosition),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                
                // Very thin progress bar in center
                Slider(
                    value = if (duration > 0) currentPosition.toFloat() else 0f,
                    onValueChange = { exoPlayer.seekTo(it.toLong()) },
                    valueRange = 0f..duration.toFloat(),
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.Gray
                    )
                )
                
                // Total duration on right
                Text(
                    text = formatTime(duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
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
