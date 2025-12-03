# Version History

## Current Version: 0.1.4

### Release Date: December 3, 2025

---

## Version 0.1.4 (Current)
**Release Date:** December 3, 2025

### What's New
- 🎭 **Performer Info Display** - Performer thumbnail and name at top left
- ⏯️ **Video Seek Controls** - Interactive progress bar to jump to any position
- ⏱️ **Playback Time** - Current time on left, total duration on right
- 📊 **Real-time Updates** - Position updates every 100ms
- 🎨 **Redesigned Layout** - Action buttons moved to bottom right

### Technical Changes
- Added performers field to FindScenes GraphQL query
- Updated SceneItem data model with performers list
- LaunchedEffect for continuous position tracking
- ExoPlayer seek integration
- Time formatting helper function (formatTime)
- Removed session-based tracking for accurate play counts
- Scene-keyed remember blocks for proper state management

### Bug Fixes
- Videos now count correctly on each view (not just first time)
- Next video in pager properly tracks play count
- Fixed tracking state reset between different videos

---

## Version 0.1.3
**Release Date:** December 3, 2025

### What's New
- 💧 **New O-Count Icon** - Water drop icon replaces trending up icon
- ⭐ **Improved Rating UI** - Interactive 5-star rating dialog
- 🎨 **Visual Polish** - Filled/outlined stars with gold and gray colors
- 👆 **Better Touch Targets** - Larger star icons for easier selection

### Technical Changes
- WaterDrop icon for O-count button
- StarBorder outlined icons for unrated levels
- 40dp star size for improved usability
- Gold (#FFD700) color for filled stars
- Rating dialog shows current rating state

---

## Version 0.1.2
**Release Date:** December 3, 2025

### What's New
- 📊 **Play History Tracking** - Videos automatically increment play count when viewed
- 🎯 **O-Count Management** - Tap to increment O-count with real-time server sync
- ⭐ **5-Star Rating System** - Rate scenes with intuitive dialog interface
- 🔄 **Real-time Updates** - All interactions update both UI and server immediately

### Technical Changes
- GraphQL mutations for scene tracking (play count, O-count, rating)
- Session-based tracking prevents duplicate play counts
- ExoPlayer listener integration for automatic playback tracking
- ReelsViewModel state management for scene updates
- Visual indicators for rated scenes (yellow star icon)

---

## Version 0.1.1
**Release Date:** December 3, 2025

### What's New
- ✅ **Fixed Thumbnails** - Images now load correctly on the home screen
- 🎥 **Video Playback** - Reels screen now plays videos using ExoPlayer
- 🎨 **Visual Feedback** - Added loading placeholders and error states for images
- 🔐 **Authentication** - Fixed API key handling for image/video URLs

### Technical Changes
- Custom Application class with Coil ImageLoader configuration
- API key appended to media URLs as query parameters
- Media3 ExoPlayer integration for video playback
- Enhanced logging for debugging media loading issues

---

## Version 0.1.0
**Release Date:** December 3, 2025

### Initial Release Features
- 🏠 Home screen with content sections
- 🔍 Browse screen with tabs for Scenes, Images, and Performers
- 📱 Reels screen for vertical video scrolling
- ⚙️ Settings screen with server configuration
- 🎨 Material Design 3 with dynamic colors
- 🔌 GraphQL integration with Stash server
- 👤 Performer detail screens
- 🔒 Network security configuration

---

## Version Numbering

This project follows [Semantic Versioning](https://semver.org/):
- **MAJOR.MINOR.PATCH** (e.g., 0.1.1)
- **MAJOR**: Incompatible API changes
- **MINOR**: New features (backwards compatible)
- **PATCH**: Bug fixes (backwards compatible)

### Pre-release (0.x.x)
Versions below 1.0.0 are considered pre-release and may have breaking changes between minor versions.
