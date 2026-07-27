package dev.elysium.core.event;

import dev.elysium.core.player.ElysiumPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fire khi mana cua player thay doi.
 * ElysiumCombat listen de cap nhat skill item lore.
 */
public class ElysiumManaChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    private final Player        player;
    private final ElysiumPlayer elysiumPlayer;
    private final int           oldMana;
    private final int           newMana;
    private final ChangeReason  reason;

    public enum ChangeReason { SKILL_USE, REGEN, ADMIN, OTHER }

    public ElysiumManaChangeEvent(Player player, ElysiumPlayer ep,
                                  int oldMana, int newMana, ChangeReason reason) {
        this.player = player; this.elysiumPlayer = ep;
        this.oldMana = oldMana; this.newMana = newMana; this.reason = reason;
    }

    public Player        getPlayer()        { return player; }
    public ElysiumPlayer getElysiumPlayer() { return elysiumPlayer; }
    public int           getOldMana()       { return oldMana; }
    public int           getNewMana()       { return newMana; }
    public int           getDelta()         { return newMana - oldMana; }
    public ChangeReason  getReason()        { return reason; }

    @Override public boolean isCancelled()        { return cancelled; }
    @Override public void setCancelled(boolean c) { this.cancelled = c; }
    @Override public HandlerList getHandlers()    { return HANDLERS; }
    public static HandlerList getHandlerList()    { return HANDLERS; }
}
