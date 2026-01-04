package org.frags.talismanManager.enums;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum TalismanRarity {

    COMMON(NamedTextColor.WHITE, "Común"),
    RARE(NamedTextColor.BLUE, "Raro"),
    LEGENDARY(NamedTextColor.GOLD, "Legendario");

    private final TextColor color;
    private final String displayName;

    TalismanRarity(TextColor color, String displayName) {
        this.color = color;
        this.displayName = displayName;
    }

    public TextColor getColor() {
        return color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TalismanRarity getRarity(String rarity) {
        return switch (rarity.toLowerCase()) {
            case "common" -> COMMON;
            case "rare" -> RARE;
            case "legendary" -> LEGENDARY;
            default -> null;
        };
    }
}
