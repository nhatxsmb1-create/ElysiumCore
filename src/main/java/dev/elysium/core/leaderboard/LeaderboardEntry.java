package dev.elysium.core.leaderboard;

import java.util.UUID;

public class LeaderboardEntry {

    private final int    rank;
    private final UUID   uuid;
    private final String name;
    private final int    level;
    private final long   exp;
    private final double balance;
    private final String playerClass;

    public LeaderboardEntry(int rank, UUID uuid, String name,
                            int level, long exp, double balance, String playerClass) {
        this.rank = rank; this.uuid = uuid; this.name = name;
        this.level = level; this.exp = exp; this.balance = balance;
        this.playerClass = playerClass;
    }

    public int    getRank()        { return rank; }
    public UUID   getUuid()        { return uuid; }
    public String getName()        { return name; }
    public int    getLevel()       { return level; }
    public long   getExp()         { return exp; }
    public double getBalance()     { return balance; }
    public String getPlayerClass() { return playerClass; }
}
