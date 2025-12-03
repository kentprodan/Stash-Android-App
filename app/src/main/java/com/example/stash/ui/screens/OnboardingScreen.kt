package com.example.stash.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stash.data.SettingsStore
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(navController: NavController, settingsStore: SettingsStore) {
    var url by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "Connect to Stash", style = MaterialTheme.typography.headlineLarge)
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Server URL") },
            placeholder = { Text("http://localhost:9999") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("API Key") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (url.isNotBlank() && apiKey.isNotBlank()) {
                    scope.launch {
                        isSaving = true
                        try {
                            settingsStore.save(url, apiKey)
                            navController.navigate("home") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        } finally { isSaving = false }
                    }
                }
            },
            enabled = !isSaving && url.isNotBlank() && apiKey.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSaving) "Saving…" else "Continue")
        }
    }
}
