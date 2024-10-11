package com.rappytv.labyutils.bungee;

import com.rappytv.labyutils.bungee.commands.LabyInfoCommand;
import com.rappytv.labyutils.bungee.commands.ReloadCommand;
import com.rappytv.labyutils.bungee.listener.PlayerListener;
import com.rappytv.labyutils.common.ILabyUtilsPlugin;
import net.labymod.serverapi.server.bungeecord.LabyModProtocolService;
import net.md_5.bungee.api.plugin.Plugin;

public final class LabyUtilsBungee extends Plugin implements ILabyUtilsPlugin {

    private static LabyUtilsBungee instance;
    private BungeeConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new BungeeConfigManager(new LabyUtilsConfig(this));
        try {
            LabyModProtocolService.initialize(this);
            getLogger().info("LabyMod protocol service initialized.");
        } catch (IllegalStateException e) {
            getLogger().info("LabyMod protocol service already initialized.");
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
}
