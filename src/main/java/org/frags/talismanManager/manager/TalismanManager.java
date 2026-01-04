package org.frags.talismanManager.manager;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.frags.talismanManager.TalismanMain;
import org.frags.talismanManager.enums.TalismanStat;
import org.frags.talismanManager.objects.StatAttribute;
import org.frags.talismanManager.objects.Talisman;
import org.frags.talismanManager.utils.ItemSerialization;

import java.util.*;

public class TalismanManager {

    private final Map<UUID, Map<TalismanStat, List<StatAttribute>>> activeBonuses = new HashMap<>();
    private final TalismanMain plugin;
    private final NamespacedKey STORAGE_KEY;

    public TalismanManager(TalismanMain plugin) {
        this.plugin = plugin;
        this.STORAGE_KEY = new NamespacedKey(plugin, "talisman_bag_data");
    }

    public void recalculateStats(Player player, ItemStack[] bagContents) {
        Map<TalismanStat, List<StatAttribute>> stats = new HashMap<>();

        for (ItemStack item : bagContents) {
            if (item == null || !item.hasItemMeta()) continue;

            String id = item.getItemMeta().getPersistentDataContainer().get(plugin.talismanKey, PersistentDataType.STRING);
            if (id == null) continue;

            Talisman talisman = plugin.getTalisman(id);
            if (talisman != null) {
                for (StatAttribute attr : talisman.getAttributes()) {
                    stats.computeIfAbsent(attr.getStat(), k -> new ArrayList<>()).add(attr);
                }
            }
        }

        activeBonuses.put(player.getUniqueId(), stats);
    }

    public void clearPlayer(Player player) {
        activeBonuses.remove(player.getUniqueId());
    }

    public void saveTalismanBag(Player player, ItemStack[] bagContents) {
        byte[] data = ItemSerialization.toBytes(bagContents);

        player.getPersistentDataContainer().set(STORAGE_KEY, PersistentDataType.BYTE_ARRAY, data);

        recalculateStats(player, bagContents);
    }

    public ItemStack[] loadTalismanBag(Player player) {
        if (!player.getPersistentDataContainer().has(STORAGE_KEY, PersistentDataType.BYTE_ARRAY)) {
            return new ItemStack[0];
        }

        byte[] data = player.getPersistentDataContainer().get(STORAGE_KEY, PersistentDataType.BYTE_ARRAY);

        return ItemSerialization.fromBytes(data);
    }

    public double getStatValue(Player player, TalismanStat stat, EntityType target) {
        if (!activeBonuses.containsKey(player.getUniqueId())) return 0.0;
        Map<TalismanStat, List<StatAttribute>> stats = activeBonuses.get(player.getUniqueId());
        if (!stats.containsKey(stat)) return 0.0;
        double total = 0.0;

        for (StatAttribute attr : stats.get(stat)) {
            if (attr.appliesTo(target)) { total += attr.getValue(); }
        }
        return total;
    }
}
