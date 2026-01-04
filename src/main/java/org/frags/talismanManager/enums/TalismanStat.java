package org.frags.talismanManager.enums;

public enum TalismanStat {

    DAMAGE_MULTIPLIER,
    DEFENSE_MULTIPLIER,
    XP_BONUS,
    SPEED;


    public static TalismanStat getStat(String stat) {
        return switch (stat.toLowerCase()) {
            case "damagemultiplier", "damage", "damage_bonus" -> DAMAGE_MULTIPLIER;
            case "defensemultiplier", "defense", "defense_reduction" -> DEFENSE_MULTIPLIER;
            case "xpbonus", "xp" -> XP_BONUS;
            case "speed" -> SPEED;
            default -> null;
        };
    }
}
