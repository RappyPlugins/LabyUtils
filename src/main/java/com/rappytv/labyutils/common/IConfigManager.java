package com.rappytv.labyutils.common;

public interface IConfigManager<T> {

    String defaultPrefix = "§8[§9LABY§8] ";
    String defaultKickMessage = "§c§lKICKED!\n\n§bReason: §7Missing required addons: %s";

    void reloadConfig();
    String getPrefix();
    boolean isWelcomeLogEnabled();
    boolean isWelcomeMessageEnabled();
    String getWelcomeMessage();
    boolean isBannerEnabled();
    String getBannerUrl();
    int getEconomyUpdateInterval();
    boolean showCashBalance();
    boolean showBankBalance();
    boolean areFlagsEnabled();
    boolean areSubtitlesEnabled();
    T getSubtitles();
    boolean areInteractionsEnabled();
    T getInteractionBullets();
    boolean isAddonManagementEnabled();
    T getAddonManagement();
    String getAddonKickMessage();
    boolean arePermissionsEnabled();
    T getPermissions();
    boolean isRpcEnabled();
    String getRpcText();
    boolean showRpcJoinTime();
}
