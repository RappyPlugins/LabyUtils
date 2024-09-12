package com.rappytv.labyutils.velocity.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.rappytv.labyutils.velocity.LabyUtilsVelocity;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReloadCommand {

    public static BrigadierCommand createBrigadierCommand(final LabyUtilsVelocity plugin) {
        LiteralCommandNode<CommandSource> reloadNode = BrigadierCommand.literalArgumentBuilder("labyutils")
                .requires(source -> source.hasPermission("labyutils.reload"))
                .executes(context -> {
                    plugin.getConfigManager().reloadConfig();
                    context.getSource().sendMessage(LabyUtilsVelocity.getPrefix().append(Component.text(
                            "Addon config successfully reloaded!",
                            NamedTextColor.GRAY
                    )));

                    return Command.SINGLE_SUCCESS;
                })
                .build();

        return new BrigadierCommand(reloadNode);
    }
}
