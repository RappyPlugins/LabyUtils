package com.rappytv.labyutils.bungee;

import com.rappytv.labyutils.bungee.commands.LabyInfoCommand;
import com.rappytv.labyutils.bungee.commands.ReloadCommand;
import com.rappytv.labyutils.bungee.listener.PlayerListener;
import com.rappytv.labyutils.common.ILabyUtilsPlugin;
import net.labymod.serverapi.api.logger.ProtocolPlatformLogger;
import net.labymod.serverapi.server.bungeecord.LabyModProtocolService;
import net.labymod.serverapi.server.common.JavaProtocolLogger;
import net.md_5.bungee.api.plugin.Plugin;

public final class LabyUtilsBungee extends Plugin implements ILabyUtilsPlugin {

    private static LabyUtilsBungee instance;
    private BungeeConfigManager configManager;
    private ProtocolPlatformLogger platformLogger;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new BungeeConfigManager(new LabyUtilsConfig(this));
        platformLogger = new JavaProtocolLogger(getLogger());
        try {
            LabyModProtocolService.initialize(this);
            getLogger().info("LabyMod protocol service initialized.");
        } catch (IllegalStateException e) {
            getLogger().info("LabyMod protocol service already initialized.");
        }
        if(configManager.isSentryEnabled()) {
            getLogger().info("Thanks for enabling Sentry! Loading...");
            initializeSentry(getDescription().getVersion());
        }
        getProxy().getPluginManager().registerCommand(this, new LabyInfoCommand(this));
        getProxy().getPluginManager().registerCommand(this, new ReloadCommand(this));
        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
    }

    public static String getPrefix() {
        return instance.getConfigManager().getPrefix();
    }

    public BungeeConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public ProtocolPlatformLogger logger() {
        return platformLogger;
    }
}
