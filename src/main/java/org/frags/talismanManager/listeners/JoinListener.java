package org.frags.talismanManager.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.frags.talismanManager.TalismanMain;

public class JoinListener implements Listener {

    private final TalismanMain plugin;

    public JoinListener(TalismanMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ItemStack[] savedItems = plugin.getTalismanManager().loadTalismanBag(event.getPlayer());

        plugin.getTalismanManager().recalculateStats(event.getPlayer(), savedItems);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getTalismanManager().clearPlayer(event.getPlayer());
    }
}
