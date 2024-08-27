package com.rappytv.labyutils.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rappytv.labyutils.LabyUtilsPlugin;
import com.rappytv.labyutils.events.EconomyBalanceUpdateEvent;
import io.sentry.Sentry;
import me.clip.placeholderapi.PlaceholderAPI;
import net.labymod.serverapi.api.model.component.ServerAPIComponent;
import net.labymod.serverapi.core.model.display.TabListFlag;
import net.labymod.serverapi.core.model.feature.DiscordRPC;
import net.labymod.serverapi.core.model.feature.InteractionMenuEntry;
import net.labymod.serverapi.core.model.moderation.Permission;
import net.labymod.serverapi.core.model.moderation.RecommendedAddon;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import net.labymod.serverapi.server.bukkit.event.LabyModPlayerJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Consumer;

public class PlayerListener implements Listener {

    public final static Map<UUID, TabListFlag.TabListFlagCountryCode> cachedFlags = new HashMap<>();
    private final static Gson gson = new Gson();
    private final static HttpClient client = HttpClient.newHttpClient();

    private final LabyUtilsPlugin plugin;

    public PlayerListener(LabyUtilsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(LabyModPlayerJoinEvent event) {
        LabyModPlayer labyPlayer = event.labyModPlayer();

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

    private void logJoin(LabyModPlayer player) {
        if(!plugin.getConfigManager().isWelcomeLogEnabled()) return;
        plugin.getLogger().info(String.format(
                "%s just joined with LabyMod v%s!",
                player.getPlayer().getName(),
                player.getLabyModVersion()
        ));
    }

    private void sendWelcomer(LabyModPlayer player) {
        if(!plugin.getConfigManager().isWelcomeMessageEnabled()) return;
        String text = plugin
                .getConfigManager()
                .getWelcomeMessage()
                .replace("<prefix>", LabyUtilsPlugin.getPrefix());
        if(plugin.isUsingPapi()) {
            text = PlaceholderAPI.setPlaceholders(player.getPlayer(), text);
        }
        player.getPlayer().sendMessage(text);
    }

    private void setBanner(LabyModPlayer player) {
        if(!plugin.getConfigManager().isBannerEnabled()) return;
        player.sendTabListBanner(plugin.getConfigManager().getBannerUrl());
    }

    private void setFlag(LabyModPlayer player) {
        getCountryCode(player.getPlayer(), (flag) -> {
            if(flag != null && plugin.getConfigManager().areFlagsEnabled()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.setTabListFlag(flag));
            }
        });
    }

    private void setSubtitle(LabyModPlayer player) {
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

    private void setInteractionBullets(LabyModPlayer player) {
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

    private void manageAddons(LabyModPlayer player) {
        if(!plugin.getConfigManager().isAddonManagementEnabled()) return;
        List<RecommendedAddon> recommendedAddons = new ArrayList<>();
        List<String> disabledAddons = new ArrayList<>();

        ConfigurationSection section = plugin
                .getConfigManager()
                .getAddonManagement();

        if(section == null) return;

        for(String key : section.getKeys(false)) {
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

    private void managePermissions(LabyModPlayer player) {
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

    private void setRPC(LabyModPlayer player) {
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

    private void getCountryCode(Player player, Consumer<TabListFlag.@Nullable TabListFlagCountryCode> consumer) {
        if(cachedFlags.containsKey(player.getUniqueId())) {
            consumer.accept(cachedFlags.get(player.getUniqueId()));
            return;
        }
        if(player.getAddress() == null) {
            consumer.accept(null);
            return;
        }
        String host = player.getAddress().getHostString();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.country.is/" + host))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
                JsonObject body = gson.fromJson(response.body(), JsonObject.class);
                cachedFlags.put(player.getUniqueId(), TabListFlag.TabListFlagCountryCode.valueOf(body.get("country").getAsString()));
                consumer.accept(cachedFlags.get(player.getUniqueId()));
            }).exceptionally(throwable -> {
                Sentry.captureException(throwable);
                plugin.getLogger().warning("Failed to get country code of " + host);
                consumer.accept(null);
                return null;
            });
        } catch (Exception e) {
            Sentry.captureException(e);
            plugin.getLogger().warning("Failed to get country code of " + host);
            consumer.accept(null);
        }
    }
}
