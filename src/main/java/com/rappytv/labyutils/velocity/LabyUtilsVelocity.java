package com.rappytv.labyutils.velocity;

import com.rappytv.labyutils.common.ILabyUtilsPlugin;
import com.rappytv.labyutils.velocity.commands.LabyInfoCommand;
import com.rappytv.labyutils.velocity.commands.ReloadCommand;
import com.rappytv.labyutils.velocity.listener.PlayerListener;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.labymod.serverapi.api.logger.ProtocolPlatformLogger;
import net.labymod.serverapi.server.velocity.LabyModProtocolService;
import net.labymod.serverapi.server.velocity.Slf4jPlatformLogger;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.Optional;

public final class LabyUtilsVelocity implements ILabyUtilsPlugin {

    private static LabyUtilsVelocity instance;
    private VelocityConfigManager configManager;
    private final ProxyServer server;
    private final ProtocolPlatformLogger logger;
    private final Logger serverLogger;
    private final Path dataDirectory;

    @Inject
    public LabyUtilsVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.serverLogger = logger;
        this.logger = new Slf4jPlatformLogger(logger);
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        configManager = new VelocityConfigManager(/*new VelocityConfig(this)*/);
        try {
            LabyModProtocolService.initialize(this, server, serverLogger);
            logger.info("LabyMod protocol service initialized.");
        } catch (IllegalStateException e) {
            logger.info("LabyMod protocol service already initialized.");
        }
        if(configManager.isSentryEnabled()) {
            String version = "?";
            Optional<PluginContainer> plugin = server.getPluginManager().getPlugin("labyutils");
            if(plugin.isPresent() && plugin.get().getDescription().getVersion().isPresent()) {
                version = plugin.get().getDescription().getVersion().get();
            }
            logger.info("Thanks for enabling Sentry! Loading...");
            initializeSentry(version);
        }
        CommandManager manager = server.getCommandManager();
        BrigadierCommand infoCommand = LabyInfoCommand.createBrigadierCommand(this);
        BrigadierCommand reloadCommand = ReloadCommand.createBrigadierCommand(this);
        manager.register(manager.metaBuilder(infoCommand).build(), infoCommand);
        manager.register(manager.metaBuilder(reloadCommand).build(), reloadCommand);
        server.getEventManager().register(this, new PlayerListener(this));
    }

    @Override
    public ProtocolPlatformLogger logger() {
        return logger;
    }

    public static TextComponent getPrefix() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(instance.getConfigManager().getPrefix());
    }

    public VelocityConfigManager getConfigManager() {
        return configManager;
    }

    public ProxyServer getServer() {
        return server;
    }
}
