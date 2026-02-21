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
import java.util.Collections;
import java.util.List;

public class TalismanCommand implements CommandExecutor, TabCompleter {

    private final TalismanMain plugin;

    public TalismanCommand(TalismanMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length == 0) {
            if (sender instanceof Player player) {
                new TalismanMenu(plugin, CustomItems.getInstance().getMenuManager().getPlayerMenuUtility(player)).open();
            } else {
                sender.sendMessage(ChatColor.RED + "La consola no puede abrir menús. Usa /talisman give <player> <id>");
            }
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("reload")) {
            if (!sender.hasPermission("talisman.admin")) {
                sender.sendMessage(ChatColor.RED + "No tienes permiso.");
                return true;
            }
            plugin.reloadConfig();
            plugin.loadTalismans();
            sender.sendMessage(ChatColor.GREEN + "Talisman config reloaded.");
            return true;
        }

        if (subCommand.equals("reset")) {
            if (!sender.hasPermission("talisman.admin")) {
                sender.sendMessage(ChatColor.RED + "No tienes permiso.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Uso: /talisman reset <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Jugador no encontrado.");
                return true;
            }
            plugin.getTalismanManager().saveTalismanBag(target, new ItemStack[0]);
            sender.sendMessage(ChatColor.GREEN + "Bolsa de talismanes reseteada para " + target.getName());
            return true;
        }

        if (subCommand.equals("get")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "La consola debe usar: /talisman give <player> <id>");
                return true;
            }
            if (!player.hasPermission("talisman.get")) {
                player.sendMessage(ChatColor.RED + "No tienes permiso.");
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Uso: /talisman get <id>");
                return true;
            }
            giveTalisman(sender, player, args[1]);
            return true;
        }

        if (subCommand.equals("give")) {
            if (!sender.hasPermission("talisman.give")) {
                sender.sendMessage(ChatColor.RED + "No tienes permiso.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Uso: /talisman give <player> <id>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Jugador " + args[1] + " no encontrado.");
                return true;
            }
            giveTalisman(sender, target, args[2]);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Comando desconocido. Usa /talisman");
        return true;
    }

    private void giveTalisman(CommandSender sender, Player receiver, String talismanId) {
        Talisman talisman = plugin.getTalisman(talismanId);
        if (talisman == null) {
            sender.sendMessage(ChatColor.RED + "El talismán con ID '" + talismanId + "' no existe.");
            return;
        }

        ItemStack itemStack = talisman.buildItem();

        if (receiver.getInventory().firstEmpty() == -1) {
            receiver.getWorld().dropItem(receiver.getLocation(), itemStack);
            receiver.sendMessage(ChatColor.YELLOW + "Tu inventario estaba lleno, el talismán cayó al suelo.");
        } else {
            receiver.getInventory().addItem(itemStack);
        }

        if (!sender.equals(receiver)) {
            sender.sendMessage(ChatColor.GREEN + "Has dado " + talismanId + " a " + receiver.getName());
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("talisman.get")) completions.add("get");
            if (sender.hasPermission("talisman.give")) completions.add("give");
            if (sender.hasPermission("talisman.admin")) {
                completions.add("reload");
                completions.add("reset");
            }
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();

            if (sub.equals("get") && sender.hasPermission("talisman.get")) {
                return StringUtil.copyPartialMatches(args[1], plugin.allIds(), new ArrayList<>());
            }

            if ((sub.equals("give") || sub.equals("reset")) && sender.hasPermission("talisman.give")) {
                return null;
            }
        }

        if (args.length == 3) {
            String sub = args[0].toLowerCase();

            if (sub.equals("give") && sender.hasPermission("talisman.give")) {
                return StringUtil.copyPartialMatches(args[2], plugin.allIds(), new ArrayList<>());
            }
        }

        return Collections.emptyList();
    }
}