package com.rappytv.labyutils.velocity.listener;

import com.rappytv.labyutils.common.listeners.IPlayerListener;
import com.rappytv.labyutils.velocity.LabyUtilsVelocity;
import com.velocitypowered.api.event.Subscribe;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.kyori.adventure.text.Component;
import net.labymod.serverapi.api.model.component.ServerAPIComponent;
import net.labymod.serverapi.core.model.feature.DiscordRPC;
import net.labymod.serverapi.core.model.feature.InteractionMenuEntry;
import net.labymod.serverapi.core.model.moderation.Permission;
import net.labymod.serverapi.core.model.moderation.RecommendedAddon;
import net.labymod.serverapi.server.velocity.LabyModPlayer;
import net.labymod.serverapi.server.velocity.event.LabyModPlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerListener implements IPlayerListener<LabyModPlayerJoinEvent, LabyModPlayer> {

    private final LabyUtilsVelocity plugin;

    public PlayerListener(LabyUtilsVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onPlayerJoin(LabyModPlayerJoinEvent event) {

        LabyModPlayer labyPlayer = event.labyModPlayer();

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
    public void logJoin(LabyModPlayer player) {
        if(!plugin.getConfigManager().isWelcomeLogEnabled()) return;
        plugin.logger().info(String.format(
                "%s just joined with LabyMod v%s!",
                player.getPlayer().getGameProfile().getName(),
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
        player.getPlayer().sendMessage(Component.text(text));
    }

    @Override
    public void setBanner(LabyModPlayer player) {
        if(!plugin.getConfigManager().isBannerEnabled()) return;
        player.sendTabListBanner(plugin.getConfigManager().getBannerUrl());
    }

    @Override
    public void setFlag(LabyModPlayer player) {
        plugin.getCountryCode(player.getUniqueId(), player.getPlayer().getRemoteAddress(), (flag) -> {
            if(flag != null && plugin.getConfigManager().areFlagsEnabled()) {
                plugin.getProxy().getScheduler().buildTask(plugin, () -> player.setTabListFlag(flag));
            }
        });
    }

    @Override
    public void setSubtitle(LabyModPlayer player) {
        if(!plugin.getConfigManager().areSubtitlesEnabled()) return;
        Section section = plugin
                .getConfigManager()
                .getSubtitles();

        if(section == null) return;
        ServerAPIComponent component = null;
        double size = -1;

        for(String key : section.getKeys().stream().map(Object::toString).collect(Collectors.toSet())) {
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
        Section section = plugin
                .getConfigManager()
                .getInteractionBullets();

        for(String key : section.getKeys().stream().map(Object::toString).collect(Collectors.toSet())) {
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
                plugin.logger().warn("Failed to build interaction bullet with id " + key);
            }
        }

        if(!entries.isEmpty()) player.sendInteractionMenuEntries(entries);
    }

    @Override
    public void managePermissions(LabyModPlayer player) {
        if(!plugin.getConfigManager().arePermissionsEnabled()) return;
        // TODO: Find error source
        List<Permission.StatedPermission> permissions = new ArrayList<>();
        Section section = plugin
                .getConfigManager()
                .getPermissions();

        if(section == null) return;

        for(String key : section.getKeys().stream().map(Object::toString).collect(Collectors.toSet())) {
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

        Section section = plugin
                .getConfigManager()
                .getAddonManagement();

        if(section == null) return;

        for(String key : section.getKeys().stream().map(Object::toString).collect(Collectors.toSet())) {
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
                List<String> missingRequiredAddons = new ArrayList<>();
                for(String missingAddon : response.getMissingAddons()) {
                    for(RecommendedAddon addon : recommendedAddons) {
                        if(!addon.getNamespace().equals(missingAddon) || !addon.isRequired()) continue;
                        missingRequiredAddons.add(missingAddon);
                    }
                }
                if(!missingRequiredAddons.isEmpty() && player.getPlayer().isActive()) {
                    player.getPlayer().disconnect(
                            Component.text(String.format(
                                    plugin.getConfigManager().getAddonKickMessage(),
                                    String.join(", ", missingRequiredAddons)
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
