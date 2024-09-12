package com.rappytv.labyutils.velocity.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.rappytv.labyutils.common.ILabyUtilsPlugin;
import com.rappytv.labyutils.velocity.LabyUtilsVelocity;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.labymod.serverapi.api.model.component.ServerAPITextComponent;
import net.labymod.serverapi.server.velocity.LabyModPlayer;
import net.labymod.serverapi.server.velocity.LabyModProtocolService;

public class LabyInfoCommand {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static BrigadierCommand createBrigadierCommand(final LabyUtilsVelocity plugin) {
        Component prefix = LabyUtilsVelocity.getPrefix();
        LiteralCommandNode<CommandSource> infoNode = BrigadierCommand.literalArgumentBuilder("labyinfo")
                .requires(source -> source.hasPermission("labyutils.info"))
                .executes(context -> {
                    plugin.getConfigManager().reloadConfig();
                    context.getSource().sendMessage(prefix.append(Component.text(
                            "Please enter a player name!",
                            NamedTextColor.RED
                    )));

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
                                Component response = Component.empty()
                                        .append(prefix)
                                        .append(Component.text(
                                                "LabyInfo of " + player.getUsername(), NamedTextColor.GOLD
                                        ))
                                        .appendNewline()
                                        .append(prefix)
                                        .append(Component.text("UUID: ", NamedTextColor.AQUA))
                                        .append(Component.text(player.getUniqueId().toString(), NamedTextColor.GRAY))
                                        .appendNewline()
                                        .append(prefix)
                                        .append(Component.text("Using LabyMod: ", NamedTextColor.AQUA));
                                LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(player.getUniqueId());
                                if(labyPlayer == null) {
                                    context.getSource().sendMessage(response.append(
                                            Component.text("No", NamedTextColor.RED)
                                    ));
                                    return;
                                }
                                response.append(Component.text("Yes", NamedTextColor.GREEN));
                                if(plugin.getConfigManager().areSubtitlesEnabled()
                                        && context.getSource().hasPermission("labyutils.info.subtitle")) {
                                    ServerAPITextComponent component = (ServerAPITextComponent) labyPlayer.subtitle().getText();
                                    String subtitle = component != null ? component.getText() : "--";
                                    response
                                            .appendNewline()
                                            .append(prefix)
                                            .append(Component.text("Server subtitle: ", NamedTextColor.AQUA))
                                            .append(LegacyComponentSerializer.legacyAmpersand().deserialize(
                                                    "§7" + subtitle
                                            ));
                                }
                                if(context.getSource().hasPermission("labyutils.info.economy")) {
                                    response
                                            .appendNewline()
                                            .append(prefix)
                                            .append(Component.text("Economy cash: ", NamedTextColor.AQUA))
                                            .append(Component.text(
                                                    plugin.formatNumber(labyPlayer.cashEconomy().getBalance()),
                                                    NamedTextColor.GRAY
                                            ))
                                            .appendNewline()
                                            .append(prefix)
                                            .append(Component.text("Economy bank: ", NamedTextColor.AQUA))
                                            .append(Component.text(
                                                    plugin.formatNumber(labyPlayer.bankEconomy().getBalance()),
                                                    NamedTextColor.GRAY
                                            ));
                                }
                                if(context.getSource().hasPermission("labyutils.info.version")) {
                                    response
                                            .appendNewline()
                                            .append(prefix)
                                            .append(Component.text("LabyMod version: ", NamedTextColor.AQUA))
                                            .append(Component.text(labyPlayer.getLabyModVersion()));
                                }
                                if(context.getSource().hasPermission("labyutils.info.region")) {
                                    String flag = ILabyUtilsPlugin.cachedFlags.containsKey(player.getUniqueId())
                                            ? ILabyUtilsPlugin.cachedFlags.get(player.getUniqueId()).name()
                                            : "--";
                                    response
                                            .appendNewline()
                                            .append(prefix)
                                            .append(Component.text("Region: ", NamedTextColor.AQUA))
                                            .append(Component.text(flag, NamedTextColor.GRAY));
                                }
                                context.getSource().sendMessage(response);
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
