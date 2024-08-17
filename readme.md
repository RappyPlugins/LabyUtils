# LabyUtils
A simple plugin to utilize LabyMod's server API without coding knowledge.

### General info

Commands:
- /labyutils - Reloads the config.

Permissions:
- `labyutils.reload` - Grants access to `/labyutils`

### Dependencies

Depends on
- [LabyModServerAPI](https://github.com/LabyMod/labymod4-server-api/releases/latest)

Optional dependencies
- Vault - For economy displays
- PlaceholderAPI - Should be self-explanatory

### Supported placeholders
- `%labyutils_playerflag%`* - The player's country code
- `%labyutils_subtitle%`* - The player's server subtitle
- `%labyutils_clientversion%`* - The player's LabyMod version
- `%labyutils_cash%`* - The player's cash balance
- `%labyutils_bank%`* - The player's bank balance
- `%labyutils_banner%` - The server's banner

<sub>* - Requires a player argument</sub>

Note: I'm not a LabyMod developer so this is an unofficial plugin.
