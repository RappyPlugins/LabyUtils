# LabyUtils
A simple plugin to utilize LabyMod's server API without coding knowledge.

### Sources
- [Modrinth](https://modrinth.com/plugin/labyutils)
- [Curseforge](https://www.curseforge.com/minecraft/bukkit-plugins/labyutils)
- [SpigotMC](https://www.spigotmc.org/resources/labyutils.118954/)

## General info

### Features
- Custom banner above the tablist
- An economy display (cash & bank) using [Vault](https://www.spigotmc.org/resources/vault.34315/)
- Country flags beside your name in the tablist
- Subtitles below player names based on their permissions
- Customizable interaction menu bullets
- Addons which are disabled on join
- Recommended addons
- Required addons (The player will get kicked if not having all required addons)
- Enabled/disabled permissions which allow or limit core/addon functions of LabyMod
- Partly customizable Discord rich presence (LabyMod does not allow fully customizable rpc)

### Commands:
- `/labyutils` - Reloads the config.

### Permissions:
- `labyutils.reload` - Grants access to `/labyutils`

## Dependencies
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
