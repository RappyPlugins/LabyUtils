package com.rappytv.labyutils.bungee;

import com.rappytv.labyutils.bungee.commands.LabyInfoCommand;
import com.rappytv.labyutils.bungee.commands.ReloadCommand;
import com.rappytv.labyutils.bungee.listener.PlayerListener;
import com.rappytv.labyutils.common.ILabyUtilsPlugin;
import io.sentry.Sentry;
import net.md_5.bungee.api.plugin.Plugin;

public final class LabyUtilsBungee extends Plugin implements ILabyUtilsPlugin {

    private BungeeConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new BungeeConfigManager(new LabyUtilsConfig(this));
        if(configManager.isSentryEnabled()) {
            getLogger().info("Thanks for enabling Sentry! Loading...");
            initializeSentry();
        }
        getProxy().getPluginManager().registerCommand(this, new LabyInfoCommand(this));
        getProxy().getPluginManager().registerCommand(this, new ReloadCommand(this));
        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
    }

    public BungeeConfigManager getConfigManager() {
        return configManager;
    }

    private void initializeSentry() {
        Sentry.init(options -> {
            options.setDsn("https://bd16d626052842d7209032d5329fb525@sentry.rappytv.com/3");
            options.setTracesSampleRate(1.0);
            options.setRelease(getDescription().getVersion());
            getLogger().info("Sentry loaded!");
        });
    }
}
