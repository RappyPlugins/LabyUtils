# LabyUtils
A simple plugin to utilize LabyMod's server API without coding knowledge.

### Sources
- [Modrinth](https://modrinth.com/plugin/labyutils)
- [Curseforge](https://www.curseforge.com/minecraft/bukkit-plugins/labyutils)
- [SpigotMC](https://www.spigotmc.org/resources/labyutils.118954/)

## General info

### Features
- Disallow LabyMod completely by kicking players as soon as they join with LabyMod
- Custom banner above the tablist
- An economy display (cash & bank) using [Vault](https://www.spigotmc.org/resources/vault.34315/)
- Country flags beside your name in the tablist
- Subtitles below player names based on their permissions
- Customizable interaction menu bullets
- Sync LabyMod permissions which allow/limit core/addon features with server permissions
- Partly customizable Discord rich presence (LabyMod does not allow fully customizable rpc)
- A command to see LabyMod specific information of a player
  - The LabyMod subtitle
  - The economy balances
  - The LabyMod version
  - The region code
- Put specific addons into on of 3 states (These are bypassable with permissions)
  - Recommended (Shows the player a popup recommending these addons)
  - Required (The player will get kicked if not installing all required addons from the popup)
  - Disabled (Disables addons completely)

### Commands
- `/labyinfo` - Shows LabyMod specific info about a player.
- `/labyutils` - Reloads the config.

### Permissions
- `labyutils.bypass.addon.*` - Ignores all recommended, required, or disallowed addons
- `labyutils.bypass.addon.<namespace>` - Ignores a specific recommended, required, or disallowed addon
- `labyutils.info` - Grants base access to `/labyinfo`
- `labyutils.info.subtitle` - See player subtitles in `/labyutils`
- `labyutils.info.economy` - See economy balances in `/labyutils`
- `labyutils.info.version` - See LabyMod versions in `/labyutils`
- `labyutils.info.region` - See player regions in `/labyutils`
- `labyutils.permissions.*` - Gives the player all configured LabyMod permissions
- `labyutils.reload` - Grants access to `/labyutils`

## Dependencies
Requires Java 11+

Optional dependencies
- Vault - For economy displays
- PlaceholderAPI - For placeholders. See [supported placeholders](https://github.com/RappyPlugins/LabyUtils/master/readme.md#supported-placeholderapi-placeholders)

### Supported PlaceholderAPI placeholders
- `%labyutils_playerflag%`* - The player's country code
- `%labyutils_subtitle%`* - The player's server subtitle
- `%labyutils_clientversion%`* - The player's LabyMod version
- `%labyutils_cash%`* - The player's cash balance
- `%labyutils_bank%`* - The player's bank balance
- `%labyutils_banner%` - The server's banner

<sub>* - Requires a player argument</sub>

Note: I'm not a LabyMod developer so this is an unofficial plugin.
