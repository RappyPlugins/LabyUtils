package com.rappytv.labyutils.commands;

import com.rappytv.labyutils.LabyUtilsPlugin;
import net.labymod.serverapi.api.model.component.ServerAPITextComponent;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import net.labymod.serverapi.server.bukkit.LabyModProtocolService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LabyInfoCommand implements CommandExecutor {

    private final LabyUtilsPlugin plugin;

    public LabyInfoCommand(LabyUtilsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String prefix, @NotNull String[] args) {
        if(!sender.hasPermission("labyutils.info")) {
            sender.sendMessage(LabyUtilsPlugin.getPrefix() + "§cYou are not allowed to use this command!");
            return false;
        }
        if(args.length < 1) {
            sender.sendMessage(LabyUtilsPlugin.getPrefix() + "§cPlease enter a player name!");
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if(player == null) {
            sender.sendMessage(LabyUtilsPlugin.getPrefix() + "§cThis player was not found!");
            return false;
        }
        String response = LabyUtilsPlugin.getPrefix() + "§6LabyInfo of " + sender.getName();
        LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(player.getUniqueId());
        response += "\n" + LabyUtilsPlugin.getPrefix() + "§bUUID: §7" + player.getUniqueId();
        if(labyPlayer == null) {
            response += "\n" + LabyUtilsPlugin.getPrefix() + "§bUsing LabyMod: §cNo";
            sender.sendMessage(response);
            return true;
        }
        response += "\n" + LabyUtilsPlugin.getPrefix() + "§bUsing LabyMod: §aYes";
        if(plugin.getConfig().getBoolean("subtitles.enabled")
                && sender.hasPermission("labyutils.info.subtitle")) {
            ServerAPITextComponent component = (ServerAPITextComponent) labyPlayer.subtitle().getText();
            String subtitle = component != null ? component.getText() : "--";
            response += "\n" + LabyUtilsPlugin.getPrefix() + "§bServer subtitle: §7" + subtitle;
        }
        if(sender.hasPermission("labyutils.info.economy")) {
            response += "\n" + LabyUtilsPlugin.getPrefix() +
                    "§bEconomy cash: §a" + labyPlayer.cashEconomy().getBalance() +
                    "\n" + LabyUtilsPlugin.getPrefix() +
                    "§bEconomy bank: §a" + labyPlayer.bankEconomy().getBalance();
        }
        if(sender.hasPermission("labyutils.info.version")) {
            response += "\n" + LabyUtilsPlugin.getPrefix() + "§bLabyMod version: §b" + labyPlayer.getLabyModVersion();
        }
        if(plugin.getConfig().getBoolean("subtitles.enabled")
                && sender.hasPermission("labyutils.info.region")) {
            String flag = labyPlayer.getTabListFlag() != null ? labyPlayer.getTabListFlag().toString() : "--";
            response += "\n" + LabyUtilsPlugin.getPrefix() + "§bRegion: §7" + flag;
        }
        sender.sendMessage(response);
        return true;
    }
}
