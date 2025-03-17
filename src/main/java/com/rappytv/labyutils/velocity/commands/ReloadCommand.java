package com.rappytv.labyutils.velocity.commands;

import com.rappytv.labyutils.velocity.LabyUtilsVelocity;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

public class ReloadCommand implements SimpleCommand {

    private final LabyUtilsVelocity plugin;

    public ReloadCommand(LabyUtilsVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        if (!sender.hasPermission("labyutils.reload")) {
            sender.sendMessage(Component.text(LabyUtilsVelocity.getPrefix() + "§cYou are not allowed to use this command!"));
            return;
        }
        plugin.getConfigManager().reloadConfig();
        sender.sendMessage(Component.text(LabyUtilsVelocity.getPrefix() + "§7Plugin config successfully reloaded!"));
    }
}
