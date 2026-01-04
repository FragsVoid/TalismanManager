package org.frags.talismanManager.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.util.ArrayList;
import java.util.List;

public class ItemSerialization {


    public static byte[] toBytes(ItemStack[] items) {
        List<ItemStack> safeList = new ArrayList<>(items.length);

        for (ItemStack item : items) {
            if (item == null || item.getType().isAir())
                continue;
            safeList.add(item);
        }

        return ItemStack.serializeItemsAsBytes(safeList);
    }

    public static ItemStack[] fromBytes(byte[] data) {
        if (data == null || data.length == 0)
            return new ItemStack[0];

        return ItemStack.deserializeItemsFromBytes(data);
    }
}
