package com.example.stash.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stash.data.SettingsStore
import com.example.stash.network.GraphqlClient
import com.example.stash.repository.ServerStats
import com.example.stash.repository.StashRepository
import com.example.stash.repository.VersionInfo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsStore = SettingsStore(application)
    val serverUrl: StateFlow<String?> = settingsStore.serverUrl.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val apiKey: StateFlow<String?> = settingsStore.apiKey.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _stats = MutableStateFlow<UiState<ServerStats>>(UiState.Loading)
    val stats: StateFlow<UiState<ServerStats>> = _stats.asStateFlow()

    private val _versionInfo = MutableStateFlow<UiState<VersionInfo>>(UiState.Loading)
    val versionInfo: StateFlow<UiState<VersionInfo>> = _versionInfo.asStateFlow()

    init {
        loadStats()
        loadVersionInfo()
    }

    private fun loadStats() {
        viewModelScope.launch {
            combine(serverUrl, apiKey) { url, key ->
                if (url != null && key != null) {
                    try {
                        val client = GraphqlClient().create(url, key)
                        val repo = StashRepository(client, url, key)
                        _stats.value = UiState.Success(repo.stats())
                    } catch (e: Exception) {
                        android.util.Log.e("SettingsViewModel", "Error loading stats", e)
                        _stats.value = UiState.Error("${e.javaClass.simpleName}: ${e.message}")
                    }
                }
            }.collect()
        }
    }

    private fun loadVersionInfo() {
        viewModelScope.launch {
            combine(serverUrl, apiKey) { url, key ->
                if (url != null && key != null) {
                    try {
                        val client = GraphqlClient().create(url, key)
                        val repo = StashRepository(client, url, key)
                        _versionInfo.value = UiState.Success(repo.versionInfo())
                    } catch (e: Exception) {
                        android.util.Log.e("SettingsViewModel", "Error loading version info", e)
                        _versionInfo.value = UiState.Error("${e.javaClass.simpleName}: ${e.message}")
                    }
                }
            }.collect()
        }
    }
}
