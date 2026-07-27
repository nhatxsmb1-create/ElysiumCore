package dev.elysium.core.api;

import dev.elysium.core.ElysiumCore;
import dev.elysium.core.achievement.AchievementType;
import dev.elysium.core.event.ElysiumLevelUpEvent;
import dev.elysium.core.event.ElysiumManaChangeEvent;
import dev.elysium.core.leaderboard.LeaderboardEntry;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class CoreAPI {

    private static ElysiumCore core;
    public static void init(ElysiumCore plugin) { core = plugin; }

    // ── Player ────────────────────────────────────────────────────────────────
    public static ElysiumPlayer getPlayer(Player p)  { return core.getPlayerManager().getPlayer(p); }
    public static ElysiumPlayer getPlayer(UUID uuid) { return core.getPlayerManager().getPlayer(uuid); }
    public static Collection<ElysiumPlayer> getOnlinePlayers() { return core.getPlayerManager().getOnlinePlayers(); }

    // ── Balance (Vault first) ─────────────────────────────────────────────────
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
    public static double getBalance(OfflinePlayer p) {
        Economy eco = core.getEconomy();
        if (eco != null) return eco.getBalance(p);
        ElysiumPlayer ep = getPlayer(p.getUniqueId()); return ep != null ? ep.getBalance() : 0;
    }
    public static void addBalance(Player p, double amt) {
        Economy eco = core.getEconomy();
        if (eco != null) { eco.depositPlayer(p, amt); return; }
        ElysiumPlayer ep = getPlayer(p); if (ep != null) ep.addBalance(amt);
    }
    public static boolean removeBalance(Player p, double amt) {
        Economy eco = core.getEconomy();
        if (eco != null) { if (!eco.has(p, amt)) return false; eco.withdrawPlayer(p, amt); return true; }
        ElysiumPlayer ep = getPlayer(p); return ep != null && ep.removeBalance(amt);
    }
    public static boolean hasBalance(Player p, double amt) {
        Economy eco = core.getEconomy();
        if (eco != null) return eco.has(p, amt);
        ElysiumPlayer ep = getPlayer(p); return ep != null && ep.getBalance() >= amt;
    }

    // ── Mana (fires ElysiumManaChangeEvent) ───────────────────────────────────
    public static int getMana(Player p) { ElysiumPlayer ep=getPlayer(p); return ep!=null?ep.getMana():0; }

    public static boolean useMana(Player p, int amt) {
        ElysiumPlayer ep = getPlayer(p);
        if (ep == null) return false;
        int oldMana = ep.getMana();
        if (!ep.useMana(amt)) return false;
        fireManaEvent(p, ep, oldMana, ep.getMana(), ElysiumManaChangeEvent.ChangeReason.SKILL_USE);
        return true;
    }

    public static void addMana(Player p, int amt) {
        ElysiumPlayer ep = getPlayer(p);
        if (ep == null) return;
        int oldMana = ep.getMana();
        ep.addMana(amt);
        fireManaEvent(p, ep, oldMana, ep.getMana(), ElysiumManaChangeEvent.ChangeReason.REGEN);
    }

    private static void fireManaEvent(Player p, ElysiumPlayer ep, int old, int now,
                                      ElysiumManaChangeEvent.ChangeReason reason) {
        Bukkit.getPluginManager().callEvent(new ElysiumManaChangeEvent(p, ep, old, now, reason));
    }

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
                    if (core.getConfigManager().isBroadcastLevelUp())
                        Bukkit.broadcastMessage(dev.elysium.core.util.ColorUtil.color(
                            "&b[Elysium] &e" + p.getName() + " &7da dat &eLevel " + newLevel + "&7!"));
                    // Check achievements sau khi level up
                    core.getAchievementManager().checkAll(p, ep);
                }
            });
        }
    }
    public static int getLevel(Player p) { ElysiumPlayer ep=getPlayer(p); return ep!=null?ep.getLevel():0; }

    // ── Achievements ──────────────────────────────────────────────────────────
    public static boolean hasAchievement(Player p, AchievementType type) {
        ElysiumPlayer ep = getPlayer(p);
        return ep != null && ep.hasAchievement(type.getId());
    }
    public static void awardAchievement(Player p, AchievementType type) {
        ElysiumPlayer ep = getPlayer(p);
        if (ep != null) core.getAchievementManager().award(p, ep, type);
    }
    public static void checkAchievements(Player p) {
        ElysiumPlayer ep = getPlayer(p);
        if (ep != null) core.getAchievementManager().checkAll(p, ep);
    }

    // ── Leaderboard ───────────────────────────────────────────────────────────
    public static List<LeaderboardEntry> getTopByLevel(int limit)   { return core.getLeaderboardManager().getTopByLevel(limit); }
    public static List<LeaderboardEntry> getTopByBalance(int limit) { return core.getLeaderboardManager().getTopByBalance(limit); }

    // ── Season ────────────────────────────────────────────────────────────────
    public static int    getCurrentSeason() { return core.getSeasonManager().getCurrentSeason(); }
    public static String getSeasonName()    { return core.getSeasonManager().getSeasonName(); }

    // ── Server ────────────────────────────────────────────────────────────────
    public static String     getServerName() { return core.getConfigManager().getServerName(); }
    public static int        getSeason()     { return core.getConfigManager().getSeason(); }
    public static String     getAge()        { return core.getConfigManager().getAge(); }
    public static ElysiumCore getCore()      { return core; }
}
