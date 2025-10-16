# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.10.0-1.21.10] - 2025-10-16

### Added

- Add support for manual texture overrides for skin, cape, and elytra textures
- New style option: `Pushable`, which allows statues to be pushed around and to enter vehicles such as boats and
  minecarts
- New style option: `Invulnerable`, which prevents breaking the statue entity in survival mode (this was previously tied
  to the sealed option)
- New style option: `Dynamic`, which allows the statue skin to automatically update to the current skin of the set
  player, instead of always keeping the skin from the time it was set

### Changed

- Update to Minecraft 1.21.10
- WARNING: This is a huge rewrite of the straw statue entity which is now based on the new mannequin entity instead of
  armor stands; however, there should be no noticeable differences in-game
- WARNING: After updating test the new mod version on a backup of your world to make sure that your existing straw
  statues do all load correctly in the new version
- The `No Gravity` style option is now called `Immovable` and also prevents moving by water etc.
- Entity rotation and scale options can now be individually reset via a new button that appears only while holding down
  any `Alt` key
- Changing statue rotations is now fully interpolated, so it looks more snappy

### Removed

- Equipping items is no longer possible from in-world by clicking on the statue, you now have to use the menu screen
