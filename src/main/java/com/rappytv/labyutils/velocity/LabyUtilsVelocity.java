package com.rappytv.labyutils.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;

import javax.inject.Inject;
import java.util.logging.Logger;

public class LabyUtilsVelocity {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public LabyUtilsVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

    }

}
