package dev.elysium.core.player;

import dev.elysium.core.ElysiumCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager implements Listener {

    private final ElysiumCore plugin;
    // ConcurrentHashMap vi AsyncPlayerPreLoginEvent chay async
    private final ConcurrentHashMap<UUID, ElysiumPlayer> cache = new ConcurrentHashMap<>();

    public PlayerManager(ElysiumCore plugin) {
        this.plugin = plugin;
    }

    // Pre-load data truoc khi player join (async thread)
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getUniqueId();
        ElysiumPlayer ep = plugin.getDatabaseManager().loadPlayer(uuid, e.getName());
        if (ep == null) {
            ep = new ElysiumPlayer(uuid, e.getName());
            plugin.getDatabaseManager().createPlayer(ep);
        }
        cache.put(uuid, ep);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // Update ten moi nhat (truong hop doi username)
        ElysiumPlayer ep = cache.get(e.getPlayer().getUniqueId());
        if (ep != null) ep.setName(e.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        ElysiumPlayer ep = cache.remove(player.getUniqueId());
        if (ep != null) {
            ep.setLastSeen(System.currentTimeMillis());
            ep.setName(player.getName());
            // Save async, khong block main thread
            plugin.getScheduler().runAsync(() -> plugin.getDatabaseManager().savePlayer(ep));
        }
    }

    public ElysiumPlayer getPlayer(UUID uuid)   { return cache.get(uuid); }
    public ElysiumPlayer getPlayer(Player p)    { return cache.get(p.getUniqueId()); }
    public Collection<ElysiumPlayer> getOnlinePlayers() { return cache.values(); }

    /** Goi khi server shutdown — save dong bo */
    public void saveAll() {
        int count = 0;
        for (ElysiumPlayer ep : cache.values()) {
            ep.setLastSeen(System.currentTimeMillis());
            plugin.getDatabaseManager().savePlayer(ep);
            count++;
        }
        plugin.getLogger().info("Saved " + count + " player(s).");
    }
}
