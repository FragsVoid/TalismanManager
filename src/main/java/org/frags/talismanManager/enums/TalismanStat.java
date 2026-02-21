package org.frags.talismanManager.enums;

public enum TalismanStat {

    DAMAGE_MULTIPLIER,
    DAMAGE_ADITIVE,
    DEFENSE_MULTIPLIER,
    FALL_DEFENSE,
    XP_BONUS,
    LIFE_STEAL,
    SPEED,
    FIRE_DEFENSE,
    ARMOR_PENETRATION;


    public static TalismanStat getStat(String stat) {
        return switch (stat.toLowerCase()) {
            case "damagemultiplier", "damage", "damage_bonus" -> DAMAGE_MULTIPLIER;
            case "damageaditive", "damage_aditive" -> DAMAGE_ADITIVE;
            case "defensemultiplier", "defense", "defense_reduction" -> DEFENSE_MULTIPLIER;
            case "xpbonus", "xp" -> XP_BONUS;
            case "speed" -> SPEED;
            case "lifesteal", "life" -> LIFE_STEAL;
            case "fall", "fall_defense" -> FALL_DEFENSE;
            case "fire", "lava", "fire_defense" -> FIRE_DEFENSE;
            case "armor", "armor_penetration" -> ARMOR_PENETRATION;
            default -> null;
        };
    }
}
