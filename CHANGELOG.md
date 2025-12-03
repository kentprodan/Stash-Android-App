# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.8] - 2025-12-03

### Changed
- Made seekbar ultra-thin (reduced from 8dp to 4dp)
- Moved time displays inline with seekbar (left: current, right: total)
- Restructured layout from Column to Row for horizontal alignment
- Added weight modifier to seekbar for flexible width

### Improved
- More compact and streamlined progress bar design
- Better visual alignment of time and seekbar
- Minimal UI intrusion with 4dp seekbar height

## [0.1.7] - 2025-12-03

### Changed
- Redesigned Reels controls with TikTok-style vertical button layout
- Moved action buttons to bottom right in vertical stack (O-count, Rating, Details)
- Integrated time display with seekbar (current time on left, total duration on right)
- Removed all semitransparent backgrounds from controls
- Made seekbar thinner (reduced from 12dp to 8dp)
- Increased button sizes to 48dp for better touch targets
- Increased icon sizes to 28dp for better visibility

### Improved
- Cleaner, more modern interface with transparent overlays
- Better use of screen space with vertical button layout
- More intuitive time display integrated with progress bar
- Enhanced visual clarity without background boxes

## [0.1.6] - 2025-12-03

### Added
- Tap-to-pause/play functionality on video player
- O-count number displayed inline next to water drop icon
- Star rating number displayed inline next to star icon (1-5 scale)

### Changed
- Separated time and action buttons into two distinct semitransparent backgrounds
- Made seekbar thinner (reduced from 20dp to 12dp)
- Reduced control section heights to 44dp for more compact UI
- Both control sections now have equal height

### Improved
- Better visual separation of control elements
- More intuitive video playback control with tap gesture
- Cleaner, less intrusive video controls
- Inline metrics display for better readability

## [0.1.5] - 2025-12-03

### Changed
- Removed background colors from Reels screen overlays for cleaner look
- Repositioned time display and action buttons above seekbar
- Made seekbar thinner (reduced height to 20dp)
- Performer thumbnail now circular shape
- Performer thumbnail is now clickable and navigates to performer page

### Improved
- Cleaner, more modern UI with transparent overlays
- Better visual hierarchy with reorganized controls
- Enhanced user interaction with tappable performer avatars

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
