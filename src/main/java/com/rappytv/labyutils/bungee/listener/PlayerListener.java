package com.rappytv.labyutils.bungee.listener;

import com.rappytv.labyutils.bungee.LabyUtilsBungee;
import com.rappytv.labyutils.common.listeners.IPlayerListener;
import net.labymod.serverapi.api.model.component.ServerAPIComponent;
import net.labymod.serverapi.core.model.feature.DiscordRPC;
import net.labymod.serverapi.core.model.feature.InteractionMenuEntry;
import net.labymod.serverapi.core.model.moderation.Permission;
import net.labymod.serverapi.core.model.moderation.RecommendedAddon;
import net.labymod.serverapi.server.bungeecord.LabyModPlayer;
import net.labymod.serverapi.server.bungeecord.event.LabyModPlayerJoinEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener, IPlayerListener<LabyModPlayerJoinEvent, LabyModPlayer> {

    private final LabyUtilsBungee plugin;

    public PlayerListener(LabyUtilsBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(LabyModPlayerJoinEvent event) {
        LabyModPlayer labyPlayer = event.labyModPlayer();

        if(disallowLabyMod(labyPlayer)) return;

        logJoin(labyPlayer);
        sendWelcomer(labyPlayer);
        setBanner(labyPlayer);
        setFlag(labyPlayer);
        setSubtitle(labyPlayer);
        setInteractionBullets(labyPlayer);
        managePermissions(labyPlayer);
        setRPC(labyPlayer);
        manageAddons(labyPlayer);
    }

    @Override
    public boolean disallowLabyMod(LabyModPlayer player) {
        if(plugin.getConfigManager().isLabyModDisallowed()) {
            player.getPlayer().disconnect(
                    TextComponent.fromLegacyText(
                            plugin
                                    .getConfigManager()
                                    .getDisallowedKickMessage()
                    )
            );
            return true;
        }
        return false;
    }

    @Override
    public void logJoin(LabyModPlayer player) {
        if(!plugin.getConfigManager().isWelcomeLogEnabled()) return;
        plugin.getLogger().info(String.format(
                "%s just joined with LabyMod v%s!",
                player.getPlayer().getName(),
                player.getLabyModVersion()
        ));
    }

    @Override
    public void sendWelcomer(LabyModPlayer player) {
        if(!plugin.getConfigManager().isWelcomeMessageEnabled()) return;
        String text = plugin
                .getConfigManager()
                .getWelcomeMessage()
                .replace("<prefix>", plugin.getConfigManager().getPrefix());
        player.getPlayer().sendMessage(TextComponent.fromLegacyText(text));
    }

    @Override
    public void setBanner(LabyModPlayer player) {
        if(!plugin.getConfigManager().isBannerEnabled()) return;
        player.sendTabListBanner(plugin.getConfigManager().getBannerUrl());
    }

    @Override
    public void setFlag(LabyModPlayer player) {
        plugin.getCountryCode(player.getUniqueId(), player.getPlayer().getAddress(), (flag) -> {
            if(flag != null && plugin.getConfigManager().areFlagsEnabled()) {
                plugin.getProxy().getScheduler().runAsync(plugin, () -> player.setTabListFlag(flag));
            }
        });
    }

    @Override
    public void setSubtitle(LabyModPlayer player) {
        if(!plugin.getConfigManager().areSubtitlesEnabled()) return;
        Configuration section = plugin
                .getConfigManager()
                .getSubtitles();

        if(section == null) return;
        ServerAPIComponent component = null;
        double size = -1;

        for(String key : section.getKeys()) {
            String permission = section.getString(key + ".permission");
            String text = section.getString(key + ".text");
            if(permission != null && text != null && player.getPlayer().hasPermission(permission)) {
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

    @Override
    public void setInteractionBullets(LabyModPlayer player) {
        if(!plugin.getConfigManager().areInteractionsEnabled()) return;
        List<InteractionMenuEntry> entries = new ArrayList<>();
        Configuration section = plugin
                .getConfigManager()
                .getInteractionBullets();

        for(String key : section.getKeys()) {
            if(section.contains(key + ".permission")
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

    @Override
    public void managePermissions(LabyModPlayer player) {
        if(!plugin.getConfigManager().arePermissionsEnabled()) return;
        // TODO: Find error source
        List<Permission.StatedPermission> permissions = new ArrayList<>();
        Configuration section = plugin
                .getConfigManager()
                .getPermissions();

        if(section == null) return;

        for(String key : section.getKeys()) {
            boolean hasPermission = player.getPlayer().hasPermission("labyutils.permissions.*")
                    || player.getPlayer().hasPermission("labyutils.permissions." + section.getString(key));
            permissions.add(hasPermission ? Permission.of(key).allow() : Permission.of(key).deny());
        }

        player.sendPermissions(permissions);
    }

    @Override
    public void setRPC(LabyModPlayer player) {
        if(!plugin.getConfigManager().isRpcEnabled()) return;
        String text = plugin.getConfigManager().getRpcText();
        boolean showTime = plugin.getConfigManager().showRpcJoinTime();

        if(text == null) return;

        DiscordRPC rpc = showTime
                ? DiscordRPC.createWithStart(text, System.currentTimeMillis())
                : DiscordRPC.create(text);

        player.sendDiscordRPC(rpc);
    }

    @Override
    public void manageAddons(LabyModPlayer player) {
        if(!plugin.getConfigManager().isAddonManagementEnabled()) return;
        List<RecommendedAddon> recommendedAddons = new ArrayList<>();
        List<String> disabledAddons = new ArrayList<>();

        Configuration section = plugin
                .getConfigManager()
                .getAddonManagement();

        if(section == null) return;

        for(String key : section.getKeys()) {
            boolean canBypass = player.getPlayer().hasPermission("labyutils.bypass.*")
                    || player.getPlayer().hasPermission("labyutils.bypass." + key);
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
                    if(player.getPlayer().isConnected()) player.getPlayer().disconnect(
                            TextComponent.fromLegacyText(String.format(
                                    plugin.getConfigManager().getAddonKickMessage(),
                                    String.join(", ", response.getMissingAddons())
                            ))
                    );
                }
            });
        }
        if(!disabledAddons.isEmpty()) {
            player.disableAddons(disabledAddons);
        }
    }
}
