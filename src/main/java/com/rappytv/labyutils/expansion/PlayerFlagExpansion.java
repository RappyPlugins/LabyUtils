package com.rappytv.labyutils.expansion;

import com.rappytv.labyutils.LabyUtilsPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import net.labymod.serverapi.server.bukkit.LabyModProtocolService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerFlagExpansion extends PlaceholderExpansion {

    private final LabyUtilsPlugin plugin;

    public PlayerFlagExpansion(LabyUtilsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "labyutils";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        switch (identifier.toLowerCase()) {
            case "playerflag": {
                if(player == null) return "";
                LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(player.getUniqueId());
                if(labyPlayer == null) return "";
                return labyPlayer.getTabListFlag() != null ? labyPlayer.getTabListFlag().getCountryCode().name() : "";
            }
            default: return null;
        }
    }
}
