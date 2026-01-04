package org.frags.talismanManager.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TalismanAddEvent extends Event {

    private Player player;
    private String talismanId;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public TalismanAddEvent(Player player, String talismanId) {
        this.player = player;
        this.talismanId = talismanId;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public String getTalismanId() {
        return talismanId;
    }
}
