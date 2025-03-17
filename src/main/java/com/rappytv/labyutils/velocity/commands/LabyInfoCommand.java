package com.rappytv.labyutils.velocity.commands;

import com.rappytv.labyutils.common.ILabyUtilsPlugin;
import com.rappytv.labyutils.velocity.LabyUtilsVelocity;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.labymod.serverapi.api.model.component.ServerAPITextComponent;
import net.labymod.serverapi.server.velocity.LabyModPlayer;
import net.labymod.serverapi.server.velocity.LabyModProtocolService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LabyInfoCommand implements SimpleCommand {

    private final LabyUtilsVelocity plugin;

    public LabyInfoCommand(LabyUtilsVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (!sender.hasPermission("labyutils.info")) {
            sender.sendMessage(Component.text(LabyUtilsVelocity.getPrefix() + "§cYou are not allowed to use this command!"));
            return;
        }
        if (args.length < 1) {
            sender.sendMessage(Component.text(LabyUtilsVelocity.getPrefix() + "§cPlease enter a player name!"));
            return;
        }
        Player player = (Player) sender;
        if (player == null) {
            sender.sendMessage(Component.text(LabyUtilsVelocity.getPrefix() + "§cThis player was not found!"));
            return;
        }
        String response = LabyUtilsVelocity.getPrefix() + "§6LabyInfo of " + player.getUsername();
        LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(player.getUniqueId());
        response += "\n" + LabyUtilsVelocity.getPrefix() + "§bUUID: §7" + player.getUniqueId();
        if (labyPlayer == null) {
            response += "\n" + LabyUtilsVelocity.getPrefix() + "§bUsing LabyMod: §cNo";
            sender.sendMessage(Component.text(response));
            return;
        }
        response += "\n" + LabyUtilsVelocity.getPrefix() + "§bUsing LabyMod: §aYes";
        if (plugin.getConfigManager().areSubtitlesEnabled()
                && sender.hasPermission("labyutils.info.subtitle")) {
            ServerAPITextComponent component = (ServerAPITextComponent) labyPlayer.subtitle().getText();
            String subtitle = component != null ? component.getText() : "--";
            response += "\n" + LabyUtilsVelocity.getPrefix() + "§bServer subtitle: §7" + subtitle;
        }
        if (sender.hasPermission("labyutils.info.economy")) {
            response += "\n" + LabyUtilsVelocity.getPrefix() +
                    "§bEconomy cash: §7" + plugin.formatNumber(labyPlayer.cashEconomy().getBalance()) +
                    "\n" + LabyUtilsVelocity.getPrefix() +
                    "§bEconomy bank: §7" + plugin.formatNumber(labyPlayer.bankEconomy().getBalance());
        }
        if (sender.hasPermission("labyutils.info.version")) {
            response += "\n" + LabyUtilsVelocity.getPrefix() + "§bLabyMod version: §7v" + labyPlayer.getLabyModVersion();
        }
        if (sender.hasPermission("labyutils.info.region")) {
            String flag = ILabyUtilsPlugin.cachedFlags.containsKey(player.getUniqueId())
                    ? ILabyUtilsPlugin.cachedFlags.get(player.getUniqueId()).name()
                    : "--";
            response += "\n" + LabyUtilsVelocity.getPrefix() + "§bRegion: §7" + flag;
        }
        sender.sendMessage(Component.text(response));
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        List<String> players = new ArrayList<>();
        String[] args = invocation.arguments();
        if (args.length == 0) {
            for (Player player : plugin.getProxy().getAllPlayers()) {
                players.add(player.getUsername());
            }
        }
        return CompletableFuture.completedFuture(players);
    }
}
