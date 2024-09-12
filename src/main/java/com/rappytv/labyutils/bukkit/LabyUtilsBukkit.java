package com.rappytv.labyutils.bukkit;

import com.rappytv.labyutils.bukkit.commands.LabyInfoCommand;
import com.rappytv.labyutils.bukkit.commands.ReloadCommand;
import com.rappytv.labyutils.bukkit.events.EconomyBalanceUpdateEvent;
import com.rappytv.labyutils.bukkit.expansion.LabyModPlayerExpansion;
import com.rappytv.labyutils.bukkit.listeners.EconomyBalanceUpdateListener;
import com.rappytv.labyutils.bukkit.listeners.PlayerListener;
import com.rappytv.labyutils.common.ILabyUtilsPlugin;
import io.sentry.Sentry;
import net.labymod.serverapi.api.logger.ProtocolPlatformLogger;
import net.labymod.serverapi.server.bukkit.LabyModProtocolService;
import net.labymod.serverapi.server.common.JavaProtocolLogger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class LabyUtilsBukkit extends JavaPlugin implements ILabyUtilsPlugin {

    private static LabyUtilsBukkit instance;
    private Economy economy = null;
    private boolean usingPapi = false;
    private BukkitConfigManager configManager;
    private ProtocolPlatformLogger logger;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configManager = new BukkitConfigManager(this);
        logger = new JavaProtocolLogger(getLogger());
        if(configManager.isSentryEnabled()) {
            getLogger().info("Thanks for enabling Sentry! Loading...");
            initializeSentry(getDescription().getVersion());
        }
        try {
            LabyModProtocolService.initialize(this);
            getLogger().info("LabyMod protocol service initialized.");
        } catch (IllegalStateException e) {
            getLogger().info("LabyMod protocol service already initialized.");
        }

        // Load dependencies
        if(loadVaultEconomy()) {
            getLogger().info("Vault is installed, loaded economy provider!");
        } else {
            getLogger().warning("Vault not accessible. Economy display disabled.");
        }

        if(loadPlaceholderAPI()) {
            getLogger().info("PlaceholderAPI is installed. Registering expansion...");
            new LabyModPlayerExpansion(this).register();
        } else {
            getLogger().warning("PlaceholderAPI not installed.");
        }

        EconomyBalanceUpdateEvent.initialize(this);
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new EconomyBalanceUpdateListener(this), this);
        pluginManager.registerEvents(new PlayerListener(this), this);
        Objects.requireNonNull(Bukkit.getPluginCommand("labyinfo")).setExecutor(new LabyInfoCommand(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("labyutils")).setExecutor(new ReloadCommand(this));
    }

    @Override
    public void onDisable() {
        Sentry.close();
    }

    public static String getPrefix() {
        return instance.getConfigManager().getPrefix();
    }

    @Nullable
    public Economy getEconomy() {
        return economy;
    }

    public boolean isUsingPapi() {
        return usingPapi;
    }

    public BukkitConfigManager getConfigManager() {
        return configManager;
    }

    private boolean loadVaultEconomy() {
        if(getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> provider = getServer().getServicesManager().getRegistration(Economy.class);
        if(provider == null) return false;
        economy = provider.getProvider();
        return true;
    }

    private boolean loadPlaceholderAPI() {
        usingPapi = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
        return usingPapi;
    }

    @Override
    public ProtocolPlatformLogger logger() {
        return logger;
    }
}
