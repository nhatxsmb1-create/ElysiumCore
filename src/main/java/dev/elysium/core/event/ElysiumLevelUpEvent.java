package dev.elysium.core.event;

import dev.elysium.core.player.ElysiumPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fire khi player len level.
 * Cac plugin khac (ElysiumCombat, ElysiumSky...) co the listen event nay.
 *
 * Vi du:
 *   @EventHandler
 *   public void onLevelUp(ElysiumLevelUpEvent e) {
 *       e.getPlayer().sendMessage("Level up len " + e.getNewLevel() + "!");
 *   }
 */
public class ElysiumLevelUpEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    private final Player        player;
    private final ElysiumPlayer elysiumPlayer;
    private final int           oldLevel;
    private final int           newLevel;

    public ElysiumLevelUpEvent(Player player, ElysiumPlayer ep, int oldLevel, int newLevel) {
        this.player        = player;
        this.elysiumPlayer = ep;
        this.oldLevel      = oldLevel;
        this.newLevel      = newLevel;
    }

    public Player        getPlayer()        { return player; }
    public ElysiumPlayer getElysiumPlayer() { return elysiumPlayer; }
    public int           getOldLevel()      { return oldLevel; }
    public int           getNewLevel()      { return newLevel; }

    @Override public boolean isCancelled()        { return cancelled; }
    @Override public void setCancelled(boolean c) { this.cancelled = c; }
    @Override public HandlerList getHandlers()    { return HANDLERS; }
    public static HandlerList getHandlerList()    { return HANDLERS; }
}
