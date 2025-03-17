package com.rappytv.labyutils.velocity;

import com.google.inject.Inject;
import com.rappytv.labyutils.common.ILabyUtilsPlugin;
import com.rappytv.labyutils.velocity.commands.LabyInfoCommand;
import com.rappytv.labyutils.velocity.commands.ReloadCommand;
import com.rappytv.labyutils.velocity.listener.PlayerListener;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.labymod.serverapi.api.logger.ProtocolPlatformLogger;
import net.labymod.serverapi.server.velocity.LabyModProtocolService;
import net.labymod.serverapi.server.velocity.Slf4jPlatformLogger;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "labyutils",
        name = "LabyUtils",
        version = "1.0.3",
        description = "A simple plugin to utilize LabyMod's server API without coding knowledge.",
        authors = {"RappyTV", "EinsJustin"}
)
public class LabyUtilsVelocity implements ILabyUtilsPlugin {

    private static LabyUtilsVelocity instance;
    private final VelocityConfigManager configManager;

    private final Logger serverLogger;
    private final ProtocolPlatformLogger logger;
    private final ProxyServer proxy;

    @Inject
    public LabyUtilsVelocity(Logger logger, ProxyServer proxy, @DataDirectory Path dataDirectory) {
        this.serverLogger = logger;
        this.logger = new Slf4jPlatformLogger(logger);
        this.proxy = proxy;
        instance = this;
        configManager = new VelocityConfigManager(new LabyUtilsConfig(this, dataDirectory));
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            LabyModProtocolService.initialize(this, proxy, serverLogger);
            logger.info("LabyMod protocol service initialized.");
        } catch (IllegalArgumentException e) {
            logger.info("LabyMod protocol service already initialized.");
            throw new IllegalStateException(e);
        }
        proxy.getEventManager().register(this, new PlayerListener(this));

        CommandManager commandManager = proxy.getCommandManager();

        CommandMeta labyInfoMeta = commandManager.metaBuilder("labyinfo").plugin(this).build();
        CommandMeta reloadMeta = commandManager.metaBuilder("labyutils").plugin(this).build();

        commandManager.register(labyInfoMeta, new LabyInfoCommand(this));
        commandManager.register(reloadMeta, new ReloadCommand(this));
    }

    public static String getPrefix() {
        return instance.getConfigManager().getPrefix();
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public VelocityConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public ProtocolPlatformLogger logger() {
        return null;
    }
}
