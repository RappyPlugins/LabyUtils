package com.rappytv.labyutils.common.listeners;

import net.labymod.serverapi.server.common.model.player.AbstractServerLabyModPlayer;

public interface IPlayerListener<E, L extends AbstractServerLabyModPlayer<?, ?>> {

    void onPlayerJoin(E event);

    boolean disallowLabyMod(L player);
    void logJoin(L player);
    void sendWelcomer(L player);
    void setBanner(L player);
    void setFlag(L player);
    void setSubtitle(L player);
    void setInteractionBullets(L player);
    void managePermissions(L player);
    void setRPC(L player);
    void manageAddons(L player);
}
