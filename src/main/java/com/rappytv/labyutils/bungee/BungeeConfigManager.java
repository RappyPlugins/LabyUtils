package com.rappytv.labyutils.bungee;

import com.rappytv.labyutils.common.IConfigManager;
import net.md_5.bungee.config.Configuration;

public class BungeeConfigManager implements IConfigManager<Configuration> {

    private final LabyUtilsConfig pluginConfig;
    private Configuration config;

    public BungeeConfigManager(LabyUtilsConfig config) {
        this.pluginConfig = config;
    }

    @Override
    public void reloadConfig() {
        pluginConfig.reload();
        config = pluginConfig.get();
    }

    public String getPrefix() {
        return config.getString("prefix", defaultPrefix);
    }

    public boolean isSentryEnabled() {
        return config.getBoolean("sentry", true);
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

    public Configuration getSubtitles() {
        return config.getSection("subtitles.subtitles");
    }

    public boolean areInteractionsEnabled() {
        return config.getBoolean("interactions.enabled");
    }

    public Configuration getInteractionBullets() {
        return config.getSection("interactions.bullets");
    }

    public boolean isAddonManagementEnabled() {
        return config.getBoolean("addons.enabled");
    }

    public Configuration getAddonManagement() {
        return config.getSection("addons.addons");
    }

    public String getAddonKickMessage() {
        return config.getString("addons.kickMessage", defaultKickMessage);
    }

    public boolean arePermissionsEnabled() {
        return config.getBoolean("permissions.enabled");
    }

    public Configuration getPermissions() {
        return config.getSection("permissions.permissions");
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
