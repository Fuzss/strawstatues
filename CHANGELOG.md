# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v4.0.13-1.19.2] - 2024-09-21
### Fixed
- Fix custom `EntityDataSerializer` id mismatch on Forge (`io.netty.handler.codec.DecoderException: Unknown serializer type`)

## [v4.0.12-1.19.2] - 2023-07-26
### Added
- Added two buttons for aligning the statue on a block to the position screen
- Added a button to the rotations screen for mirroring the current pose
- Added many new poses from the Vanilla Tweaks Armor Statues data pack
- Added the ability to switch between tabs by pressing hotbar keys
### Fixed
- Fixed left and right arm and leg parts being swapped on the rotations screen
- Fixed an issue where new rotations set on the rotations screen wouldn't save if the sliders were moved using the arrow keys

## [v4.0.11-1.19.2] - 2023-07-24
### Added
- Added a new configuration tab for configuring x- and z- rotations of a statue, y-rotation and scale have been moved here
### Changed
- New statue item icon!
- Adjusted the statue entity size to match players instead of armor stands
- Text fields now show description tooltips when hovered to make it more clear which field is for setting a new display name and which one changes the statue skin
- Opening the statue menu no longer requires a stick to be held, instead shift + right-click with an empty hand is the way to go, which the statue item tooltip reflects
- A new statue skin can now easily be applied by equipping the statue with a configured player head
### Fixed
- Fixed an occasional start-up crash on Forge related to registering the straw statue dispense behavior

## [v4.0.10-1.19.2] - 2023-01-21
### Added
- Added an option in the configuration screen to adjust the scale of statues
- Added an option in the style screen to make the statue look as if it where crouching
### Changed
- Refined the default straw statue texture

## [v4.0.9-1.19.2] - 2023-01-17
### Changed
- Opening the statue menu now requires a stick to be held in addition to sneaking, this was changed to improve compatibility with the Quark mod
- Removed the option to cycle through statue menu tabs using the tab key, it was conflicting with showing the vanilla server player list (at least on Fabric)
- Instead, you can now scroll through the tabs when your cursor is hovering them

## [v4.0.8-1.19.2] - 2022-12-05
### Changed
- The statue menu no longer opens when a new statue is placed down to fix a rare issue where the menu would open on the remote before the statue entity itself was synced
- Instead, statues are now placed down with a random pose, editing (opening the menu) is only possible via shift + right-clicking the placed statue afterwards
- Hold shift while placing down the statue to prevent a random pose from being applied

## [v4.0.7-1.19.2] - 2022-12-05
### Changed
- Improved exception message when no statue entity is found to create a screen for

## [v4.0.6-1.19.2] - 2022-12-05
### Fixed
- Prevent client crash when trying to open statue screen, but no statue entity is present
- Instead, an exception will be thrown and caught immediately, and no screen will open
- The cause for the entity missing is currently unknown, so this seems like the best work around right now

## [v4.0.5-1.19.2] - 2022-11-30
### Fixed
- Fixed statue skin failing to save, therefore being reset upon every world reload

## [v4.0.4-1.19.2] - 2022-11-22
### Changed
- New texture for Straw Statue item
- Display name and statue skin are now independent, set a name using the text box on the style screen, on the model parts screen enter a player name for the statue to show their skin
### Fixed
- Fixed an issue where long player names (above 16 chars) would crash the game when trying to retrieve their skin

## [v4.0.3-1.19.2] - 2022-10-22
### Fixed
- Fixed crash on start-up when the [Armor Statues] mod is installed

## [v4.0.2-1.19.2] - 2022-10-19
### Changed
- Any item can now be placed into the head slot on the equipment screen (thanks to [Mephodio])
- Tooltips on the rotations screen will no longer obstruct the armor stand model (thanks to [Mephodio])
- Tooltips on the style screen are now split into multiple lines to prevent them from flowing off the screen

## [v4.0.1-1.19.2] - 2022-10-11
### Fixed
- Hopefully fixed occasional client crash when renaming straw statue on a server

## [v4.0.0-1.19.2] - 2022-09-22
- Initial release

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
[Mephodio]: https://github.com/Mephodio
[Armor Statues]: https://www.curseforge.com/minecraft/mc-mods/armor-statues
