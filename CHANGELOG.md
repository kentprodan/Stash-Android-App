# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

# Changelog

All notable changes to this project will be documented in this file.

## [0.1.19] - 2025-12-03
### Changed
- Fixed play count tracking to prevent counting replays
- Fixed play count display in info panel to increment correctly on multiple views
- Added divider between Video Information and Play Statistics sections
- Replaced Total Play Time with Frame Rate in info panel

## [0.1.18] - 2025-12-03
### Added
- Added search functionality to tag input field
- Available tags list now filters in real-time as you type
- Shows "No matching tags found" when search has no results

## [0.1.17] - 2024-12-03
### Changed
- Made available tags list scrollable in Add Tag dialog
- Available tags list now shows all tags from server instead of limiting to 11
- Added vertical scroll with 300dp max height for better UX

## [0.1.16] - 2024-12-03
### Changed
- Optimized tag width and layout in Reels Info sheet
- Tags now use wrap content width instead of equal spacing
- Added horizontal scroll for tags when they exceed screen width

### Technical
- Tags use natural width with horizontal scroll
- Removed weight modifiers for dynamic sizing

## [0.1.15] - 2025-12-03

### Added
- Tag removal: X button on each tag to remove it from the scene
- All tags now displayed (removed 6-tag limit)
- Tags wrap in rows of 2 for better layout

### Fixed
- Typing existing tag name now properly adds it to scene
- Duplicate tag handling: searches for existing tag if creation fails
- Enhanced logging for tag operations debugging

### Technical
- `removeTagFromScene` suspend function for tag deletion
- Fallback to tag search when creation fails (handles duplicates)
- Comprehensive logging throughout tag operations
- Tags displayed in Column with chunked rows

## [0.1.14] - 2025-12-03

### Fixed
- Tag creation now properly waits for server response before closing dialog
- New tags now appear immediately after creation
- Tag addition operations now complete before UI updates

### Technical
- Converted `addTagToScene` and `createAndAddTag` to suspend functions
- UI now uses coroutine scope to wait for tag operations to complete
- Dialog closes only after successful tag creation and addition

## [0.1.13] - 2025-12-03

### Added
- Tag creation: create new tags on-the-fly from tag addition dialog
- Live tag updates: newly added tags appear immediately in Info sheet

### Fixed
- Tag list now loads all tags from server instead of limiting to first page
- Tag dialog shows all available tags, not just first 11
- New tags now display immediately after creation without closing sheet

### Technical
- `TagCreate` mutation with `createTag` repository method
- `createAndAddTag` ViewModel method for creating and adding tags
- SceneDetailsSheet observes live scene state for tag updates
- FindTags query updated to use `per_page = -1` and include count field

## [0.1.12] - 2025-12-03

### Added
- Live play stats in Info sheet: play count and total play time update while watching
- Replay counting: each video loop increments play count
- Server-side play duration sync: accumulated watch time persisted on page away
- Add tag feature: tap + button in Tags section to add tags to scene

### Changed
- Info sheet: Performers section moved above Video Information
- Tags section has dedicated divider and add button

### Technical
- Session watch time tracking with loop handling
- `updateScenePlayDuration` mutation on ReelItem disposal
- `FindTags` query and `updateSceneTags` repository method
- ViewModel methods: `fetchAllTags`, `addTagToScene`

## [0.1.11] - 2025-12-03

### Changed
- Info sheet: added icons to all sections and labels
- Reordered Play Statistics: Rating & O-Count now above totals
- Totals row shows Total Play Time first, then Play Count
- Resolution displays friendly label plus raw dimensions (e.g., Full HD (1920×1080))

### Technical
- Replaced deprecated `Divider` with `HorizontalDivider`
- Switched to `Icons.AutoMirrored.Filled.Label` for Tags
- Populated Reels scenes with width/height/play stats/tags in repository

## [0.1.10] - 2025-12-03

### Fixed
- Reels: ensure only the active page plays audio/video
- Pause previous ExoPlayer when paging to the next item

### Technical
- Added `isActive` coordination between pager and `ReelItem`
- `LaunchedEffect` pauses non-active players to prevent audio bleed

## [0.1.9] - 2025-12-03

### Added
- Comprehensive Reels Info sheet with detailed scene metadata
- Video resolution (width × height) display
- Play statistics: play count and total play duration
- Performers list with avatars and navigation
- Tags chips display (up to 6)

### Changed
- Replaced old info sheet with structured sections (Video Info, Play Stats, Rating & O-Count, Performers, Tags)
- Star rating now shown with visual stars and numeric value

### Improved
- Clearer, richer scene details accessible from Reels Info button
- Faster navigation to performer and scene pages

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
