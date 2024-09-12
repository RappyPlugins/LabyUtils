package com.rappytv.labyutils.velocity;

import com.rappytv.labyutils.common.IConfigManager;

public class VelocityConfigManager implements IConfigManager {

    @Override
    public void reloadConfig() {

    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public boolean isSentryEnabled() {
        return true;
    }

    @Override
    public boolean isWelcomeLogEnabled() {
        return true;
    }

    @Override
    public boolean isWelcomeMessageEnabled() {
        return true;
    }

    @Override
    public String getWelcomeMessage() {
        return "";
    }

    @Override
    public boolean isBannerEnabled() {
        return true;
    }

    @Override
    public String getBannerUrl() {
        return "";
    }

    @Override
    public int getEconomyUpdateInterval() {
        return 0;
    }

    @Override
    public boolean showCashBalance() {
        return true;
    }

    @Override
    public boolean showBankBalance() {
        return true;
    }

    @Override
    public boolean areFlagsEnabled() {
        return true;
    }

    @Override
    public boolean areSubtitlesEnabled() {
        return true;
    }

    @Override
    public Object getSubtitles() {
        return null;
    }

    @Override
    public boolean areInteractionsEnabled() {
        return true;
    }

    @Override
    public Object getInteractionBullets() {
        return null;
    }

    @Override
    public boolean isAddonManagementEnabled() {
        return true;
    }

    @Override
    public Object getAddonManagement() {
        return null;
    }

    @Override
    public String getAddonKickMessage() {
        return "";
    }

    @Override
    public boolean arePermissionsEnabled() {
        return true;
    }

    @Override
    public Object getPermissions() {
        return null;
    }

    @Override
    public boolean isRpcEnabled() {
        return true;
    }

    @Override
    public String getRpcText() {
        return "";
    }

    @Override
    public boolean showRpcJoinTime() {
        return true;
    }
}
