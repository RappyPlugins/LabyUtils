package com.rappytv.labyutils.bukkit;

import com.rappytv.labyutils.common.IConfigManager;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

public class BukkitConfigManager implements IConfigManager<ConfigurationSection> {

    private final LabyUtilsBukkit plugin;
    private Configuration config;

    public BukkitConfigManager(LabyUtilsBukkit plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public String getPrefix() {
        return config.getString("prefix", defaultPrefix);
    }

    public boolean isLabyModDisallowed() {
        return config.getBoolean("disallow.enabled");
    }

    public String getDisallowedKickMessage() {
        return config.getString("disallow.kickMessage", defaultDisallowedKickMessage);
    }

    public boolean isWelcomeLogEnabled() {
        return config.getBoolean("welcome.log");
    }

    public boolean isWelcomeMessageEnabled() {
        return config.getBoolean("welcome.enabled");
    }

    public String getWelcomeMessage() {
        return config.getString("welcome.message");
    }

    public boolean isBannerEnabled() {
        return config.getBoolean("banner.enabled");
    }

    public String getBannerUrl() {
        return config.getString("banner.url", "");
    }

    public int getEconomyUpdateInterval() {
        return config.getInt("economy.updateInterval");
    }

    public boolean showCashBalance() {
        return config.getBoolean("economy.cash");
    }

    public boolean showBankBalance() {
        return config.getBoolean("economy.bank");
    }

    public boolean areFlagsEnabled() {
        return config.getBoolean("flags.enabled");
    }

    public boolean areSubtitlesEnabled() {
        return config.getBoolean("subtitles.enabled");
    }

    public ConfigurationSection getSubtitles() {
        return config.getConfigurationSection("subtitles.subtitles");
    }

    public boolean areInteractionsEnabled() {
        return config.getBoolean("interactions.enabled");
    }

    public ConfigurationSection getInteractionBullets() {
        return config.getConfigurationSection("interactions.bullets");
    }

    public boolean isAddonManagementEnabled() {
        return config.getBoolean("addons.enabled");
    }

    public ConfigurationSection getAddonManagement() {
        return config.getConfigurationSection("addons.addons");
    }

    public String getAddonKickMessage() {
        return config.getString("addons.kickMessage", defaultAddonKickMessage);
    }

    public boolean arePermissionsEnabled() {
        return config.getBoolean("permissions.enabled");
    }

    public ConfigurationSection getPermissions() {
        return config.getConfigurationSection("permissions.permissions");
    }

    public boolean isRpcEnabled() {
        return config.getBoolean("rpc.enabled");
    }

    public String getRpcText() {
        return config.getString("rpc.text");
    }

    public boolean showRpcJoinTime() {
        return config.getBoolean("rpc.showJoinTime");
    }
}
