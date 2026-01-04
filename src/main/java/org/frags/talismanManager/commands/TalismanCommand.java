package org.frags.talismanManager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.frags.customItems.CustomItems;
import org.frags.talismanManager.TalismanMain;
import org.frags.talismanManager.menu.TalismanMenu;
import org.frags.talismanManager.objects.Talisman;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TalismanCommand implements CommandExecutor, TabCompleter {

    private final TalismanMain plugin;

    public TalismanCommand(TalismanMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return true;
        }

        if (args.length == 0) {
            new TalismanMenu(plugin, CustomItems.getInstance().getMenuManager().getPlayerMenuUtility(player)).open();
            return true;
        }

        if (!player.hasPermission("talisman.get")) {
            player.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.loadTalismans();
            player.sendMessage(ChatColor.GREEN + "Talisman config reloaded.");
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            plugin.getTalismanManager().saveTalismanBag(player, new ItemStack[0]);
            player.sendMessage(ChatColor.GREEN + "Talisman config reset.");
            return true;
        }

        if (!args[0].equalsIgnoreCase("get")) {
            player.sendMessage("Usage: /talisman get <id>");
            return true;
        }

        else if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments.");
        }

        String id = args[1];
        Talisman talisman = plugin.getTalisman(id);
        if (talisman == null) {
            player.sendMessage(ChatColor.RED + "That id is null!");
            return true;
        }

        ItemStack itemStack = talisman.buildItem();
        player.getInventory().addItem(itemStack);
        player.sendMessage(ChatColor.GREEN + "Talisman has been created!");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(commandSender instanceof Player player)) {
            return List.of();
        }

        if (!player.hasPermission("talisman.get")) {
            return List.of();
        }

        if (args.length == 1) {
            return List.of("get", "reload", "reset");
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("get")) {
                return StringUtil.copyPartialMatches(args[1], plugin.allIds(), new ArrayList<>());
            } else if (args[0].equalsIgnoreCase("reset")) {
                return StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), new ArrayList<>());
            }
        }

        return List.of();
    }
}
