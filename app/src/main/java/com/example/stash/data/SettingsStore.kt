package com.example.stash.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "stash_settings")

class SettingsStore(private val context: Context) {
    private val KEY_SERVER_URL = stringPreferencesKey("server_url")
    private val KEY_API_KEY = stringPreferencesKey("api_key")

    val serverUrl: Flow<String?> = context.dataStore.data.map { it[KEY_SERVER_URL] }
    val apiKey: Flow<String?> = context.dataStore.data.map { it[KEY_API_KEY] }

    suspend fun save(serverUrl: String, apiKey: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SERVER_URL] = serverUrl.trimEnd('/')
            prefs[KEY_API_KEY] = apiKey
        }
    }
}
