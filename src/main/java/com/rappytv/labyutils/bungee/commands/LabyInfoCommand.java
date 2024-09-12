package com.rappytv.labyutils.bungee.commands;

import com.rappytv.labyutils.bungee.LabyUtilsBungee;
import com.rappytv.labyutils.common.ILabyUtilsPlugin;
import net.labymod.serverapi.api.model.component.ServerAPITextComponent;
import net.labymod.serverapi.server.bungeecord.LabyModPlayer;
import net.labymod.serverapi.server.bungeecord.LabyModProtocolService;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LabyInfoCommand extends Command implements TabExecutor {

    private final LabyUtilsBungee plugin;

    public LabyInfoCommand(LabyUtilsBungee plugin) {
        super("labyinfo", "labyutils.info");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission("labyutils.info")) {
            sender.sendMessage(TextComponent.fromLegacyText(
                    LabyUtilsBungee.getPrefix() + "§cYou are not allowed to use this command!"
            ));
            return;
        }
        if(args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(
                    LabyUtilsBungee.getPrefix() + "§cPlease enter a player name!"
            ));
            return;
        }
        ProxiedPlayer player = plugin.getProxy().getPlayer(args[0]);
        if(player == null) {
            sender.sendMessage(TextComponent.fromLegacyText(
                    LabyUtilsBungee.getPrefix() + "§cThis player was not found!"
            ));
            return;
        }
        String response = LabyUtilsBungee.getPrefix() + "§6LabyInfo of " + player.getName();
        LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(player.getUniqueId());
        response += "\n" + LabyUtilsBungee.getPrefix() + "§bUUID: §7" + player.getUniqueId();
        if(labyPlayer == null) {
            response += "\n" + LabyUtilsBungee.getPrefix() + "§bUsing LabyMod: §cNo";
            sender.sendMessage(TextComponent.fromLegacyText(response));
            return;
        }
        response += "\n" + LabyUtilsBungee.getPrefix() + "§bUsing LabyMod: §aYes";
        if(plugin.getConfigManager().areSubtitlesEnabled()
                && sender.hasPermission("labyutils.info.subtitle")) {
            ServerAPITextComponent component = (ServerAPITextComponent) labyPlayer.subtitle().getText();
            String subtitle = component != null ? component.getText() : "--";
            response += "\n" + LabyUtilsBungee.getPrefix() + "§bServer subtitle: §7" + subtitle;
        }
        if(sender.hasPermission("labyutils.info.economy")) {
            response += "\n" + LabyUtilsBungee.getPrefix() +
                    "§bEconomy cash: §7" + plugin.formatNumber(labyPlayer.cashEconomy().getBalance()) +
                    "\n" + LabyUtilsBungee.getPrefix() +
                    "§bEconomy bank: §7" + plugin.formatNumber(labyPlayer.bankEconomy().getBalance());
        }
        if(sender.hasPermission("labyutils.info.version")) {
            response += "\n" + LabyUtilsBungee.getPrefix() + "§bLabyMod version: §7v" + labyPlayer.getLabyModVersion();
        }
        if(sender.hasPermission("labyutils.info.region")) {
            String flag = ILabyUtilsPlugin.cachedFlags.containsKey(player.getUniqueId())
                    ? ILabyUtilsPlugin.cachedFlags.get(player.getUniqueId()).name()
                    : "--";
            response += "\n" + LabyUtilsBungee.getPrefix() + "§bRegion: §7" + flag;
        }
        sender.sendMessage(TextComponent.fromLegacyText(response));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> players = new ArrayList<>();
            for(ProxiedPlayer player : plugin.getProxy().getPlayers()) {
                if(player.getName().toLowerCase().startsWith(args[0].toLowerCase())) players.add(player.getName());
            }
            return players;
        }
        return Collections.emptyList();
    }
}
