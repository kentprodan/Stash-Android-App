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
    
    private var repository: StashRepository? = null
    private val trackedScenes = mutableSetOf<String>()

    init {
        loadScenes()
    }

    private fun loadScenes() {
        viewModelScope.launch {
            combine(settingsStore.serverUrl, settingsStore.apiKey) { url, key ->
                if (url != null && key != null) {
                    try {
                        val client = GraphqlClient().create(url, key)
                        repository = StashRepository(client, url, key)
                        _scenes.value = UiState.Success(repository!!.reelsRandom(50))
                    } catch (e: Exception) {
                        android.util.Log.e("ReelsViewModel", "Error loading scenes", e)
                        _scenes.value = UiState.Error("${e.javaClass.simpleName}: ${e.message}")
                    }
                }
            }.collect()
        }
    }

    fun incrementPlayCount(sceneId: String) {
        if (trackedScenes.contains(sceneId)) {
            android.util.Log.d("ReelsViewModel", "Scene $sceneId already tracked in this session")
            return
        }
        
        viewModelScope.launch {
            repository?.incrementScenePlayCount(sceneId)
            trackedScenes.add(sceneId)
        }
    }

    fun incrementOCount(sceneId: String) {
        viewModelScope.launch {
            val newCount = repository?.incrementSceneOCount(sceneId)
            if (newCount != null) {
                updateSceneInList(sceneId) { it.copy(oCount = newCount) }
            }
        }
    }

    fun resetOCount(sceneId: String) {
        viewModelScope.launch {
            val newCount = repository?.resetSceneOCount(sceneId)
            if (newCount != null) {
                updateSceneInList(sceneId) { it.copy(oCount = newCount) }
            }
        }
    }

    fun updateRating(sceneId: String, rating: Int) {
        viewModelScope.launch {
            val success = repository?.updateSceneRating(sceneId, rating)
            if (success == true) {
                updateSceneInList(sceneId) { it.copy(rating = rating) }
            }
        }
    }

    private fun updateSceneInList(sceneId: String, update: (SceneItem) -> SceneItem) {
        val currentState = _scenes.value
        if (currentState is UiState.Success) {
            val updatedList = currentState.data.map { scene ->
                if (scene.id == sceneId) update(scene) else scene
            }
            _scenes.value = UiState.Success(updatedList)
        }
    }

    fun refresh() {
        trackedScenes.clear()
        loadScenes()
    }
}
