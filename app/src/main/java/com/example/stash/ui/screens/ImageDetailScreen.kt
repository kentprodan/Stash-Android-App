package com.example.stash.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.stash.repository.ImageItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailScreen(navController: NavController, imageId: String, viewModel: com.example.stash.ui.viewmodel.HomeViewModel) {
    val imagesState = viewModel.images.collectAsState()
    val images = imagesState.value
    val image = when (images) {
        is com.example.stash.ui.viewmodel.UiState.Success -> (images as com.example.stash.ui.viewmodel.UiState.Success<List<ImageItem>>).data.find { it.id == imageId }
        else -> null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(image?.title ?: "Image") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (image?.thumbnail != null) {
                AsyncImage(
                    model = image.thumbnail,
                    contentDescription = image.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Image not found or missing thumbnail.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
