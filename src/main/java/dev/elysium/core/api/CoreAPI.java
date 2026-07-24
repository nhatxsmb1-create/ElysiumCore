package dev.elysium.core.api;

import dev.elysium.core.ElysiumCore;
import dev.elysium.core.player.ElysiumPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

/**
 * Public API for ElysiumCore.
 * Cac plugin khac (Combat, War, Sky...) chi can goi class nay.
 *
 * Vi du:
 *   ElysiumPlayer data = CoreAPI.getPlayer(player);
 *   data.addMana(20);
 *   CoreAPI.addExp(player, 100);
 */
public final class CoreAPI {

    private static ElysiumCore core;

    public static void init(ElysiumCore plugin) { core = plugin; }

    // ── Player ───────────────────────────────────────────────────────────────
    public static ElysiumPlayer getPlayer(Player p)  { return core.getPlayerManager().getPlayer(p); }
    public static ElysiumPlayer getPlayer(UUID uuid) { return core.getPlayerManager().getPlayer(uuid); }
    public static Collection<ElysiumPlayer> getOnlinePlayers() { return core.getPlayerManager().getOnlinePlayers(); }

    // ── Balance ──────────────────────────────────────────────────────────────
    public static double  getBalance(Player p)               { ElysiumPlayer ep = getPlayer(p); return ep != null ? ep.getBalance() : 0; }
    public static void    addBalance(Player p, double amt)   { ElysiumPlayer ep = getPlayer(p); if (ep != null) ep.addBalance(amt); }
    public static boolean removeBalance(Player p, double amt){ ElysiumPlayer ep = getPlayer(p); return ep != null && ep.removeBalance(amt); }
    public static boolean hasBalance(Player p, double amt)   { ElysiumPlayer ep = getPlayer(p); return ep != null && ep.getBalance() >= amt; }

    // ── Mana ─────────────────────────────────────────────────────────────────
    public static int     getMana(Player p)              { ElysiumPlayer ep = getPlayer(p); return ep != null ? ep.getMana() : 0; }
    public static boolean useMana(Player p, int amt)     { ElysiumPlayer ep = getPlayer(p); return ep != null && ep.useMana(amt); }
    public static void    addMana(Player p, int amt)     { ElysiumPlayer ep = getPlayer(p); if (ep != null) ep.addMana(amt); }

    // ── EXP / Level ──────────────────────────────────────────────────────────
    public static void addExp(Player p, long amt)  { ElysiumPlayer ep = getPlayer(p); if (ep != null) ep.addExp(amt); }
    public static int  getLevel(Player p)          { ElysiumPlayer ep = getPlayer(p); return ep != null ? ep.getLevel() : 0; }

    // ── Server ───────────────────────────────────────────────────────────────
    public static String     getServerName() { return core.getConfigManager().getServerName(); }
    public static int        getSeason()     { return core.getConfigManager().getSeason(); }
    public static String     getAge()        { return core.getConfigManager().getAge(); }
    public static ElysiumCore getCore()      { return core; }
}
