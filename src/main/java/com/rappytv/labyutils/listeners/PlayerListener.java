package com.rappytv.labyutils.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rappytv.labyutils.LabyUtilsPlugin;
import com.rappytv.labyutils.events.EconomyBalanceUpdateEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.labymod.serverapi.api.model.component.ServerAPIComponent;
import net.labymod.serverapi.core.model.display.TabListFlag;
import net.labymod.serverapi.core.model.feature.DiscordRPC;
import net.labymod.serverapi.core.model.feature.InteractionMenuEntry;
import net.labymod.serverapi.core.model.moderation.Permission;
import net.labymod.serverapi.core.model.moderation.RecommendedAddon;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import net.labymod.serverapi.server.bukkit.event.LabyModPlayerJoinEvent;
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

    private final static Gson gson = new Gson();
    private final static HttpClient client = HttpClient.newHttpClient();
    private final static String defaultKickMessage = "§c§lKICKED!\n\n§bReason: §7Missing required addons: %s";

    private final LabyUtilsPlugin plugin;

    public PlayerListener(LabyUtilsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(LabyModPlayerJoinEvent event) {
        LabyModPlayer labyPlayer = event.labyModPlayer();
        Player player = labyPlayer.getPlayer();

        if(plugin.getConfig().getBoolean("banner.enabled")) {
            labyPlayer.sendTabListBanner(plugin.getConfig().getString("banner.url"));
        }

        if(plugin.getConfig().getBoolean("flags.enabled")) {
            getCountryCode(player, (flag) -> {
                if(flag != null) labyPlayer.setTabListFlag(flag);
            });
        }

        if(plugin.getConfig().getBoolean("rpc.enabled")) {
            String text = plugin.getConfig().getString("rpc.text");
            boolean showTime = plugin.getConfig().getBoolean("rpc.showJoinTime");

            if(text == null) return;
            if(plugin.isUsingPapi()) {
                text = PlaceholderAPI.setPlaceholders(player, text);
            }

            DiscordRPC rpc = showTime
                    ? DiscordRPC.createWithStart(text, System.currentTimeMillis())
                    : DiscordRPC.create(text);

            labyPlayer.sendDiscordRPC(rpc);
        }

        if(plugin.getConfig().getBoolean("addons.disabled.enabled")) {
            labyPlayer.disableAddons(plugin.getConfig().getStringList("addons.disabled.addons"));
        }

        if(plugin.getConfig().getBoolean("addons.recommendations.enabled")) {
            List<RecommendedAddon> addons = new ArrayList<>();
            ConfigurationSection section = plugin
                    .getConfig()
                    .getConfigurationSection("addons.recommendations.addons");

            if(section == null) return;

            for(String key : section.getKeys(false)) {
                addons.add(RecommendedAddon.of(key, section.getBoolean(key)));
            }

            labyPlayer.sendAddonRecommendations(addons, response -> {
                if(response.isInitial()) return;
                if(!response.isAllInstalled()) {
                    player.kickPlayer(String.format(
                            section.getString("kickMessage", defaultKickMessage),
                            String.join(", ", response.getMissingAddons())
                    ));
                }
            });
        }

        if(plugin.getConfig().getBoolean("subtitles.enabled")) {
            ConfigurationSection section = plugin
                    .getConfig()
                    .getConfigurationSection("subtitles.subtitles");

            if(section == null) return;
            ServerAPIComponent component = null;
            double size = -1;

            for(String key : section.getKeys(false)) {
                String permission = section.getString(key + ".permission");
                String text = section.getString(key + ".text");
                if(permission != null && text != null && player.hasPermission(permission)) {
                    if(plugin.isUsingPapi()) {
                        text = PlaceholderAPI.setPlaceholders(player, text);
                    }
                    component = ServerAPIComponent.text(text);
                    size = section.getDouble(key + ".size");
                    break;
                }
            }
            if(component == null || size <= 0) {
                labyPlayer.resetSubtitle();
                return;
            }

            labyPlayer.updateSubtitle(component, size);
        }

        // TODO: Find error source
        if(plugin.getConfig().getBoolean("permissions.enabled")) {
            List<Permission.StatedPermission> permissions = new ArrayList<>();
            ConfigurationSection section = plugin
                    .getConfig()
                    .getConfigurationSection("permissions.permissions");

            if(section == null) return;

            for(String key : section.getKeys(false)) {
                permissions.add(section.getBoolean(key) ? Permission.of(key).allow() : Permission.of(key).deny());
            }

            labyPlayer.sendPermissions(permissions);
        }

        if(plugin.getConfig().getBoolean("interactions.enabled")) {
            List<InteractionMenuEntry> entries = new ArrayList<>();

            // TODO: Rewrite
            List<InteractionBullet> bullets = (List<InteractionBullet>) plugin.getConfig().getList("interactions.bullets", Collections.emptyList());
            for(InteractionBullet bullet : bullets) {
                if(bullet.permission != null && !player.hasPermission(bullet.permission)) continue;
                if(bullet.title == null || bullet.title.isBlank() || bullet.type == null || bullet.value == null) continue;
                InteractionMenuEntry.InteractionMenuType type;
                try {
                    type = InteractionMenuEntry.InteractionMenuType.valueOf(
                            bullet.type
                                    .toUpperCase()
                                    .replace(' ', '_')
                    );
                } catch (Exception e) {
                    continue;
                }
                entries.add(InteractionMenuEntry.create(
                        ServerAPIComponent.text(bullet.title),
                        type,
                        bullet.value
                ));
            }

            if(!entries.isEmpty()) labyPlayer.sendInteractionMenuEntries(entries);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        EconomyBalanceUpdateEvent.cashBalances.remove(event.getPlayer().getUniqueId());
        EconomyBalanceUpdateEvent.bankBalances.remove(event.getPlayer().getUniqueId());
    }

    public void getCountryCode(Player player, Consumer<TabListFlag.@Nullable TabListFlagCountryCode> consumer) {
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
                consumer.accept(TabListFlag.TabListFlagCountryCode.valueOf(body.get("country").getAsString()));
            }).exceptionally(throwable -> {
                plugin.getLogger().warning("Failed to get country code of " + host);
                consumer.accept(null);
                return null;
            });
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get country code of " + host);
            consumer.accept(null);
        }
    }

    private static class InteractionBullet {
        public String title;
        public String permission;
        public String type;
        public String value;
    }
}
