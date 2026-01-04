package org.frags.talismanManager.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.frags.customItems.menu.Menu;
import org.frags.customItems.menu.PlayerMenuUtility;
import org.frags.talismanManager.TalismanMain;
import org.frags.talismanManager.events.TalismanAddEvent;

public class TalismanMenu extends Menu<TalismanMain, PlayerMenuUtility> {

    public TalismanMenu(TalismanMain plugin, PlayerMenuUtility playerMenuUtility) {
        super(plugin, playerMenuUtility);
    }

    @Override
    public Component getMenuName() {
        return Component.text("Bolsa de Talismanes");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (event.getClick().isKeyboardClick()) {
            event.setCancelled(true);
            return;
        }



        if (event.getClickedInventory().equals(inventory)) {
            if (cursorItem != null && !cursorItem.getType().isAir()) {
                if (isTalisman(cursorItem)) {
                    event.setCancelled(false);

                    plugin.getServer().getPluginManager().callEvent(new TalismanAddEvent(playerMenuUtility.getPlayer(), getTalismanId(cursorItem)));
                } else {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "¡Solo puedes guardar talismanes!");
                }
            } else if (clickedItem != null && !clickedItem.getType().isAir()) {
                event.setCancelled(false);
            }
        } else {
            if (event.isShiftClick()) {
                if (inventory.firstEmpty() == -1)
                    return;
                if (isTalisman(clickedItem)) {
                    event.setCancelled(false);

                    plugin.getServer().getPluginManager().callEvent(new TalismanAddEvent(playerMenuUtility.getPlayer(), getTalismanId(clickedItem)));
                } else {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "¡Solo puedes guardar talismanes!");
                }
            }

            else {
                event.setCancelled(false);
            }
        }

    }

    @Override
    public void handleClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        ItemStack[] contents = event.getInventory().getContents();

        plugin.getTalismanManager().saveTalismanBag(player, contents);
    }

    @Override
    public void setMenuItems() {
        ItemStack[] savedItems = plugin.getTalismanManager().loadTalismanBag(playerMenuUtility.getPlayer());
        inventory.setContents(savedItems);
    }

    private boolean isTalisman(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(plugin.talismanKey, PersistentDataType.STRING);
    }

    private String getTalismanId(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().get(plugin.talismanKey, PersistentDataType.STRING);
    }
}
