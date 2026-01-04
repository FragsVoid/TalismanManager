package org.frags.talismanManager.objects;

import org.bukkit.entity.EntityType;
import org.frags.talismanManager.enums.TalismanStat;

import java.util.Set;

public class StatAttribute {


    private final TalismanStat stat;
    private final double value;
    private final Set<EntityType> targetEntities;

    public StatAttribute(TalismanStat stat, double value, Set<EntityType> targetEntities) {
        this.stat = stat;
        this.value = value;
        this.targetEntities = targetEntities;
    }

    public TalismanStat getStat() {
        return stat;
    }

    public double getValue() {
        return value;
    }

    public boolean appliesTo(EntityType target) {
        if (targetEntities == null || targetEntities.isEmpty()) return true;
        return targetEntities.contains(target);
    }
}
