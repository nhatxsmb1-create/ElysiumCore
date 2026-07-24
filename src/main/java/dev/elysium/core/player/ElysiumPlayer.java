package dev.elysium.core.player;

import java.util.UUID;

public class ElysiumPlayer {

    private final UUID uuid;
    private String name;

    // Economy
    private double balance;
    private int    playerPoints;

    // RPG
    private String playerClass;
    private int    level;
    private long   exp;
    private int    mana;
    private int    maxMana;

    // Server
    private String guild;
    private String island;
    private int    battlePassLevel;
    private int    season;

    // Timestamps
    private long firstJoin;
    private long lastSeen;

    public ElysiumPlayer(UUID uuid, String name) {
        this.uuid          = uuid;
        this.name          = name;
        this.balance       = 1000.0;
        this.playerPoints  = 0;
        this.playerClass   = "NONE";
        this.level         = 1;
        this.exp           = 0;
        this.mana          = 100;
        this.maxMana       = 100;
        this.guild         = "";
        this.island        = "";
        this.battlePassLevel = 0;
        this.season        = 1;
        this.firstJoin     = System.currentTimeMillis();
        this.lastSeen      = System.currentTimeMillis();
    }

    // ── EXP ──────────────────────────────────────────────────────────────────

    public void addExp(long amount) {
        this.exp += amount;
        while (this.exp >= getExpRequired() && this.level < 100) {
            this.exp -= getExpRequired();
            this.level++;
        }
    }

    /** EXP can next level. Scale 15% moi level. */
    public long getExpRequired() {
        return (long)(100 * Math.pow(1.15, level - 1));
    }

    // ── Mana ─────────────────────────────────────────────────────────────────

    public void addMana(int amount) { this.mana = Math.min(this.mana + amount, this.maxMana); }

    public boolean useMana(int amount) {
        if (this.mana < amount) return false;
        this.mana -= amount;
        return true;
    }

    // ── Balance ──────────────────────────────────────────────────────────────

    public void addBalance(double amount) { this.balance += amount; }

    public boolean removeBalance(double amount) {
        if (this.balance < amount) return false;
        this.balance -= amount;
        return true;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public UUID   getUuid()                     { return uuid; }
    public String getName()                     { return name; }
    public void   setName(String n)             { this.name = n; }

    public double getBalance()                  { return balance; }
    public void   setBalance(double b)          { this.balance = b; }
    public int    getPlayerPoints()             { return playerPoints; }
    public void   setPlayerPoints(int pp)       { this.playerPoints = pp; }

    public String getPlayerClass()              { return playerClass; }
    public void   setPlayerClass(String c)      { this.playerClass = c; }
    public int    getLevel()                    { return level; }
    public void   setLevel(int lvl)             { this.level = lvl; }
    public long   getExp()                      { return exp; }
    public void   setExp(long exp)              { this.exp = exp; }
    public int    getMana()                     { return mana; }
    public void   setMana(int mana)             { this.mana = mana; }
    public int    getMaxMana()                  { return maxMana; }
    public void   setMaxMana(int maxMana)       { this.maxMana = maxMana; }

    public String getGuild()                    { return guild; }
    public void   setGuild(String guild)        { this.guild = guild; }
    public String getIsland()                   { return island; }
    public void   setIsland(String island)      { this.island = island; }
    public int    getBattlePassLevel()          { return battlePassLevel; }
    public void   setBattlePassLevel(int bpl)   { this.battlePassLevel = bpl; }
    public int    getSeason()                   { return season; }
    public void   setSeason(int season)         { this.season = season; }

    public long   getFirstJoin()                { return firstJoin; }
    public void   setFirstJoin(long t)          { this.firstJoin = t; }
    public long   getLastSeen()                 { return lastSeen; }
    public void   setLastSeen(long t)           { this.lastSeen = t; }
}
