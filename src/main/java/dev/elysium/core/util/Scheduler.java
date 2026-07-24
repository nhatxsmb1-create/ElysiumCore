package dev.elysium.core.util;

import dev.elysium.core.ElysiumCore;
import org.bukkit.scheduler.BukkitTask;

/**
 * Wrapper tien loi cho BukkitScheduler.
 * Su dung: CoreAPI.getCore().getScheduler()
 */
public class Scheduler {

    private final ElysiumCore plugin;
    public Scheduler(ElysiumCore plugin) { this.plugin = plugin; }

    public BukkitTask runAsync(Runnable r)                        { return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, r); }
    public BukkitTask runSync(Runnable r)                         { return plugin.getServer().getScheduler().runTask(plugin, r); }
    public BukkitTask runLater(Runnable r, long delay)            { return plugin.getServer().getScheduler().runTaskLater(plugin, r, delay); }
    public BukkitTask runLaterAsync(Runnable r, long delay)       { return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, r, delay); }
    public BukkitTask runTimer(Runnable r, long delay, long period){ return plugin.getServer().getScheduler().runTaskTimer(plugin, r, delay, period); }
    public BukkitTask runTimerAsync(Runnable r, long d, long p)   { return plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, r, d, p); }
}
