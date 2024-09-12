package com.rappytv.labyutils.bungee;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LabyUtilsConfig {

    private static final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private final LabyUtilsBungee plugin;
    private final File configFolder;
    private final File configFile;
    private Configuration configuration;

    public LabyUtilsConfig(LabyUtilsBungee plugin) {
        this.plugin = plugin;
        configFolder = plugin.getDataFolder();
        configFile = new File(configFolder, "config.yml");
        reload();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void reload() {
        try {
            if(!configFolder.exists()) {
                configFolder.mkdir();
            }
            if(!configFile.exists()) {
                FileOutputStream outputStream = new FileOutputStream(configFile);
                InputStream in = plugin.getResourceAsStream("config_bungee.yml");
                in.transferTo(outputStream);
                in.close();
            }

            configuration = provider.load(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load plugin configuration!");
        }
    }

    public Configuration get() {
        if(configuration == null) {
            throw new IllegalStateException("Configuration is not yet initialized!");
        }
        return configuration;
    }
}
