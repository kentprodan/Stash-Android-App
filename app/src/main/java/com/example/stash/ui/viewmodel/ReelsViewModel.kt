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

    suspend fun addTagToScene(sceneId: String, currentTags: List<com.example.stash.repository.TagItem>, newTag: com.example.stash.repository.TagItem) {
        android.util.Log.d("ReelsViewModel", "addTagToScene: sceneId=$sceneId, currentTags=${currentTags.map { it.name }}, newTag=${newTag.name}")
        val updatedTags = (currentTags + newTag).distinctBy { it.id }
        android.util.Log.d("ReelsViewModel", "addTagToScene: updatedTags=${updatedTags.map { it.name }}")
        val success = repository?.updateSceneTags(sceneId, updatedTags.map { it.id })
        android.util.Log.d("ReelsViewModel", "addTagToScene: server update success=$success")
        if (success == true) {
            updateSceneInList(sceneId) { it.copy(tags = updatedTags) }
            android.util.Log.d("ReelsViewModel", "Added tag to scene: $sceneId, tags now: ${updatedTags.map { it.name }}")
        } else {
            android.util.Log.e("ReelsViewModel", "Failed to update tags on server for scene: $sceneId")
        }
    }

    suspend fun createAndAddTag(sceneId: String, currentTags: List<com.example.stash.repository.TagItem>, tagName: String) {
        android.util.Log.d("ReelsViewModel", "createAndAddTag called: sceneId=$sceneId, tagName=$tagName, currentTags=${currentTags.map { it.name }}")
        try {
            var tagToAdd = repository?.createTag(tagName)
            android.util.Log.d("ReelsViewModel", "createTag returned: ${tagToAdd?.name ?: "null"}")
            
            // If tag creation failed (likely because it already exists), try to find it
            if (tagToAdd == null) {
                android.util.Log.d("ReelsViewModel", "Tag creation failed, searching for existing tag with name: $tagName")
                val allTags = repository?.allTags() ?: emptyList()
                tagToAdd = allTags.find { it.name.equals(tagName, ignoreCase = true) }
                android.util.Log.d("ReelsViewModel", "Found existing tag: ${tagToAdd?.name ?: "not found"}")
            }
            
            if (tagToAdd != null) {
                android.util.Log.d("ReelsViewModel", "Adding tag to scene: ${tagToAdd.name}")
                addTagToScene(sceneId, currentTags, tagToAdd)
                android.util.Log.d("ReelsViewModel", "addTagToScene completed")
            } else {
                android.util.Log.e("ReelsViewModel", "Failed to create or find tag: $tagName")
            }
        } catch (e: Exception) {
            android.util.Log.e("ReelsViewModel", "Exception in createAndAddTag", e)
        }
    }

    suspend fun removeTagFromScene(sceneId: String, currentTags: List<com.example.stash.repository.TagItem>, tagToRemove: com.example.stash.repository.TagItem) {
        val updatedTags = currentTags.filter { it.id != tagToRemove.id }
        val success = repository?.updateSceneTags(sceneId, updatedTags.map { it.id })
        if (success == true) {
            updateSceneInList(sceneId) { it.copy(tags = updatedTags) }
            android.util.Log.d("ReelsViewModel", "Removed tag from scene: $sceneId")
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
