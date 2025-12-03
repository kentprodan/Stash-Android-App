package com.example.stash.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stash.data.SettingsStore
import com.example.stash.network.GraphqlClient
import com.example.stash.repository.SceneItem
import com.example.stash.repository.StashRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReelsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsStore = SettingsStore(application)
    private val _scenes = MutableStateFlow<UiState<List<SceneItem>>>(UiState.Loading)
    val scenes: StateFlow<UiState<List<SceneItem>>> = _scenes.asStateFlow()

    init {
        loadScenes()
    }

    private fun loadScenes() {
        viewModelScope.launch {
            combine(settingsStore.serverUrl, settingsStore.apiKey) { url, key ->
                if (url != null && key != null) {
                    try {
                        val client = GraphqlClient().create(url, key)
                        val repo = StashRepository(client, url, key)
                        _scenes.value = UiState.Success(repo.reelsRandom(50))
                    } catch (e: Exception) {
                        android.util.Log.e("ReelsViewModel", "Error loading scenes", e)
                        _scenes.value = UiState.Error("${e.javaClass.simpleName}: ${e.message}")
                    }
                }
            }.collect()
        }
    }

    fun refresh() {
        loadScenes()
    }
}
