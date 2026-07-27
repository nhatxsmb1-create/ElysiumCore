package dev.elysium.core.database;

import dev.elysium.core.leaderboard.LeaderboardEntry;
import dev.elysium.core.player.ElysiumPlayer;

import java.util.List;
import java.util.UUID;

public abstract class DatabaseManager {
    public abstract void initialize();
    public abstract void close();

    // Player CRUD
    public abstract ElysiumPlayer loadPlayer(UUID uuid, String name);
    public abstract void createPlayer(ElysiumPlayer player);
    public abstract void savePlayer(ElysiumPlayer player);

    // Leaderboard
    public abstract List<LeaderboardEntry> getTopByLevel(int limit);
    public abstract List<LeaderboardEntry> getTopByBalance(int limit);

    // Season reset
    public abstract void resetAllForSeason(int newSeason);
}
