package com.rappytv.labyutils.bukkit.listeners;

import com.rappytv.labyutils.bukkit.LabyUtilsBukkit;
import com.rappytv.labyutils.bukkit.events.EconomyBalanceUpdateEvent;
import com.rappytv.labyutils.common.listeners.IPlayerListener;
import me.clip.placeholderapi.PlaceholderAPI;
import net.labymod.serverapi.api.model.component.ServerAPIComponent;
import net.labymod.serverapi.core.model.feature.DiscordRPC;
import net.labymod.serverapi.core.model.feature.InteractionMenuEntry;
import net.labymod.serverapi.core.model.moderation.Permission;
import net.labymod.serverapi.core.model.moderation.RecommendedAddon;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import net.labymod.serverapi.server.bukkit.event.LabyModPlayerJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class PlayerListener implements Listener, IPlayerListener<LabyModPlayerJoinEvent, LabyModPlayer> {

    private final LabyUtilsBukkit plugin;

    public PlayerListener(LabyUtilsBukkit plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(LabyModPlayerJoinEvent event) {
        LabyModPlayer labyPlayer = event.labyModPlayer();

        if(disallowLabyMod(labyPlayer)) return;

        logJoin(labyPlayer);
        sendWelcomer(labyPlayer);
        setBanner(labyPlayer);
        setFlag(labyPlayer);
        setSubtitle(labyPlayer);
        setInteractionBullets(labyPlayer);
        manageAddons(labyPlayer);
        managePermissions(labyPlayer);
        setRPC(labyPlayer);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        EconomyBalanceUpdateEvent.cashBalances.remove(event.getPlayer().getUniqueId());
        EconomyBalanceUpdateEvent.bankBalances.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public boolean disallowLabyMod(LabyModPlayer player) {
        if(plugin.getConfigManager().isLabyModDisallowed() && !player.getPlayer().hasPermission("labyutils.bypass.labymod")) {
            player.getPlayer().kickPlayer(plugin.getConfigManager().getDisallowedKickMessage());
            return true;
        }
        return false;
    }

    public void logJoin(LabyModPlayer player) {
        if(!plugin.getConfigManager().isWelcomeLogEnabled()) return;
        plugin.getLogger().info(String.format(
                "%s just joined with LabyMod v%s!",
                player.getPlayer().getName(),
                player.getLabyModVersion()
        ));
    }

    public void sendWelcomer(LabyModPlayer player) {
        if(!plugin.getConfigManager().isWelcomeMessageEnabled()) return;
        String text = plugin
                .getConfigManager()
                .getWelcomeMessage()
                .replace("<prefix>", plugin.getConfigManager().getPrefix());
        if(plugin.isUsingPapi()) {
            text = PlaceholderAPI.setPlaceholders(player.getPlayer(), text);
        }
        player.getPlayer().sendMessage(text);
    }

    public void setBanner(LabyModPlayer player) {
        if(!plugin.getConfigManager().isBannerEnabled()) return;
        player.sendTabListBanner(plugin.getConfigManager().getBannerUrl());
    }

    public void setFlag(LabyModPlayer player) {
        plugin.getCountryCode(player.getUniqueId(), player.getPlayer().getAddress(), (flag) -> {
            if(flag != null && plugin.getConfigManager().areFlagsEnabled()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.setTabListFlag(flag));
            }
        });
    }

    public void setSubtitle(LabyModPlayer player) {
        if(!plugin.getConfigManager().areSubtitlesEnabled()) return;
        ConfigurationSection section = plugin
                .getConfigManager()
                .getSubtitles();

        if(section == null) return;
        ServerAPIComponent component = null;
        double size = -1;

        for(String key : section.getKeys(false)) {
            String permission = section.getString(key + ".permission");
            String text = section.getString(key + ".text");
            if(permission != null && text != null && player.getPlayer().hasPermission(permission)) {
                if(plugin.isUsingPapi()) {
                    text = PlaceholderAPI.setPlaceholders(player.getPlayer(), text);
                }
                component = ServerAPIComponent.text(text);
                size = section.getDouble(key + ".size");
                break;
            }
        }
        if(component == null || size <= 0) {
            player.resetSubtitle();
            return;
        }

        player.updateSubtitle(component, size);
    }

    public void setInteractionBullets(LabyModPlayer player) {
        if(!plugin.getConfigManager().areInteractionsEnabled()) return;
        List<InteractionMenuEntry> entries = new ArrayList<>();
        ConfigurationSection section = plugin
                .getConfigManager()
                .getInteractionBullets();

        for(String key : section.getKeys(false)) {
            if(section.isString(key + ".permission")
                    && !player.getPlayer().hasPermission(section.getString(key + ".permission"))) continue;
            try {
                entries.add((InteractionMenuEntry.create(
                        ServerAPIComponent.text(section.getString(key + ".title")),
                        InteractionMenuEntry.InteractionMenuType.valueOf(
                                section.getString(key + ".type").toUpperCase().replace(' ', '_')
                        ),
                        section.getString(key + ".value")
                )));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to build interaction bullet with id " + key);
            }
        }

        if(!entries.isEmpty()) player.sendInteractionMenuEntries(entries);
    }

    public void managePermissions(LabyModPlayer player) {
        if(!plugin.getConfigManager().arePermissionsEnabled()) return;
        // TODO: Find error source
        List<Permission.StatedPermission> permissions = new ArrayList<>();
        ConfigurationSection section = plugin
                .getConfigManager()
                .getPermissions();

        if(section == null) return;

        for(String key : section.getKeys(false)) {
            boolean hasPermission = player.getPlayer().hasPermission("labyutils.permissions.*")
                    || player.getPlayer().hasPermission("labyutils.permissions." + section.getString(key));
            permissions.add(hasPermission ? Permission.of(key).allow() : Permission.of(key).deny());
        }

        player.sendPermissions(permissions);
    }

    public void setRPC(LabyModPlayer player) {
        if(!plugin.getConfigManager().isRpcEnabled()) return;
        String text = plugin.getConfigManager().getRpcText();
        boolean showTime = plugin.getConfigManager().showRpcJoinTime();

        if(text == null) return;
        if(plugin.isUsingPapi()) {
            text = PlaceholderAPI.setPlaceholders(player.getPlayer(), text);
        }

        DiscordRPC rpc = showTime
                ? DiscordRPC.createWithStart(text, System.currentTimeMillis())
                : DiscordRPC.create(text);

        player.sendDiscordRPC(rpc);
    }

    public void manageAddons(LabyModPlayer player) {
        if(!plugin.getConfigManager().isAddonManagementEnabled()) return;
        List<RecommendedAddon> recommendedAddons = new ArrayList<>();
        List<String> disabledAddons = new ArrayList<>();

        ConfigurationSection section = plugin
                .getConfigManager()
                .getAddonManagement();

        if(section == null) return;

        for(String key : section.getKeys(false)) {
            boolean canBypass = player.getPlayer().hasPermission("labyutils.bypass.addon.*")
                    || player.getPlayer().hasPermission("labyutils.bypass.addon." + key);
            if(canBypass) continue;
            switch (section.getString(key, "none").toLowerCase()) {
                case "recommend":
                    recommendedAddons.add(RecommendedAddon.of(key, false));
                    break;
                case "require":
                    recommendedAddons.add(RecommendedAddon.of(key, true));
                    break;
                case "disable":
                    disabledAddons.add(key);
                    break;
            }
        }

        if(!recommendedAddons.isEmpty()) {
            player.sendAddonRecommendations(recommendedAddons, response -> {
                if(response.isInitial()) return;
                if(!response.isAllInstalled()) {
                    if(player.getPlayer().isOnline()) player.getPlayer().kickPlayer(String.format(
                            plugin.getConfigManager().getAddonKickMessage(),
                            String.join(", ", response.getMissingAddons())
                    ));
                }
            });
        }
        if(!disabledAddons.isEmpty()) {
            player.disableAddons(disabledAddons);
        }
    }
}
