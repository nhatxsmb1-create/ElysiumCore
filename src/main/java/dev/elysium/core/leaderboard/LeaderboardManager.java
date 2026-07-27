package dev.elysium.core.leaderboard;

import dev.elysium.core.ElysiumCore;

import java.util.List;

/**
 * API leaderboard cho cac plugin khac.
 *
 * Vi du:
 *   List<LeaderboardEntry> top = CoreAPI.getCore().getLeaderboard().getTopByLevel(10);
 *   for (LeaderboardEntry e : top) {
 *       player.sendMessage("#" + e.getRank() + " " + e.getName() + " - Lv" + e.getLevel());
 *   }
 */
public class LeaderboardManager {

    private final ElysiumCore plugin;

    // Cache de tranh query DB lien tuc
    private List<LeaderboardEntry> cacheLevel;
    private List<LeaderboardEntry> cacheBalance;
    private long lastUpdate = 0;
    private static final long CACHE_DURATION = 60_000; // 1 phut

    public LeaderboardManager(ElysiumCore plugin) {
        this.plugin = plugin;
        // Refresh cache moi 5 phut
        plugin.getScheduler().runTimerAsync(this::refreshCache, 6000L, 6000L);
    }

    private void refreshCache() {
        cacheLevel   = plugin.getDatabaseManager().getTopByLevel(10);
        cacheBalance = plugin.getDatabaseManager().getTopByBalance(10);
        lastUpdate   = System.currentTimeMillis();
    }

    public List<LeaderboardEntry> getTopByLevel(int limit) {
        if (cacheLevel == null || isExpired()) refreshCache();
        return cacheLevel != null ? cacheLevel.subList(0, Math.min(limit, cacheLevel.size()))
                                  : List.of();
    }

    public List<LeaderboardEntry> getTopByBalance(int limit) {
        if (cacheBalance == null || isExpired()) refreshCache();
        return cacheBalance != null ? cacheBalance.subList(0, Math.min(limit, cacheBalance.size()))
                                    : List.of();
    }

    private boolean isExpired() {
        return System.currentTimeMillis() - lastUpdate > CACHE_DURATION;
    }

    /** Force refresh ngay lap tuc */
    public void forceRefresh() { refreshCache(); }
}
