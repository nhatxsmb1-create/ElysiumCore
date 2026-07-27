package dev.elysium.core;

import dev.elysium.core.achievement.AchievementManager;
import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.config.ConfigManager;
import dev.elysium.core.database.DatabaseManager;
import dev.elysium.core.database.MySQLDatabase;
import dev.elysium.core.database.SQLiteDatabase;
import dev.elysium.core.gui.GuiManager;
import dev.elysium.core.leaderboard.LeaderboardManager;
import dev.elysium.core.placeholder.ElysiumExpansion;
import dev.elysium.core.player.PlayerManager;
import dev.elysium.core.season.SeasonManager;
import dev.elysium.core.util.CooldownManager;
import dev.elysium.core.util.Scheduler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ElysiumCore extends JavaPlugin {

    private static ElysiumCore instance;

    private ConfigManager      configManager;
    private DatabaseManager    databaseManager;
    private PlayerManager      playerManager;
    private GuiManager         guiManager;
    private CooldownManager    cooldownManager;
    private Scheduler          scheduler;
    private AchievementManager achievementManager;
    private LeaderboardManager leaderboardManager;
    private SeasonManager      seasonManager;
    private Economy            economy;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        configManager = new ConfigManager(this);

        setupDatabase();

        playerManager      = new PlayerManager(this);
        guiManager         = new GuiManager(this);
        cooldownManager    = new CooldownManager();
        scheduler          = new Scheduler(this);
        achievementManager = new AchievementManager(this);
        leaderboardManager = new LeaderboardManager(this);
        seasonManager      = new SeasonManager(this);

        CoreAPI.init(this);

        setupVault();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new ElysiumExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered.");
        }

        Bukkit.getPluginManager().registerEvents(playerManager, this);
        Bukkit.getPluginManager().registerEvents(guiManager, this);
        getCommand("elysium").setExecutor(new ElysiumCommand(this));

        startAutoSave();

        getLogger().info("=== ElysiumCore v" + getDescription().getVersion() + " enabled! ===");
        getLogger().info("Season: " + configManager.getSeason() + " | Age: " + configManager.getAge());
    }

    @Override
    public void onDisable() {
        if (playerManager != null) playerManager.saveAll();
        if (databaseManager != null) databaseManager.close();
        getLogger().info("ElysiumCore disabled.");
    }

    private void setupDatabase() {
        String type = getConfig().getString("database.type", "sqlite");
        databaseManager = "mysql".equalsIgnoreCase(type) ? new MySQLDatabase(this) : new SQLiteDatabase(this);
        databaseManager.initialize();
    }

    private void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) { getLogger().warning("Vault not found!"); return; }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) { getLogger().warning("No economy provider!"); return; }
        economy = rsp.getProvider();
        getLogger().info("Vault hooked: " + economy.getName());
    }

    private void startAutoSave() {
        if (!getConfig().getBoolean("auto-save.enabled", true)) return;
        int interval = getConfig().getInt("auto-save.interval", 6000);
        scheduler.runTimerAsync(() -> {
            int count = playerManager.getOnlinePlayers().size();
            if (count > 0) {
                playerManager.saveAll();
                getLogger().info("[AutoSave] Saved " + count + " player(s).");
            }
        }, interval, interval);
    }

    public static ElysiumCore getInstance()          { return instance; }
    public ConfigManager   getConfigManager()        { return configManager; }
    public DatabaseManager getDatabaseManager()      { return databaseManager; }
    public PlayerManager   getPlayerManager()        { return playerManager; }
    public GuiManager      getGuiManager()           { return guiManager; }
    public CooldownManager getCooldownManager()      { return cooldownManager; }
    public Scheduler       getScheduler()            { return scheduler; }
    public AchievementManager getAchievementManager(){ return achievementManager; }
    public LeaderboardManager getLeaderboardManager(){ return leaderboardManager; }
    public SeasonManager   getSeasonManager()        { return seasonManager; }
    public Economy         getEconomy()              { return economy; }
                                }
