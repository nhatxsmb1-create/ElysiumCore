package dev.elysium.core.config;

import dev.elysium.core.ElysiumCore;
import dev.elysium.core.util.ColorUtil;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final ElysiumCore plugin;
    private FileConfiguration cfg;

    public ConfigManager(ElysiumCore plugin) {
        this.plugin = plugin; this.cfg = plugin.getConfig();
    }

    public void reload() { plugin.reloadConfig(); this.cfg = plugin.getConfig(); }

    public String  getServerName()        { return cfg.getString("server-name","Elysium"); }
    public int     getSeason()            { return cfg.getInt("season",1); }
    public String  getAge()               { return cfg.getString("age","BEGINNING"); }
    public String  getPrefix()            { return ColorUtil.color(cfg.getString("messages.prefix","&b[Elysium] &r")); }
    public String  getMsgNoPermission()   { return ColorUtil.color(cfg.getString("messages.no-permission","&cKhong co quyen!")); }
    public String  getMsgNotFound()       { return ColorUtil.color(cfg.getString("messages.player-not-found","&cKhong tim thay!")); }
    public double  getStartingBalance()   { return cfg.getDouble("defaults.starting-balance",1000.0); }
    public int     getStartingMana()      { return cfg.getInt("defaults.starting-mana",100); }
    public int     getMaxLevel()          { return cfg.getInt("defaults.max-level",100); }
    public boolean isLevelUpTitle()       { return cfg.getBoolean("level-up.title",true); }
    public boolean isLevelUpSound()       { return cfg.getBoolean("level-up.sound",true); }
    public boolean isBroadcastLevelUp()   { return cfg.getBoolean("level-up.broadcast",false); }
    public FileConfiguration getRaw()     { return cfg; }
}
