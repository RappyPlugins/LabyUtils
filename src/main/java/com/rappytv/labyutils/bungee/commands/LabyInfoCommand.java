package com.rappytv.labyutils.bungee.commands;

import com.rappytv.labyutils.bukkit.LabyUtilsBukkit;
import com.rappytv.labyutils.bungee.LabyUtilsBungee;
import com.rappytv.labyutils.common.ILabyUtilsPlugin;
import net.labymod.serverapi.api.model.component.ServerAPITextComponent;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import net.labymod.serverapi.server.bukkit.LabyModProtocolService;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LabyInfoCommand extends Command {

    private final LabyUtilsBungee plugin;

    public LabyInfoCommand(LabyUtilsBungee plugin) {
        super("labyinfo", "labyutils.info");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission("labyutils.info")) {
            sender.sendMessage(TextComponent.fromLegacyText(
                    LabyUtilsBukkit.getPrefix() + "§cYou are not allowed to use this command!"
            ));
            return;
        }
        if(args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(
                    LabyUtilsBukkit.getPrefix() + "§cPlease enter a player name!"
            ));
            return;
        }
        ProxiedPlayer player = plugin.getProxy().getPlayer(args[0]);
        if(player == null) {
            sender.sendMessage(TextComponent.fromLegacyText(
                    LabyUtilsBukkit.getPrefix() + "§cThis player was not found!"
            ));
            return;
        }
        String response = LabyUtilsBukkit.getPrefix() + "§6LabyInfo of " + sender.getName();
        LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(player.getUniqueId());
        response += "\n" + LabyUtilsBukkit.getPrefix() + "§bUUID: §7" + player.getUniqueId();
        if(labyPlayer == null) {
            response += "\n" + LabyUtilsBukkit.getPrefix() + "§bUsing LabyMod: §cNo";
            sender.sendMessage(TextComponent.fromLegacyText(response));
            return;
        }
        response += "\n" + LabyUtilsBukkit.getPrefix() + "§bUsing LabyMod: §aYes";
        if(plugin.getConfigManager().areSubtitlesEnabled()
                && sender.hasPermission("labyutils.info.subtitle")) {
            ServerAPITextComponent component = (ServerAPITextComponent) labyPlayer.subtitle().getText();
            String subtitle = component != null ? component.getText() : "--";
            response += "\n" + LabyUtilsBukkit.getPrefix() + "§bServer subtitle: §7" + subtitle;
        }
        if(sender.hasPermission("labyutils.info.economy")) {
            response += "\n" + LabyUtilsBukkit.getPrefix() +
                    "§bEconomy cash: §7" + plugin.formatNumber(labyPlayer.cashEconomy().getBalance()) +
                    "\n" + LabyUtilsBukkit.getPrefix() +
                    "§bEconomy bank: §7" + plugin.formatNumber(labyPlayer.bankEconomy().getBalance());
        }
        if(sender.hasPermission("labyutils.info.version")) {
            response += "\n" + LabyUtilsBukkit.getPrefix() + "§bLabyMod version: §7v" + labyPlayer.getLabyModVersion();
        }
        if(sender.hasPermission("labyutils.info.region")) {
            String flag = ILabyUtilsPlugin.cachedFlags.containsKey(player.getUniqueId())
                    ? ILabyUtilsPlugin.cachedFlags.get(player.getUniqueId()).name()
                    : "--";
            response += "\n" + LabyUtilsBukkit.getPrefix() + "§bRegion: §7" + flag;
        }
        sender.sendMessage(TextComponent.fromLegacyText(response));
    }
}
