package com.rappytv.labyutils.bungee;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class LabyUtilsConfig {

    private static final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private final Logger logger;
    private File configFolder;
    private File configFile;
    private Configuration configuration;

    public LabyUtilsConfig(LabyUtilsBungee plugin) {
        logger = plugin.getLogger();
        configFolder = plugin.getDataFolder();
        configFile = new File(configFolder, "config.yml");
        reload();
    }

    public void reload() {
        try {
            if(!configFolder.exists()) {
                configFolder.mkdir();
            }
            if(!configFile.exists()) {
                configFile.createNewFile();
            }

            configuration = provider.load(configFile);
        } catch (IOException e) {
            logger.severe("Failed to load plugin configuration!");
        }
    }

    public Configuration get() {
        if(configuration == null) {
            throw new IllegalStateException("Configuration is not yet initialized!");
        }
        return configuration;
    }
}
