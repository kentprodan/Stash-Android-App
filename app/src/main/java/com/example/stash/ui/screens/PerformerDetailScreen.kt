package com.example.stash.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.stash.data.SettingsStore
import com.example.stash.network.GraphqlClient
import com.example.stash.repository.PerformerItem
import com.example.stash.repository.StashRepository
import com.example.stash.ui.viewmodel.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PerformerViewModel(application: Application, private val performerId: String) : AndroidViewModel(application) {
    private val settingsStore = SettingsStore(application)
    private val _performer = MutableStateFlow<UiState<PerformerItem>>(UiState.Loading)
    val performer: StateFlow<UiState<PerformerItem>> = _performer.asStateFlow()

    init {
        loadPerformer()
    }

    private fun loadPerformer() {
        viewModelScope.launch {
            combine(settingsStore.serverUrl, settingsStore.apiKey) { url, key ->
                if (url != null && key != null) {
                    try {
                        val client = GraphqlClient().create(url, key)
                        val repo = StashRepository(client, url, key)
                        val data = repo.performerDetails(performerId)
                        _performer.value = if (data != null) UiState.Success(data) else UiState.Error("Not found")
                    } catch (e: Exception) {
                        _performer.value = UiState.Error(e.message ?: "Unknown error")
                    }
                }
            }.collect()
        }
    }

    fun updateRating(rating: Int) {
        viewModelScope.launch {
            combine(settingsStore.serverUrl, settingsStore.apiKey) { url, key ->
                if (url != null && key != null) {
                    val client = GraphqlClient().create(url, key)
                    val repo = StashRepository(client, url, key)
                    repo.updatePerformer(performerId, rating = rating)
                    loadPerformer()
                }
            }.collect()
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val current = (_performer.value as? UiState.Success)?.data?.favorite ?: false
            combine(settingsStore.serverUrl, settingsStore.apiKey) { url, key ->
                if (url != null && key != null) {
                    val client = GraphqlClient().create(url, key)
                    val repo = StashRepository(client, url, key)
                    repo.updatePerformer(performerId, favorite = !current)
                    loadPerformer()
                }
            }.collect()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformerDetailScreen(navController: NavController, performerId: String) {
    val context = LocalContext.current
    val viewModel: PerformerViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PerformerViewModel(context.applicationContext as Application, performerId) as T
            }
        }
    )
    val performer by viewModel.performer.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (performer is UiState.Success) (performer as UiState.Success).data.name else "Performer") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (performer) {
            is UiState.Success -> {
                val data = (performer as UiState.Success).data
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = data.image,
                        contentDescription = data.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(300.dp)
                    )
                    Text(data.name, style = MaterialTheme.typography.headlineLarge)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Favorite", modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                if (data.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite"
                            )
                        }
                    }

                    Text("Rating: ${data.rating ?: 0}/100", style = MaterialTheme.typography.bodyLarge)
                    Slider(
                        value = (data.rating ?: 0).toFloat(),
                        onValueChange = {},
                        onValueChangeFinished = { viewModel.updateRating((data.rating ?: 0)) },
                        valueRange = 0f..100f,
                        steps = 20
                    )

                    Text("Scenes: ${data.sceneCount}", style = MaterialTheme.typography.bodyLarge)
                    Text("O-Count: ${data.oCounter ?: 0}", style = MaterialTheme.typography.bodyLarge)
                }
            }
            is UiState.Loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is UiState.Error -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text((performer as UiState.Error).message)
            }
        }
    }
}
