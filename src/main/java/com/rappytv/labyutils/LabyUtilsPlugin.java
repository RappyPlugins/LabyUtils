package com.rappytv.labyutils;

import com.rappytv.labyutils.commands.LabyInfoCommand;
import com.rappytv.labyutils.commands.ReloadCommand;
import com.rappytv.labyutils.events.EconomyBalanceUpdateEvent;
import com.rappytv.labyutils.expansion.LabyModPlayerExpansion;
import com.rappytv.labyutils.listeners.EconomyBalanceUpdateListener;
import com.rappytv.labyutils.listeners.PlayerListener;
import net.labymod.serverapi.server.bukkit.LabyModProtocolService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class LabyUtilsPlugin extends JavaPlugin {

    private static LabyUtilsPlugin instance;
    private Economy economy = null;
    private boolean usingPapi = false;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        saveDefaultConfig();
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

    public ConfigManager getConfigManager() {
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
}
