package org.frags.talismanManager;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;
import org.frags.talismanManager.commands.TalismanCommand;
import org.frags.talismanManager.enums.TalismanRarity;
import org.frags.talismanManager.listeners.DamageListener;
import org.frags.talismanManager.listeners.JoinListener;
import org.frags.talismanManager.manager.TalismanManager;
import org.frags.talismanManager.objects.Talisman;

import java.util.*;
import java.util.logging.Level;

public final class TalismanMain extends JavaPlugin {

    public final NamespacedKey talismanKey = new NamespacedKey(this, "talisman");
    private static TalismanMain instance;

    private Map<String, Talisman> talismans = new HashMap<>();
    private TalismanManager talismanManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        talismanManager = new TalismanManager(this);

        loadTalismans();

        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        TalismanCommand command = new TalismanCommand(this);
        getCommand("talisman").setExecutor(command);
        getCommand("talisman").setTabCompleter(command);
    }

    @Override
    public void onDisable() {

    }

    public void loadTalismans() {
        talismans.clear();

        ConfigurationSection section = getConfig().getConfigurationSection("talismanes");
        if (section == null) {
            getLogger().warning("No se encontró la sección 'talismanes' en config.yml");
            return;
        }

        int count = 0;

        for (String key : section.getKeys(false)) {
            try {
                ConfigurationSection talismanSection = section.getConfigurationSection(key);

                Talisman talisman = new Talisman(this, key, talismanSection);

                talismans.put(key.toLowerCase(), talisman);
                count++;

            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Error cargando el talismán: " + key, e);
            }
        }

        getLogger().info("Cargados " + count + " talismanes.");
    }

    public static TalismanMain getInstance() {
        return instance;
    }

    public TalismanManager getTalismanManager() {
        return talismanManager;
    }

    public Set<String> allIds() {
        return talismans.keySet();
    }

    public Talisman getTalisman(String id) {
        if (id == null) return null;
        return talismans.get(id.toLowerCase());
    }
}
