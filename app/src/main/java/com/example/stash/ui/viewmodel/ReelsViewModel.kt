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
        viewModelScope.launch {
            repository?.incrementScenePlayCount(sceneId)
            android.util.Log.d("ReelsViewModel", "Incremented play count for scene: $sceneId")
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

    fun appendPlayDuration(sceneId: String, baseSeconds: Double, additionalSeconds: Double) {
        viewModelScope.launch {
            val total = baseSeconds + additionalSeconds
            val success = repository?.updateScenePlayDuration(sceneId, total)
            if (success == true) {
                updateSceneInList(sceneId) { it.copy(playDuration = total) }
                android.util.Log.d("ReelsViewModel", "Updated play duration for scene: $sceneId to ${total}s")
            }
        }
    }

    suspend fun fetchAllTags(): List<com.example.stash.repository.TagItem> {
        return repository?.allTags() ?: emptyList()
    }

    fun addTagToScene(sceneId: String, currentTags: List<com.example.stash.repository.TagItem>, newTag: com.example.stash.repository.TagItem) {
        viewModelScope.launch {
            val updatedTags = (currentTags + newTag).distinctBy { it.id }
            val success = repository?.updateSceneTags(sceneId, updatedTags.map { it.id })
            if (success == true) {
                updateSceneInList(sceneId) { it.copy(tags = updatedTags) }
                android.util.Log.d("ReelsViewModel", "Added tag to scene: $sceneId")
            }
        }
    }

    fun createAndAddTag(sceneId: String, currentTags: List<com.example.stash.repository.TagItem>, tagName: String) {
        viewModelScope.launch {
            val newTag = repository?.createTag(tagName)
            if (newTag != null) {
                android.util.Log.d("ReelsViewModel", "Tag created successfully: ${newTag.name}")
                addTagToScene(sceneId, currentTags, newTag)
            } else {
                android.util.Log.e("ReelsViewModel", "Failed to create tag: $tagName")
            }
        }
    }

    private fun updateSceneInList(sceneId: String, update: (SceneItem) -> SceneItem) {
        val currentState = _scenes.value
        if (currentState is UiState.Success) {
            val updatedScenes = currentState.data.map { scene ->
                if (scene.id == sceneId) update(scene) else scene
            }
            _scenes.value = UiState.Success(updatedScenes)
        }
    }

    fun refresh() {
        loadScenes()
    }
}
