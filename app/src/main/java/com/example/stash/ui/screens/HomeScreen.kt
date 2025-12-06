package com.example.stash.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.stash.repository.*
import com.example.stash.ui.viewmodel.HomeViewModel
import com.example.stash.ui.viewmodel.UiState

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val performers by viewModel.performers.collectAsState()
    val images by viewModel.images.collectAsState()
    val scenes by viewModel.scenes.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Screen title
        item {
            Text("Home", style = MaterialTheme.typography.headlineLarge)
        }

        if (continueWatching is UiState.Success && (continueWatching as UiState.Success).data.isNotEmpty()) {
            item {
                Text("Continue Watching", style = MaterialTheme.typography.titleMedium)
                LazyRow(contentPadding = PaddingValues(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items((continueWatching as UiState.Success).data) { item ->
                        SceneCard(item, onClick = { navController.navigate("scene/${item.id}") })
                    }
                }
            }
        }

        item {
            Text("New Performers", style = MaterialTheme.typography.titleMedium)
            when (performers) {
                is UiState.Success -> {
                    val performerList = (performers as UiState.Success).data
                    val chunked = performerList.chunked((performerList.size + 1) / 2)
                    chunked.forEach { rowItems ->
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(rowItems) { item ->
                                PerformerCard(item, onClick = { navController.navigate("performer/${item.id}") })
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
                is UiState.Loading -> CircularProgressIndicator()
                is UiState.Error -> Text((performers as UiState.Error).message)
            }
        }

        item {
            Text("New Images", style = MaterialTheme.typography.titleMedium)
            when (images) {
                is UiState.Success -> {
                    val imageList = (images as UiState.Success).data
                    val chunked = imageList.chunked((imageList.size + 1) / 2)
                    chunked.forEach { rowItems ->
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(rowItems) { item ->
                                ImageCard(item, onClick = { navController.navigate("image/${item.id}") })
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
                is UiState.Loading -> CircularProgressIndicator()
                is UiState.Error -> Text((images as UiState.Error).message)
            }
        }

        item {
            Text("New Scenes", style = MaterialTheme.typography.titleMedium)
            when (scenes) {
                is UiState.Success -> {
                    val sceneList = (scenes as UiState.Success).data
                    val chunked = sceneList.chunked((sceneList.size + 1) / 2)
                    chunked.forEach { rowItems ->
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(rowItems) { item ->
                                SceneCard(item, onClick = { navController.navigate("scene/${item.id}") })
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
                is UiState.Loading -> CircularProgressIndicator()
                is UiState.Error -> Text((scenes as UiState.Error).message)
            }
        }
    }
}

@Composable
fun SceneCard(scene: SceneItem, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.size(width = 200.dp, height = 120.dp)) {
        Box {
            AsyncImage(
                model = scene.thumbnail,
                contentDescription = scene.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = androidx.compose.ui.graphics.painter.ColorPainter(Color.Gray),
                error = androidx.compose.ui.graphics.painter.ColorPainter(Color.DarkGray),
                onSuccess = {
                    android.util.Log.d("HomeScreen", "Successfully loaded scene thumbnail: ${scene.thumbnail}")
                },
                onError = { error ->
                    android.util.Log.e("HomeScreen", "Failed to load scene thumbnail: ${scene.thumbnail}", error.result.throwable)
                }
            )
            Surface(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)) {
                Text(scene.title, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(4.dp), maxLines = 1)
            }
        }
    }
}

@Composable
fun PerformerCard(performer: PerformerItem, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.size(width = 120.dp, height = 160.dp)) {
        Column {
            AsyncImage(
                model = performer.image,
                contentDescription = performer.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = androidx.compose.ui.graphics.painter.ColorPainter(Color.Gray),
                error = androidx.compose.ui.graphics.painter.ColorPainter(Color.DarkGray),
                onSuccess = {
                    android.util.Log.d("HomeScreen", "Successfully loaded performer image: ${performer.image}")
                },
                onError = { error ->
                    android.util.Log.e("HomeScreen", "Failed to load performer image: ${performer.image}", error.result.throwable)
                }
            )
            Text(performer.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(4.dp), maxLines = 2)
        }
    }
}

@Composable
fun ImageCard(image: ImageItem, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.size(width = 150.dp, height = 150.dp)) {
        AsyncImage(
            model = image.thumbnail,
            contentDescription = image.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            placeholder = androidx.compose.ui.graphics.painter.ColorPainter(Color.Gray),
            error = androidx.compose.ui.graphics.painter.ColorPainter(Color.DarkGray),
            onSuccess = {
                android.util.Log.d("HomeScreen", "Successfully loaded image thumbnail: ${image.thumbnail}")
            },
            onError = { error ->
                android.util.Log.e("HomeScreen", "Failed to load image thumbnail: ${image.thumbnail}", error.result.throwable)
            }
        )
    }
}
