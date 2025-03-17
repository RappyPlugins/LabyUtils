package com.rappytv.labyutils.velocity;

import com.rappytv.labyutils.common.IConfigManager;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;

public class VelocityConfigManager implements IConfigManager<Section> {

    private final LabyUtilsConfig pluginConfig;
    private YamlDocument config;

    public VelocityConfigManager(LabyUtilsConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
        this.config = pluginConfig.get();
    }

    @Override
    public void reloadConfig() {
        pluginConfig.reload();
        config = pluginConfig.get();
    }

    public String getPrefix() {
        return config.getString("prefix", defaultPrefix);
    }

    @Override
    public boolean isSentryEnabled() {
        return false;
    }

    public boolean isLabyModDisallowed() {
        return config.getBoolean("disallow.enabled");
    }

    @Override
    public boolean isWelcomeLogEnabled() {
        return config.getBoolean("welcome.log");
    }

    @Override
    public boolean isWelcomeMessageEnabled() {
        return config.getBoolean("welcome.enabled");
    }

    @Override
    public String getWelcomeMessage() {
        return config.getString("welcome.message");
    }

    @Override
    public boolean isBannerEnabled() {
        return config.getBoolean("banner.enabled");
    }

    @Override
    public String getBannerUrl() {
        return config.getString("banner.url", "");
    }

    @Override
    public int getEconomyUpdateInterval() {
        return -1;
    }

    @Override
    public boolean showCashBalance() {
        return false;
    }

    @Override
    public boolean showBankBalance() {
        return false;
    }

    @Override
    public boolean areFlagsEnabled() {
        return config.getBoolean("flags.enabled");
    }

    @Override
    public boolean areSubtitlesEnabled() {
        return config.getBoolean("subtitles.enabled");
    }

    @Override
    public Section getSubtitles() {
        return config.getSection("subtitles.subtitles");
    }

    @Override
    public boolean areInteractionsEnabled() {
        return config.getBoolean("interactions.enabled");
    }

    @Override
    public Section getInteractionBullets() {
        return config.getSection("interactions.bullets");
    }

    @Override
    public boolean isAddonManagementEnabled() {
        return config.getBoolean("addons.enabled");
    }

    @Override
    public Section getAddonManagement() {
        return config.getSection("addons.addons");
    }

    @Override
    public String getAddonKickMessage() {
        return config.getString("addons.kickMessage", defaultKickMessage);
    }

    @Override
    public boolean arePermissionsEnabled() {
        return config.getBoolean("permissions.enabled");
    }

    @Override
    public YamlDocument getPermissions() {
        return config.getSection("permissions.permissions").getRoot();
    }

    @Override
    public boolean isRpcEnabled() {
        return config.getBoolean("rpc.enabled");
    }

    @Override
    public String getRpcText() {
        return config.getString("rpc.text");
    }

    public boolean showRpcJoinTime() {
        return config.getBoolean("rpc.showJoinTime");
    }
}
