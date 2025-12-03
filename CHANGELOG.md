# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
