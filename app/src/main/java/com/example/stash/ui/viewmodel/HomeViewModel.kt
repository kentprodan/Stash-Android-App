package com.example.stash.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stash.data.SettingsStore
import com.example.stash.network.GraphqlClient
import com.example.stash.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsStore = SettingsStore(application)
    private val _continueWatching = MutableStateFlow<UiState<List<SceneItem>>>(UiState.Loading)
    val continueWatching: StateFlow<UiState<List<SceneItem>>> = _continueWatching.asStateFlow()

    private val _performers = MutableStateFlow<UiState<List<PerformerItem>>>(UiState.Loading)
    val performers: StateFlow<UiState<List<PerformerItem>>> = _performers.asStateFlow()

    private val _images = MutableStateFlow<UiState<List<ImageItem>>>(UiState.Loading)
    val images: StateFlow<UiState<List<ImageItem>>> = _images.asStateFlow()

    private val _scenes = MutableStateFlow<UiState<List<SceneItem>>>(UiState.Loading)
    val scenes: StateFlow<UiState<List<SceneItem>>> = _scenes.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(settingsStore.serverUrl, settingsStore.apiKey) { url, key ->
                if (url != null && key != null) {
                    val client = GraphqlClient().create(url, key)
                    val repo = StashRepository(client, url, key)
                    try {
                        _performers.value = UiState.Success(repo.newPerformers(10))
                        _images.value = UiState.Success(repo.newImages(10))
                        _scenes.value = UiState.Success(repo.newScenes(10))
                        _continueWatching.value = UiState.Success(repo.continueWatching())
                    } catch (e: Exception) {
                        android.util.Log.e("HomeViewModel", "Error loading data", e)
                        val errorMsg = "${e.javaClass.simpleName}: ${e.message}"
                        _performers.value = UiState.Error(errorMsg)
                        _images.value = UiState.Error(errorMsg)
                        _scenes.value = UiState.Error(errorMsg)
                        _continueWatching.value = UiState.Error(errorMsg)
                    }
                }
            }.collect()
        }
    }
}
