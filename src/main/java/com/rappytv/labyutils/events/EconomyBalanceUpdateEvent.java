package com.rappytv.labyutils.events;

import com.rappytv.labyutils.LabyUtilsPlugin;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import net.labymod.serverapi.server.bukkit.LabyModProtocolService;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EconomyBalanceUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final LabyModPlayer player;
    private final double balance;
    private final BalanceType type;
    public static final Map<UUID, Double> cashBalances = new HashMap<>();
    public static final Map<UUID, Double> bankBalances = new HashMap<>();

    public EconomyBalanceUpdateEvent(@NotNull LabyModPlayer player, double balance, BalanceType type) {
        Objects.requireNonNull(player, "Player must not be null");
        Objects.requireNonNull(type, "BalanceType must not be null");
        this.player = player;
        this.balance = balance;
        this.type = type;
    }

    @NotNull
    public LabyModPlayer getPlayer() {
        return player;
    }

    public double getBalance() {
        return balance;
    }

    @NotNull
    public BalanceType getBalanceType() {
        return type;
    }

    public static void initialize(LabyUtilsPlugin plugin) {
        if(plugin.getEconomy() == null) return;
        long updateInterval = plugin.getConfigManager().getEconomyUpdateInterval() * 20L;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            boolean cash = plugin.getConfigManager().showCashBalance();
            boolean bank = plugin.getConfigManager().showBankBalance();
            if(!cash && !bank) return;

            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player == null) return;
                LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(player.getUniqueId());
                if(labyPlayer == null) continue;

                if(cash) {
                    double response = plugin.getEconomy().getBalance(player);
                    if(cashBalances.containsKey(labyPlayer.getUniqueId())
                            && cashBalances.get(labyPlayer.getUniqueId()) == response) continue;
                    cashBalances.put(labyPlayer.getUniqueId(), response);
                    Bukkit.getPluginManager().callEvent(new EconomyBalanceUpdateEvent(
                            labyPlayer,
                            response,
                            BalanceType.CASH
                    ));
                }
                if(bank && plugin.getEconomy().hasBankSupport()) {
                    EconomyResponse response = plugin.getEconomy().bankBalance(player.getName());
                    if(response.type != EconomyResponse.ResponseType.SUCCESS) {
                        plugin.getLogger().warning("Failed to get bank balance: " + response.errorMessage);
                        return;
                    }
                    if(bankBalances.containsKey(labyPlayer.getUniqueId())
                            && bankBalances.get(labyPlayer.getUniqueId()) == response.balance) continue;
                    bankBalances.put(labyPlayer.getUniqueId(), response.balance);

                    Bukkit.getPluginManager().callEvent(new EconomyBalanceUpdateEvent(
                            labyPlayer,
                            response.balance,
                            BalanceType.BANK
                    ));
                }
            }
        }, updateInterval, updateInterval);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum BalanceType {
        CASH,
        BANK
    }
}
