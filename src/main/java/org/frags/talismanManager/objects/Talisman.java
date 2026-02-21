package org.frags.talismanManager.objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.frags.customItems.CustomItems;
import org.frags.talismanManager.TalismanMain;
import org.frags.talismanManager.enums.TalismanRarity;
import org.frags.talismanManager.enums.TalismanStat;

import java.util.*;

public class Talisman {

    protected final TalismanMain plugin;
    private final String id;
    private final TalismanRarity rarity;
    private String itemId;

    private final List<StatAttribute> attributes = new ArrayList<>();

    public Talisman(TalismanMain plugin, String id, ConfigurationSection section) {
        this.plugin = plugin;
        this.id = id;
        this.rarity = TalismanRarity.getRarity(section.getString("rarity"));
        this.itemId = section.getString("item_id");

        if (section.contains("attributes")) {
            for (Map<?, ?> attrMap : section.getMapList("attributes")) {

                TalismanStat type = TalismanStat.getStat((String)attrMap.get("type"));

                double val = ((Number)attrMap.get("value")).doubleValue();

                Set<EntityType> entities = new HashSet<>();
                Map<String, Object> conditions = (Map<String, Object>) attrMap.get("conditions");

                if (conditions != null && conditions.containsKey("entities")) {
                    List<String> entityNames = (List<String>) conditions.get("entities");
                    for (String name : entityNames) {
                        entities.add(EntityType.valueOf(name));
                    }
                }

                attributes.add(new StatAttribute(type, val, entities));
            }
        }
    }

    public List<StatAttribute> getAttributes() {
        return attributes;
    }

    public String getId() {
        return id;
    }

    public TalismanRarity getRarity() {
        return rarity;
    }

    public ItemStack buildItem() {
        ItemStack originalItem = CustomItems.getInstance().getItemProvider().getItem(itemId);

        if (originalItem == null) {
            throw new RuntimeException("Item " + itemId + " not found!");
        }

        ItemStack baseItem = originalItem.clone();

        ItemMeta meta = baseItem.getItemMeta();
        meta.getPersistentDataContainer().set(plugin.talismanKey, PersistentDataType.STRING, id);
        meta.getPersistentDataContainer().set(plugin.randomKey, PersistentDataType.STRING, UUID.randomUUID().toString());
        baseItem.setItemMeta(meta);

        return baseItem;
    }

}
