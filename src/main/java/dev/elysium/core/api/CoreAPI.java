package dev.elysium.core.api;

import dev.elysium.core.ElysiumCore;
import dev.elysium.core.event.ElysiumLevelUpEvent;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

/**
 * Public API cho ElysiumCore.
 *
 * Balance uu tien Vault (EssentialsX).
 * Neu Vault khong co, fall back ve internal balance.
 */
public final class CoreAPI {

    private static ElysiumCore core;
    public static void init(ElysiumCore plugin) { core = plugin; }

    // ── Player ────────────────────────────────────────────────────────────────
    public static ElysiumPlayer getPlayer(Player p)  { return core.getPlayerManager().getPlayer(p); }
    public static ElysiumPlayer getPlayer(UUID uuid) { return core.getPlayerManager().getPlayer(uuid); }
    public static Collection<ElysiumPlayer> getOnlinePlayers() { return core.getPlayerManager().getOnlinePlayers(); }

    // ── Balance (Vault first, fallback internal) ───────────────────────────────

    public static double getBalance(Player p) {
        Economy eco = core.getEconomy();
        if (eco != null) return eco.getBalance(p);
        ElysiumPlayer ep = getPlayer(p); return ep != null ? ep.getBalance() : 0;
    }

    public static double getBalance(UUID uuid) {
        Economy eco = core.getEconomy();
        if (eco != null) return eco.getBalance(Bukkit.getOfflinePlayer(uuid));
        ElysiumPlayer ep = getPlayer(uuid); return ep != null ? ep.getBalance() : 0;
    }

    public static void addBalance(Player p, double amount) {
        Economy eco = core.getEconomy();
        if (eco != null) { eco.depositPlayer(p, amount); return; }
        ElysiumPlayer ep = getPlayer(p); if (ep != null) ep.addBalance(amount);
    }

    public static boolean removeBalance(Player p, double amount) {
        Economy eco = core.getEconomy();
        if (eco != null) {
            if (!eco.has(p, amount)) return false;
            eco.withdrawPlayer(p, amount);
            return true;
        }
        ElysiumPlayer ep = getPlayer(p); return ep != null && ep.removeBalance(amount);
    }

    public static boolean hasBalance(Player p, double amount) {
        Economy eco = core.getEconomy();
        if (eco != null) return eco.has(p, amount);
        ElysiumPlayer ep = getPlayer(p); return ep != null && ep.getBalance() >= amount;
    }

    // Balance cho OfflinePlayer (ProfileGui, leaderboard...)
    public static double getBalance(OfflinePlayer p) {
        Economy eco = core.getEconomy();
        if (eco != null) return eco.getBalance(p);
        ElysiumPlayer ep = getPlayer(p.getUniqueId()); return ep != null ? ep.getBalance() : 0;
    }

    // ── Mana ──────────────────────────────────────────────────────────────────
    public static int     getMana(Player p)          { ElysiumPlayer ep=getPlayer(p); return ep!=null?ep.getMana():0; }
    public static boolean useMana(Player p, int amt) { ElysiumPlayer ep=getPlayer(p); return ep!=null&&ep.useMana(amt); }
    public static void    addMana(Player p, int amt) { ElysiumPlayer ep=getPlayer(p); if(ep!=null) ep.addMana(amt); }

    // ── EXP / Level ───────────────────────────────────────────────────────────
    public static void addExp(Player p, long amount) {
        ElysiumPlayer ep = getPlayer(p);
        if (ep == null) return;
        int oldLevel = ep.getLevel();
        ep.addExp(amount);
        int newLevel = ep.getLevel();
        if (newLevel > oldLevel) {
            Bukkit.getScheduler().runTask(core, () -> {
                ElysiumLevelUpEvent event = new ElysiumLevelUpEvent(p, ep, oldLevel, newLevel);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    MessageUtil.sendLevelUp(p, newLevel);
                    if (core.getConfigManager().isBroadcastLevelUp()) {
                        Bukkit.broadcastMessage(
                            dev.elysium.core.util.ColorUtil.color(
                                "&b[Elysium] &e" + p.getName() +
                                " &7da dat &eLevel " + newLevel + "&7!"));
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
