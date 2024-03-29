## Changelog
#### v0.2.5
- UI improvements and changes
  - added glow effect to animation and scene buttons, which provides a preview of the effect
  - redesigned settings UI
  - new theme selector
  - improved UI style
  - added side menu animation
  - added French translation (thanks Pat'o beurre)
  - added basic touch-scroll support (experimental)
  - inverted speed and sensitivity slider (right ≙ faster/more sensitive)
- added color palettes to Color menu
- added output preview panel to Tools menu
- added E1.31 output protocol
- added settings interface for Lua scripts
- added Gradient and Sawtooth animation
- added new modes to Visualizer, Energy and Rainbow music effects
- improved several animations
- added REST API
- various other bug fixes and improvements

#### v0.2.4
- minor UI fixes
- improved ScreenColor
  - the width and x-posiotion of the scan area can now be adjusted
  - added brightness threshold and saturation setting
- effect options (Animations & MusicSync) that are currently not applied are now hidden
- added Explosions music visualizer
- added custom color presets for Energy music visualizer
- added check for updates when clicking on version tag in About panel

#### v0.2.4-rc2
- minor UI fixes and improvements
  - fixed window size sometimes not saving
  - fixed output Add-popup-menu background
  - added setting to customize the UI font and font size
  - fixed notification overlaps scrollbar
  - other minor fixes like text color
- fixed data file not completely saved to disk (issue #10)
- fixed MusicSync error when no sound input is selected (issue #11)
- added virtual output so that e.g. plugins can forward output data
- added Multi output ([learn more](https://github.com/Drumber/RemoteLight/wiki/MultiOutput))
- fixed Chain output ([learn more](https://github.com/Drumber/RemoteLight/wiki/Chain))

#### v0.2.4-rc1
- added new rainbow modes to Rainbow animation
- added plugin system for java plugins
- code related changes
  - improved project structure
  - added event handler
  - code optimization and clean up
- added Tools panel
  - added custom console window
  - added notification history
  - added plugins overview
- UI optimization
  - sidebar is now scrollable
  - fixed some UI bugs
  - fixed pending notifications not shown after start up
  - added splash screen to indicate that the program is starting
- added option to disable update notification for pre-releases
- soft fixed data file saving issues (please report any bug)

#### v0.2.3
- small bug fixes
- added `color` command
- added saving for last shown color
- updated dependencies (added local maven repository)

#### v0.2.2
- reworked data storage (data is now stored as JSON)
- added auto save
- added missing translations
- updated to [FlatLaf 0.37](https://github.com/JFormDesigner/FlatLaf/releases/tag/0.37)
  - added custom window decorations support
- small bug fixes and improvements

#### v0.2.1
- added 2 new music visualizer (RainbowNoise & Lines)
- improved FFT computation for Visualizer effect
- added new animation (SortAlgo [8 sorting algorithms so far])
- added always-on-top option to simulator window
- added new UI icons
- save side menu expand state
- fixed native sound bug
- added simple command system (available cmds: start, stop, list, close)
- added auto enable last active effect option
- added new notification system (shows notifications in the lower right corner)
- UI and backend improvements

#### pre0.2.0.9
- new ColorPicker
- added a lot of new Look And Feels ([FlatLaf](https://github.com/JFormDesigner/FlatLaf))
- added support for system sound inputs and outputs (Windows & Linux)
- added 2 new animations (RainbowNoise & Particles)
- added fadeout option to Close & Open animations
- redesigned update notification dialog
- small bug fixes

#### pre0.2.0.8
- added 7 new animations
- added 3 new music effects
- added 2 new lua scripts
- added new scene
- improved some effects
- save the Simulator window size
- added 3 new styles
- added new Lua functions
- small improvements and optimizations

#### pre0.2.0.7
- added API
- added Windows Look and Feel
- added Chain output
- new animation and music visualizer
- added RGB order option to Arduino output
- added start parameters
- added output patch
- some improvements and optimizations

#### pre0.2.0.6
- added Lua scripts feature

#### pre0.2.0.5
- added artnet protocol
- added multi language support for settings
- some small improvements

#### pre0.2.0.4
- added settings for animations
- improved start time
- added update checker
- added some new animation modes

#### pre0.2.0.3
First release of the new RemoteLight v0.2 on Github.
- recoded everything
- new UI
- new output system (multiple outputs can be added)
- new animation system + a bunch of new animations
- new MusicSync system + improved sound processing
- improved ScreenColor
- new settings system
- added neopixel simulator
- support for multiple languages (not finished)

#### v0.1.2
- added custom colors
- added support for more than one monitor
- small improvements

#### v0.1.1
- code optimization
- improved sound input window
- save last used sound input