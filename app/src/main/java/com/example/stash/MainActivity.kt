package com.example.stash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stash.ui.theme.StashTheme
import com.example.stash.ui.screens.*
import com.example.stash.data.SettingsStore
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsStore = SettingsStore(this)
            val themeMode by settingsStore.themeMode.collectAsState(initial = "system")
            val darkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            StashTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                val serverUrl by settingsStore.serverUrl.collectAsState(initial = null)
                val apiKey by settingsStore.apiKey.collectAsState(initial = null)
                val items = listOf(
                    NavItem("home", Icons.Default.Home, "Home"),
                    NavItem("browse", Icons.Default.Search, "Browse"),
                    NavItem("reels", Icons.Default.PlayArrow, "Reels"),
                    NavItem("settings", Icons.Default.Settings, "Settings")
                )
                val homeViewModel: com.example.stash.ui.viewmodel.HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route
                            items.forEach { item ->
                                NavigationBarItem(
                                    selected = currentRoute == item.route,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                    label = { Text(item.label) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    val start = if (serverUrl.isNullOrBlank() || apiKey.isNullOrBlank()) "onboarding" else "home"
                    NavHost(navController, startDestination = start, modifier = Modifier.padding(innerPadding)) {
                        composable("onboarding") { OnboardingScreen(navController, settingsStore) }
                        composable("home") { HomeScreen(navController, viewModel = homeViewModel) }
                        composable("browse") { BrowseScreen(navController) }
                        composable("reels") { ReelsScreen(navController) }
                        composable("settings") { SettingsScreen(navController) }
                        composable(
                            "scene/{sceneId}",
                            arguments = listOf(navArgument("sceneId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            SceneDetailScreen(navController, backStackEntry.arguments?.getString("sceneId") ?: "")
                        }
                        composable(
                            "image/{imageId}",
                            arguments = listOf(navArgument("imageId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            ImageDetailScreen(navController, backStackEntry.arguments?.getString("imageId") ?: "", viewModel = homeViewModel)
                        }
                        composable(
                            "performer/{performerId}",
                            arguments = listOf(navArgument("performerId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            PerformerDetailScreen(navController, backStackEntry.arguments?.getString("performerId") ?: "")
                        }
                    }
                }
            }
        }
    }
}

data class NavItem(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String)