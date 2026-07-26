package dev.elysium.core.api;

import dev.elysium.core.ElysiumCore;
import dev.elysium.core.event.ElysiumLevelUpEvent;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

/**
 * Public API cho ElysiumCore.
 * Cac plugin khac (Combat, Sky, War...) chi can goi class nay.
 */
public final class CoreAPI {

    private static ElysiumCore core;
    public static void init(ElysiumCore plugin) { core = plugin; }

    // ── Player ────────────────────────────────────────────────────────────────
    public static ElysiumPlayer getPlayer(Player p)  { return core.getPlayerManager().getPlayer(p); }
    public static ElysiumPlayer getPlayer(UUID uuid) { return core.getPlayerManager().getPlayer(uuid); }
    public static Collection<ElysiumPlayer> getOnlinePlayers() { return core.getPlayerManager().getOnlinePlayers(); }

    // ── Balance ───────────────────────────────────────────────────────────────
    public static double  getBalance(Player p)               { ElysiumPlayer ep=getPlayer(p); return ep!=null?ep.getBalance():0; }
    public static void    addBalance(Player p, double amt)   { ElysiumPlayer ep=getPlayer(p); if(ep!=null) ep.addBalance(amt); }
    public static boolean removeBalance(Player p, double amt){ ElysiumPlayer ep=getPlayer(p); return ep!=null&&ep.removeBalance(amt); }
    public static boolean hasBalance(Player p, double amt)   { ElysiumPlayer ep=getPlayer(p); return ep!=null&&ep.getBalance()>=amt; }

    // ── Mana ──────────────────────────────────────────────────────────────────
    public static int     getMana(Player p)              { ElysiumPlayer ep=getPlayer(p); return ep!=null?ep.getMana():0; }
    public static boolean useMana(Player p, int amt)     { ElysiumPlayer ep=getPlayer(p); return ep!=null&&ep.useMana(amt); }
    public static void    addMana(Player p, int amt)     { ElysiumPlayer ep=getPlayer(p); if(ep!=null) ep.addMana(amt); }

    // ── EXP / Level ───────────────────────────────────────────────────────────
    /**
     * Them EXP va tu dong xu ly level up:
     * - Fire ElysiumLevelUpEvent
     * - Hien thi title + sound
     * - Broadcast neu config bat
     */
    public static void addExp(Player p, long amount) {
        ElysiumPlayer ep = getPlayer(p);
        if (ep == null) return;
        int oldLevel = ep.getLevel();
        ep.addExp(amount);
        int newLevel = ep.getLevel();
        if (newLevel > oldLevel) {
            // Fire event tren main thread
            Bukkit.getScheduler().runTask(core, () -> {
                ElysiumLevelUpEvent event = new ElysiumLevelUpEvent(p, ep, oldLevel, newLevel);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    MessageUtil.sendLevelUp(p, newLevel);
                    if (core.getConfigManager().isBroadcastLevelUp()) {
                        Bukkit.broadcastMessage(
                            dev.elysium.core.util.ColorUtil.color(
                                "&b[Elysium] &e" + p.getName() +
                                " &7da dat Level &e" + newLevel + "&7!"));
                    }
                }
            });
        }
    }

    public static int getLevel(Player p) { ElysiumPlayer ep=getPlayer(p); return ep!=null?ep.getLevel():0; }

    // ── Server ────────────────────────────────────────────────────────────────
    public static String     getServerName() { return core.getConfigManager().getServerName(); }
    public static int        getSeason()     { return core.getConfigManager().getSeason(); }
    public static String     getAge()        { return core.getConfigManager().getAge(); }
    public static ElysiumCore getCore()      { return core; }
}
