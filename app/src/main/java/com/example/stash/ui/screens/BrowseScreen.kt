package com.example.stash.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BrowseScreen(navController: NavController) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Scenes", "Images", "Performers")
    TabRow(selectedTabIndex = tabIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = { Text(title) })
        }
    }
    LazyVerticalGrid(columns = GridCells.Adaptive(160.dp), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp)) {
        when (tabIndex) {
            0 -> items((0 until 20).toList()) { _ -> ElevatedCard { Text("Scene") } }
            1 -> items((0 until 20).toList()) { _ -> ElevatedCard { Text("Image") } }
            2 -> items((0 until 20).toList()) { _ -> ElevatedCard { Text("Performer") } }
        }
    }
}
