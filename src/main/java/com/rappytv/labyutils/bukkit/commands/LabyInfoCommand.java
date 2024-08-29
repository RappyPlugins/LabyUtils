package com.rappytv.labyutils.bukkit.commands;

import com.rappytv.labyutils.bukkit.LabyUtilsBukkit;
import com.rappytv.labyutils.common.ILabyUtilsPlugin;
import net.labymod.serverapi.api.model.component.ServerAPITextComponent;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import net.labymod.serverapi.server.bukkit.LabyModProtocolService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class LabyInfoCommand implements CommandExecutor, TabExecutor {

    private final LabyUtilsBukkit plugin;

    public LabyInfoCommand(LabyUtilsBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String prefix, @NotNull String[] args) {
        if(!sender.hasPermission("labyutils.info")) {
            sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cYou are not allowed to use this command!");
            return false;
        }
        if(args.length < 1) {
            sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cPlease enter a player name!");
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if(player == null) {
            sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cThis player was not found!");
            return false;
        }
        String response = LabyUtilsBukkit.getPrefix() + "§6LabyInfo of " + sender.getName();
        LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(player.getUniqueId());
        response += "\n" + LabyUtilsBukkit.getPrefix() + "§bUUID: §7" + player.getUniqueId();
        if(labyPlayer == null) {
            response += "\n" + LabyUtilsBukkit.getPrefix() + "§bUsing LabyMod: §cNo";
            sender.sendMessage(response);
            return true;
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
        sender.sendMessage(response);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if(args.length == 1) return null;
        return Collections.emptyList();
    }
}
