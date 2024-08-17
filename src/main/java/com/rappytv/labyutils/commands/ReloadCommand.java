package com.rappytv.labyutils.commands;

import com.rappytv.labyutils.LabyUtilsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final LabyUtilsPlugin plugin;

    public ReloadCommand(LabyUtilsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String prefix, @NotNull String[] args) {
        if(!sender.hasPermission("labyutils.reload")) {
            sender.sendMessage("§cYou are not allowed to use this command!");
            return false;
        }
        plugin.reloadConfig();
        sender.sendMessage("§9Addon config successfully reloaded!");
        return true;
    }
}
