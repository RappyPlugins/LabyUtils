package com.rappytv.labyutils.bukkit.listeners;

import com.rappytv.labyutils.bukkit.LabyUtilsBukkit;
import com.rappytv.labyutils.bukkit.events.EconomyBalanceUpdateEvent;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EconomyBalanceUpdateListener implements Listener {

    private final LabyUtilsBukkit plugin;

    public EconomyBalanceUpdateListener(LabyUtilsBukkit plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBalanceUpdate(EconomyBalanceUpdateEvent event) {
        if(plugin.getEconomy() == null) return;
        LabyModPlayer player = event.getPlayer();

        switch (event.getBalanceType()) {
            case CASH: {
                player.updateCashEconomy(economy -> economy.balance(event.getBalance()));
                break;
            }
            case BANK: {
                player.updateBankEconomy(economy -> economy.balance(event.getBalance()));
                break;
            }
        }
    }
}
