package com.example.stash.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
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
                        ReelItem(scene = scene)
                    }

                    // Overlay controls
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp)
                            .align(Alignment.CenterEnd),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        val currentScene = sceneList.getOrNull(pagerState.currentPage)
                        
                        // O-Count
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            FloatingActionButton(
                                onClick = { /* TODO: Increment O-count */ },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Default.TrendingUp, contentDescription = "O-Count")
                            }
                            currentScene?.oCount?.let {
                                Text(
                                    text = it.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }

                        // Rating
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { /* TODO: Rate */ }) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Rate",
                                    tint = Color.White
                                )
                            }
                            currentScene?.rating?.let {
                                Text(
                                    text = "$it%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }

                        // Details
                        IconButton(onClick = { 
                            selectedScene = currentScene
                            showDetailsSheet = true
                        }) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Details",
                                tint = Color.White
                            )
                        }
                    }

                    // Scene title overlay at bottom
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth(),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            sceneList.getOrNull(pagerState.currentPage)?.let { scene ->
                                Text(
                                    text = scene.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "${(scene.duration / 60).toInt()} min",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
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
                            Text("Rating: ${selectedScene!!.rating ?: "N/A"}%")
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
            }
        }
    }
}

@Composable
fun ReelItem(scene: SceneItem) {
    android.util.Log.d("ReelsScreen", "Loading scene: ${scene.title}, streamUrl: ${scene.streamUrl}")
    
    val context = LocalContext.current
    
    // Create ExoPlayer only when streamUrl is available
    val exoPlayer = remember(scene.streamUrl) {
        if (scene.streamUrl != null) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(scene.streamUrl))
                prepare()
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ONE
            }
        } else null
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
