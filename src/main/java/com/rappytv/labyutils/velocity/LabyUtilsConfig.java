package com.rappytv.labyutils.velocity;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class LabyUtilsConfig {

    private final LabyUtilsVelocity plugin;
    private final File configFolder;
    private final File configFile;
    private YamlDocument configuration;

    public LabyUtilsConfig(LabyUtilsVelocity plugin, Path dataDirectory) {
        this.plugin = plugin;
        configFolder = dataDirectory.toFile();
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
                configFile.createNewFile();
            }

            configuration = YamlDocument.create(
                    new File(configFolder, "config.yml"),
                    Objects.requireNonNull(plugin.getClass().getResourceAsStream("/config_bungee.yml")),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().build()
            );
            configuration.update();
            configuration.save();

        } catch (IOException e) {
            plugin.logger().error("Failed to load plugin configuration!");
        }
    }

    public YamlDocument get() {
        if(configuration == null) {
            throw new IllegalStateException("Configuration is not yet initialized!");
        }
        return configuration;
    }
}
