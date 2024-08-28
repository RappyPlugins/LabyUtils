package com.rappytv.labyutils.bukkit.commands;

import com.rappytv.labyutils.bukkit.LabyUtilsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final LabyUtilsPlugin plugin;

    public ReloadCommand(LabyUtilsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String prefix, String[] args) {
        if(!sender.hasPermission("labyutils.reload")) {
            sender.sendMessage(LabyUtilsPlugin.getPrefix() + "§cYou are not allowed to use this command!");
            return false;
        }
        plugin.getConfigManager().reloadConfig();
        sender.sendMessage(LabyUtilsPlugin.getPrefix() + "§7Addon config successfully reloaded!");
        return true;
    }
}
