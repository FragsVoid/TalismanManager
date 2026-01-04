package org.frags.talismanManager.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.frags.talismanManager.TalismanMain;
import org.frags.talismanManager.enums.TalismanStat;

public class DamageListener implements Listener {

    private final TalismanMain plugin;

    public DamageListener(TalismanMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player player) {
            double bonus = plugin.getTalismanManager().getStatValue(
                    player,
                    TalismanStat.DAMAGE_MULTIPLIER,
                    e.getEntity().getType()
            );

            if (bonus > 0) {
                e.setDamage(e.getDamage() * (1 + bonus));
            }
        }

        if (e.getEntity() instanceof Player player) {

            double reduction = plugin.getTalismanManager().getStatValue(
                player,
                TalismanStat.DAMAGE_MULTIPLIER,
                e.getDamager().getType()
            );

            if (reduction > 0) {
                double mult = Math.max(0, 1.0 - reduction);
                e.setDamage(e.getDamage() * mult);
            }
        }
    }


    @EventHandler
    public void onXpGain(PlayerExpChangeEvent e) {
        Player p = e.getPlayer();
        double xpBonus = plugin.getTalismanManager().getStatValue(
                p,
                TalismanStat.XP_BONUS,
                null
        );

        if (xpBonus > 0) {
            int originalXp = e.getAmount();
            int extraXp = (int) (originalXp * xpBonus);
            e.setAmount(originalXp + extraXp);
        }
    }
}
