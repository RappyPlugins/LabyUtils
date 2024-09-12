package com.rappytv.labyutils.velocity.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.rappytv.labyutils.velocity.LabyUtilsVelocity;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;

public class ReloadCommand {

    public static BrigadierCommand createBrigadierCommand(final LabyUtilsVelocity plugin) {
        LiteralCommandNode<CommandSource> reloadNode = BrigadierCommand.literalArgumentBuilder("labyutils")
                .requires(source -> source.hasPermission("labyutils.reload"))
                .executes(context -> {
                    plugin.getConfigManager().reloadConfig();
                    context.getSource().sendMessage(Component.text(
                            LabyUtilsVelocity.getPrefix() + "§7Addon config successfully reloaded!"
                    ));

                    return Command.SINGLE_SUCCESS;
                })
                .build();

        return new BrigadierCommand(reloadNode);
    }
}
