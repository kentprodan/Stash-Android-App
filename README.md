# Stash Android Client

A modern Android client for the self-hosted [Stash media server](https://github.com/stashapp/stash), built with Jetpack Compose and Material Design 3 Expressive.

## Features

### 📱 Material Design 3 Expressive
- **Dynamic Color**: Adapts to your device's wallpaper (Android 12+) with purple fallback
- **Bold Typography**: Large display and headline styles for impact
- **Rounded Shapes**: Medium-to-large corner radius for a modern, friendly feel
- **NavigationBar**: Bottom navigation with pill-shaped active indicators

### 🏠 Home Screen
- **Continue Watching**: Resume your viewing (placeholder for now)
- **New Performers**: Latest performers with profile images
- **New Images**: Grid of recently added images
- **New Scenes**: Latest scene thumbnails with titles

### 🔍 Browse
- **Tabbed Interface**: Switch between Scenes, Images, and Performers
- **Adaptive Grid**: LazyVerticalGrid with responsive sizing
- **Performer Details**: View performer info, edit rating (0-100), toggle favorite, see scene count and O-count

### 🎬 Reels
- **TikTok-style Pager**: VerticalPager for swipeable scene browsing
- **Random Order**: Scenes fetched in random order for discovery
- **Overlay Controls**: Right-aligned column with O-Count, 5-star rating, and Details buttons

### ⚙️ Settings
- **Connection Status**: Badge showing server connectivity
- **Server Stats**: Total scenes, images, performers, playtime, and O-count
- **About Section**: App info

### 🔐 Onboarding
- **Server Configuration**: Input Server URL and API Key
- **Persistent Storage**: Settings saved via DataStore
- **Auto-navigation**: Redirects to Home after successful setup

## Tech Stack

- **Jetpack Compose**: Modern declarative UI
- **Material 3**: Latest Material Design with expressive theming
- **Apollo GraphQL**: Type-safe API client for Stash GraphQL endpoint
- **Coil**: Image loading and caching
- **Navigation Compose**: Type-safe navigation with arguments
- **DataStore**: Key-value storage for settings
- **Coroutines & Flow**: Async operations and reactive streams
- **ViewModel**: UI state management with lifecycle awareness

## Architecture

```
app/
├── data/              # DataStore settings persistence
├── network/           # Apollo GraphQL client configuration
├── repository/        # Data layer with StashRepository
├── ui/
│   ├── screens/       # Composable screens (Home, Browse, Reels, Settings, Details)
│   ├── theme/         # Material 3 theme configuration
│   └── viewmodel/     # ViewModels for state management
└── graphql/           # GraphQL query definitions
```

## Setup & Build

### Prerequisites
- Android Studio Iguana or later
- JDK 11+
- Android SDK 36 (or modify `minSdk` in `app/build.gradle.kts`)

### Build Commands

```bash
# Build debug APK
./gradlew :app:assembleDebug

# Install to connected device
./gradlew :app:installDebug

# Generate Apollo GraphQL types
./gradlew :app:generateApolloSources
```

### Configuration

1. Launch the app
2. On the onboarding screen, enter:
   - **Server URL**: Your Stash server address (e.g., `http://192.168.1.100:9999`)
   - **API Key**: Generate from Stash Settings → Security → API Keys
3. Tap **Continue** to save and navigate to Home

## GraphQL Queries

The app uses the following queries defined in `app/src/main/graphql/`:
- `FindScenes.graphql`: Fetch scenes with metadata
- `FindImages.graphql`: Fetch images
- `FindPerformers.graphql`: Fetch performers list
- `FindPerformer.graphql`: Fetch single performer details
- `GetStats.graphql`: Fetch server statistics
- `PerformerUpdate.graphql`: Mutation to update performer rating/favorite

Apollo code generation runs automatically during build.

## Future Enhancements

- **Video Player**: Integrate ExoPlayer for scene playback
- **Continue Watching**: Track playback history
- **Advanced Filtering**: Search, sort, and filter options
- **Offline Support**: Cache content for offline viewing
- **Paging**: Implement paging for large datasets
- **Shimmer Placeholders**: Loading states with shimmer effect
- **Scene Markers**: Timeline markers and chapters
- **Gallery Details**: Full metadata and tag browsing
- **Performer Stats**: Extended analytics and graphs
- **Migrate to AndroidX Pager**: Replace deprecated Accompanist pager

## Known Issues

- Accompanist Pager is deprecated; migration to `androidx.compose.foundation.pager` pending
- Continue Watching is a placeholder (requires playback history tracking)
- Scene/Image detail screens are minimal (full implementation pending)

## License

This project is provided as-is for educational and personal use.

## Contributing

This is a prototype. Contributions, suggestions, and feedback are welcome!

---

Built with ❤️ for the Stash community.
