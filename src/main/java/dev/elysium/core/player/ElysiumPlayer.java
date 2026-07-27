package dev.elysium.core.player;

import java.util.HashSet;
import java.util.Set;
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

    // Phase 2/3 stubs (field da co, logic them sau)
    private String pet;
    private String skin;

    // Achievements: Set<achievementId>
    private final Set<String> achievements = new HashSet<>();

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
        this.pet           = "";
        this.skin          = "";
        this.firstJoin     = System.currentTimeMillis();
        this.lastSeen      = System.currentTimeMillis();
    }

    // ── EXP / Level ──────────────────────────────────────────────────────────

    public void addExp(long amount) {
        this.exp += amount;
        while (this.exp >= getExpRequired() && this.level < 100) {
            this.exp -= getExpRequired();
            this.level++;
        }
    }

    public long getExpRequired() {
        return (long)(100 * Math.pow(1.15, level - 1));
    }

    // ── Mana ─────────────────────────────────────────────────────────────────

    public void addMana(int amount) { this.mana = Math.min(this.mana + amount, this.maxMana); }
    public boolean useMana(int amount) {
        if (this.mana < amount) return false;
        this.mana -= amount; return true;
    }

    // ── Balance ──────────────────────────────────────────────────────────────

    public void addBalance(double amount)         { this.balance += amount; }
    public boolean removeBalance(double amount) {
        if (this.balance < amount) return false;
        this.balance -= amount; return true;
    }

    // ── Achievements ─────────────────────────────────────────────────────────

    public boolean hasAchievement(String id)   { return achievements.contains(id.toLowerCase()); }
    public void    addAchievement(String id)   { achievements.add(id.toLowerCase()); }
    public void    clearAchievements()         { achievements.clear(); }
    public Set<String> getAchievements()       { return achievements; }

    /** Parse tu String "a,b,c" khi load tu DB */
    public void loadAchievements(String csv) {
        achievements.clear();
        if (csv == null || csv.isBlank()) return;
        for (String s : csv.split(",")) {
            if (!s.isBlank()) achievements.add(s.trim().toLowerCase());
        }
    }

    /** Serialize thanh String "a,b,c" de luu vao DB */
    public String serializeAchievements() {
        return String.join(",", achievements);
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
    public String getPet()                      { return pet; }
    public void   setPet(String pet)            { this.pet = pet; }
    public String getSkin()                     { return skin; }
    public void   setSkin(String skin)          { this.skin = skin; }
    public long   getFirstJoin()                { return firstJoin; }
    public void   setFirstJoin(long t)          { this.firstJoin = t; }
    public long   getLastSeen()                 { return lastSeen; }
    public void   setLastSeen(long t)           { this.lastSeen = t; }
}
