package com.rappytv.labyutils.commands;

import com.rappytv.labyutils.LabyUtilsPlugin;
import com.rappytv.labyutils.listeners.PlayerListener;
import net.labymod.serverapi.api.model.component.ServerAPITextComponent;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import net.labymod.serverapi.server.bukkit.LabyModProtocolService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class LabyInfoCommand implements CommandExecutor {

    private final LabyUtilsPlugin plugin;

    public LabyInfoCommand(LabyUtilsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String prefix, @NotNull String[] args) {
        if(!sender.hasPermission("labyutils.info")) {
            sender.sendMessage(LabyUtilsPlugin.getPrefix() + "§cYou are not allowed to use this command!");
            return false;
        }
        if(args.length < 1) {
            sender.sendMessage(LabyUtilsPlugin.getPrefix() + "§cPlease enter a player name!");
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if(player == null) {
            sender.sendMessage(LabyUtilsPlugin.getPrefix() + "§cThis player was not found!");
            return false;
        }
        String response = LabyUtilsPlugin.getPrefix() + "§6LabyInfo of " + sender.getName();
        LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(player.getUniqueId());
        response += "\n" + LabyUtilsPlugin.getPrefix() + "§bUUID: §7" + player.getUniqueId();
        if(labyPlayer == null) {
            response += "\n" + LabyUtilsPlugin.getPrefix() + "§bUsing LabyMod: §cNo";
            sender.sendMessage(response);
            return true;
        }
        response += "\n" + LabyUtilsPlugin.getPrefix() + "§bUsing LabyMod: §aYes";
        if(plugin.getConfigManager().areSubtitlesEnabled()
                && sender.hasPermission("labyutils.info.subtitle")) {
            ServerAPITextComponent component = (ServerAPITextComponent) labyPlayer.subtitle().getText();
            String subtitle = component != null ? component.getText() : "--";
            response += "\n" + LabyUtilsPlugin.getPrefix() + "§bServer subtitle: §7" + subtitle;
        }
        if(sender.hasPermission("labyutils.info.economy")) {
            response += "\n" + LabyUtilsPlugin.getPrefix() +
                    "§bEconomy cash: §7" + formatNumber(labyPlayer.cashEconomy().getBalance()) +
                    "\n" + LabyUtilsPlugin.getPrefix() +
                    "§bEconomy bank: §7" + formatNumber(labyPlayer.bankEconomy().getBalance());
        }
        if(sender.hasPermission("labyutils.info.version")) {
            response += "\n" + LabyUtilsPlugin.getPrefix() + "§bLabyMod version: §7v" + labyPlayer.getLabyModVersion();
        }
        if(sender.hasPermission("labyutils.info.region")) {
            String flag = PlayerListener.cachedFlags.containsKey(player.getUniqueId())
                    ? PlayerListener.cachedFlags.get(player.getUniqueId()).name()
                    : "--";
            response += "\n" + LabyUtilsPlugin.getPrefix() + "§bRegion: §7" + flag;
        }
        sender.sendMessage(response);
        return true;
    }

    private String formatNumber(double number) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        formatter.setMaximumFractionDigits(2);
        formatter.setDecimalFormatSymbols(symbols);

        return formatter.format(number);
    }
}
