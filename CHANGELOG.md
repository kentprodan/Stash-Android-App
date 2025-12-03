# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.4] - 2025-12-03

### Added
- Performer info display at top left of Reels screen (thumbnail + name)
- Video progress bar with seek functionality
- Current playback time display (left corner)
- Total video duration display (right corner)
- Real-time position tracking (updates every 100ms)
- Proper time formatting (MM:SS or H:MM:SS)
- Performers data in GraphQL queries and SceneItem model

### Changed
- Redesigned Reels screen UI layout
- Moved action buttons (O-count, Rating, Details) to bottom right
- Removed session-based play tracking - videos now count each time viewed
- Scene title moved to bottom overlay with video controls
- Action buttons integrated into video control bar

### Fixed
- Videos now count in play history each time they're viewed (removed session tracking)
- Next video in Reels properly tracks play count
- Added scene.id as key to remember blocks for proper state reset

## [0.1.3] - 2025-12-03

### Changed
- O-Count button icon changed from trending up to water drop icon (💧)
- Rating dialog redesigned with 5 interactive stars
- Stars are properly aligned horizontally
- Filled stars (⭐) show gold color for rated levels
- Outlined stars (☆) show gray color for unrated levels
- Larger star icons (40dp) for better touch targets
- Rating dialog now shows current rating when opened

### Improved
- Better visual feedback for rating selection
- More intuitive rating interface
- Clearer rating state indication

## [0.1.2] - 2025-12-03

### Added
- Play history tracking - Videos automatically increment play count on Stash server when playback starts
- O-count increment functionality - Tap button to increment O-count with real-time UI updates
- O-count reset functionality - Reset O-count to 0 via server mutation
- 5-star rating system in Reels screen with dialog interface
- GraphQL mutations for scene tracking (SceneIncrementPlayCount, SceneIncrementO, SceneResetO, SceneUpdate)
- Visual feedback for rated scenes (yellow star icon)
- Session-based play tracking to prevent duplicate counts

### Changed
- Star icon changes color when scene is rated
- Rating display shows stars instead of just percentage
- ReelsViewModel now manages scene state updates for O-count and rating
- ReelItem now tracks video playback state with ExoPlayer listener

## [0.1.1] - 2025-12-03

### Added
- Custom Application class with Coil ImageLoader configuration
- API key authentication for image loading
- Video playback support in Reels screen using ExoPlayer
- Visual placeholders (gray) for loading images
- Error state indicators (dark gray) for failed image loads
- Comprehensive logging for image/video loading debugging
- Media3 ExoPlayer dependency for video playback

### Fixed
- Thumbnails now display correctly on Home screen (scenes, performers, images)
- Fixed authentication issue by appending API key to image URLs
- Reels screen now plays videos instead of showing static thumbnails

### Changed
- Updated `StashRepository.fullUrl()` to append API key as query parameter
- Enhanced all AsyncImage components with placeholder and error states
- `SceneItem` data class now includes `streamUrl` field
- ReelsScreen refactored to use ExoPlayer for video playback

## [0.1.0] - 2025-12-03

### Added
- Initial release
- Material Design 3 Expressive theme with dynamic colors
- Home screen with sections for Continue Watching, New Performers, New Images, and New Scenes
- Browse screen with tabs for Scenes, Images, and Performers
- Reels screen for vertical scrolling video content
- Settings screen with server configuration and stats
- GraphQL integration with Stash server
- Apollo Client for API communication
- Navigation between screens
- Performer detail screen with rating and favorite functionality
- Network security configuration for cleartext traffic
