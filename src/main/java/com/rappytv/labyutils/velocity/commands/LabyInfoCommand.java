package com.rappytv.labyutils.velocity.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.rappytv.labyutils.common.ILabyUtilsPlugin;
import com.rappytv.labyutils.velocity.LabyUtilsVelocity;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.labymod.serverapi.api.model.component.ServerAPITextComponent;
import net.labymod.serverapi.server.velocity.LabyModPlayer;
import net.labymod.serverapi.server.velocity.LabyModProtocolService;

public class LabyInfoCommand {

    public static BrigadierCommand createBrigadierCommand(final LabyUtilsVelocity plugin) {
        LiteralCommandNode<CommandSource> infoNode = BrigadierCommand.literalArgumentBuilder("labyutils")
                .requires(source -> source.hasPermission("labyutils.info"))
                .executes(context -> {
                    plugin.getConfigManager().reloadConfig();
                    context.getSource().sendMessage(Component.text(
                            LabyUtilsVelocity.getPrefix() + "§7Addon config successfully reloaded!"
                    ));

                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            plugin.getServer().getAllPlayers().forEach(player -> builder.suggest(player.getUsername()));
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String name = context.getArgument("player", String.class);
                            plugin.getServer().getPlayer(name).ifPresentOrElse(player -> {
                                String response = LabyUtilsVelocity.getPrefix() + "§6LabyInfo of " + player.getUsername();
                                LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(player.getUniqueId());
                                response += "\n" + LabyUtilsVelocity.getPrefix() + "§bUUID: §7" + player.getUniqueId();
                                if(labyPlayer == null) {
                                    response += "\n" + LabyUtilsVelocity.getPrefix() + "§bUsing LabyMod: §cNo";
                                    context.getSource().sendMessage(Component.text(response));
                                    return;
                                }
                                response += "\n" + LabyUtilsVelocity.getPrefix() + "§bUsing LabyMod: §aYes";
                                if(plugin.getConfigManager().areSubtitlesEnabled()
                                        && context.getSource().hasPermission("labyutils.info.subtitle")) {
                                    ServerAPITextComponent component = (ServerAPITextComponent) labyPlayer.subtitle().getText();
                                    String subtitle = component != null ? component.getText() : "--";
                                    response += "\n" + LabyUtilsVelocity.getPrefix() + "§bServer subtitle: §7" + subtitle;
                                }
                                if(context.getSource().hasPermission("labyutils.info.economy")) {
                                    response += "\n" + LabyUtilsVelocity.getPrefix() +
                                            "§bEconomy cash: §7" + plugin.formatNumber(labyPlayer.cashEconomy().getBalance()) +
                                            "\n" + LabyUtilsVelocity.getPrefix() +
                                            "§bEconomy bank: §7" + plugin.formatNumber(labyPlayer.bankEconomy().getBalance());
                                }
                                if(context.getSource().hasPermission("labyutils.info.version")) {
                                    response += "\n" + LabyUtilsVelocity.getPrefix() + "§bLabyMod version: §7v" + labyPlayer.getLabyModVersion();
                                }
                                if(context.getSource().hasPermission("labyutils.info.region")) {
                                    String flag = ILabyUtilsPlugin.cachedFlags.containsKey(player.getUniqueId())
                                            ? ILabyUtilsPlugin.cachedFlags.get(player.getUniqueId()).name()
                                            : "--";
                                    response += "\n" + LabyUtilsVelocity.getPrefix() + "§bRegion: §7" + flag;
                                }
                                context.getSource().sendMessage(Component.text(response));
                            }, () -> context.getSource().sendMessage(Component.text(
                                    LabyUtilsVelocity.getPrefix() + "§cThis player was not found!"
                            )));

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();

        return new BrigadierCommand(infoNode);
    }
}
