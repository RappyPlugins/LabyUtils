package com.rappytv.labyutils.bungee.commands;

import com.rappytv.labyutils.bukkit.LabyUtilsBukkit;
import com.rappytv.labyutils.bungee.LabyUtilsBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCommand extends Command {

    private final LabyUtilsBungee plugin;

    public ReloadCommand(LabyUtilsBungee plugin) {
        super("labyutils", "labyutils.reload");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission("labyutils.reload")) {
            sender.sendMessage(TextComponent.fromLegacyText(
                    LabyUtilsBukkit.getPrefix() + "§cYou are not allowed to use this command!"
            ));
            return;
        }
        plugin.getConfigManager().reloadConfig();
        sender.sendMessage(
                TextComponent.fromLegacyText(LabyUtilsBukkit.getPrefix() + "§7Addon config successfully reloaded!"
        ));
    }
}
