package com.rappytv.labyutils.bukkit.commands;

import com.rappytv.labyutils.bukkit.LabyUtilsBukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final LabyUtilsBukkit plugin;

    public ReloadCommand(LabyUtilsBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String prefix, String[] args) {
        if(!sender.hasPermission("labyutils.reload")) {
            sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cYou are not allowed to use this command!");
            return false;
        }
        plugin.getConfigManager().reloadConfig();
        sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§7Addon config successfully reloaded!");
        return true;
    }
}
