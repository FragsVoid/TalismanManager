package org.frags.talismanManager.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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

            double currentDamage = e.getDamage();

            double strength = plugin.getTalismanManager().getStatValue(player, TalismanStat.DAMAGE_MULTIPLIER, e.getEntity().getType());
            double damageMultiplier = 1 + (strength / 100.0);

            double finalDamageBonus = plugin.getTalismanManager().getStatValue(player, TalismanStat.DAMAGE_ADITIVE, e.getEntity().getType());
            double finalMult = 1 + (finalDamageBonus / 100.0);

            double totalDamage = currentDamage * damageMultiplier * finalMult;
            e.setDamage(totalDamage);

            if (e.getEntity() instanceof LivingEntity victim) {
                double penetration = plugin.getTalismanManager().getStatValue(
                        player,
                        TalismanStat.ARMOR_PENETRATION,
                        victim.getType()
                );

                if (penetration > 0) {
                    AttributeInstance armorAttr = victim.getAttribute(Attribute.ARMOR);
                    double armorPoints = (armorAttr != null) ? armorAttr.getBaseValue() : 0;

                    if (armorPoints > 0) {
                        double currentReduction = Math.min(0.99, armorPoints * 0.04);

                        double effectiveReduction = currentReduction * (1.0 - penetration);

                        double damageGoal = currentDamage * (1.0 - effectiveReduction);
                        double requiredDamage = damageGoal / (1.0 - currentReduction);

                        e.setDamage(Math.max(currentDamage, requiredDamage));

                        currentDamage = e.getDamage();
                    }
                }
            }

            double lifeStealPercent = plugin.getTalismanManager().getStatValue(player,
                    TalismanStat.LIFE_STEAL,
                    e.getEntity().getType());

            if (lifeStealPercent > 0) {
                double healAmount = currentDamage * lifeStealPercent;
                double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
                double currentHealth = player.getHealth();

                if (currentHealth < maxHealth) {
                    double newHealth = Math.min(currentHealth + healAmount, maxHealth);
                    player.setHealth(newHealth);
                }
            }
        }

//        if (e.getEntity() instanceof Player player) {
//
//            double reduction = plugin.getTalismanManager().getStatValue(
//                player,
//                TalismanStat.DAMAGE_MULTIPLIER,
//                e.getDamager().getType()
//            );
//
//            if (reduction > 0) {
//                double mult = Math.max(0, 1.0 - reduction);
//                e.setDamage(e.getDamage() * mult);
//            }
//        }
    }

    @EventHandler
    public void onEveryDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {

            Entity attacker = e.getDamageSource().getCausingEntity();
            EntityType attackerType = (attacker != null) ? attacker.getType() : null;

            double defensePoints = plugin.getTalismanManager().getStatValue(
                    player,
                    TalismanStat.DEFENSE_MULTIPLIER,
                    attackerType
            );

            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                double fallBonus = plugin.getTalismanManager().getStatValue(
                        player,
                        TalismanStat.FALL_DEFENSE,
                        null
                );
                defensePoints += fallBonus;
            }

            if (e.getCause() == EntityDamageEvent.DamageCause.LAVA || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                defensePoints += plugin.getTalismanManager().getStatValue(player, TalismanStat.FIRE_DEFENSE, null);
            }

            if (defensePoints > 0) {
                double constant = 100.0;
                double reductionMultiplier = constant / (constant + defensePoints);
                e.setDamage(e.getDamage() * reductionMultiplier);
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
