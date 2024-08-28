package com.rappytv.labyutils.bukkit.expansion;

import com.rappytv.labyutils.bukkit.LabyUtilsPlugin;
import com.rappytv.labyutils.bukkit.listeners.PlayerListener;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.labymod.serverapi.api.model.component.ServerAPITextComponent;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import net.labymod.serverapi.server.bukkit.LabyModProtocolService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LabyModPlayerExpansion extends PlaceholderExpansion {

    private final LabyUtilsPlugin plugin;

    public LabyModPlayerExpansion(LabyUtilsPlugin plugin) {
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
        if(player == null) {
            switch (identifier.toLowerCase()) {
                case "banner": {
                    if(!plugin.getConfigManager().isBannerEnabled()) return "";
                    return plugin.getConfigManager().getBannerUrl();
                }
                default: return null;
            }
        } else {
            LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(player.getUniqueId());
            if(labyPlayer == null) return "";

            switch (identifier.toLowerCase()) {
                case "playerflag": {
                    return PlayerListener.cachedFlags.containsKey(player.getUniqueId())
                            ? PlayerListener.cachedFlags.get(player.getUniqueId()).name()
                            : "--";
                }
                case "subtitle": {
                    ServerAPITextComponent component = (ServerAPITextComponent) labyPlayer.subtitle().getText();
                    return component != null ? component.getText() : "";
                }
                case "clientversion": {
                    return labyPlayer.getLabyModVersion();
                }
                case "cash": {
                    return String.valueOf(labyPlayer.cashEconomy().getBalance());
                }
                case "bank": {
                    return String.valueOf(labyPlayer.bankEconomy().getBalance());
                }
                default: return null;
            }
        }
    }
}
