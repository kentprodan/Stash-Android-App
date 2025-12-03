# Version History

## Current Version: 0.1.8

### Release Date: December 3, 2025

---

## Version 0.1.8 (Current)
**Release Date:** December 3, 2025

### What's New
- 📏 **Ultra-Thin Seekbar** - Reduced to 4dp for minimal visual footprint
- ⏱️ **Inline Time Display** - Time flanks seekbar on left and right sides
- 🎯 **Horizontal Layout** - All elements aligned in single Row

### Technical Changes
- Changed seekbar height from 8dp to 4dp
- Restructured from Column/Box layout to single Row
- Added weight(1f) modifier to Slider for flexible width
- Positioned times with 8dp spacing between elements
- Maintained vertical center alignment for all components

---

## Version 0.1.7
**Release Date:** December 3, 2025

### What's New
- 📱 **TikTok-Style Layout** - Vertical action buttons on bottom right
- 🎬 **Integrated Time Display** - Time shown with seekbar (left: current, right: total)
- 🎨 **Transparent Controls** - Removed all background boxes for cleaner look
- 📏 **Ultra-Thin Seekbar** - Reduced to 8dp for minimal intrusion
- 👆 **Larger Touch Targets** - Buttons increased to 48dp with 28dp icons

### Technical Changes
- Restructured controls from horizontal to vertical Column layout
- Positioned action buttons with Alignment.BottomEnd
- Moved time display into Box with seekbar
- Removed all Surface backgrounds from controls
- Adjusted spacing: 16dp vertical gap between buttons, 80dp from bottom
- Reduced seekbar height from 12dp to 8dp
- Increased button modifier sizes from 40dp to 48dp
- Increased icon modifier sizes from 20dp to 28dp

---

## Version 0.1.6
**Release Date:** December 3, 2025

### What's New
- 🎬 **Tap to Pause/Play** - Tap center of screen to toggle video playback
- 📊 **Inline Metrics** - O-count and rating numbers now appear next to icons
- 🎨 **Separated Controls** - Time and buttons have distinct background sections
- 📏 **Compact UI** - Reduced control heights to 44dp for cleaner look
- 📉 **Thinner Seekbar** - Reduced from 20dp to 12dp for less intrusion

### Technical Changes
- Added tap gesture detection with pointerInput modifier
- Implemented play/pause state management
- Split control backgrounds into separate Surface components
- Standardized control section heights to 44dp
- Added inline Row layouts for metrics display
- Rating displayed as 1-5 stars (converted from 0-100 scale)

---

## Version 0.1.5
**Release Date:** December 3, 2025

### What's New
- 🎨 **Transparent Overlays** - Removed background colors for cleaner, modern look
- 🔄 **Reorganized Controls** - Time and buttons moved above seekbar
- 📏 **Thinner Seekbar** - Reduced height for less intrusive video controls
- ⭕ **Circular Thumbnails** - Performer avatars now have rounded shape
- 👆 **Tappable Performers** - Click performer thumbnail to view their page

### Technical Changes
- Added CircleShape clip to performer thumbnails
- Added clickable modifier with navigation to performer pages
- Restructured bottom overlay layout hierarchy
- Removed Surface wrapper and background colors from overlays
- Adjusted Slider height modifier for thinner appearance

---

## Version 0.1.4
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
