package dev.elysium.core.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Quan ly cooldown cho tat ca plugin.
 * Su dung: CoreAPI.getCore().getCooldownManager()
 *
 * Vi du:
 *   cd.set(player.getUniqueId(), "FIREBALL", 5000); // 5 giay
 *   if (cd.has(uuid, "FIREBALL")) { sendMessage("Cooldown!"); return; }
 */
public class CooldownManager {

    private final Map<String, Map<UUID, Long>> data = new ConcurrentHashMap<>();

    public void set(UUID uuid, String key, long ms) {
        data.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
            .put(uuid, System.currentTimeMillis() + ms);
    }

    public boolean has(UUID uuid, String key) {
        Map<UUID, Long> map = data.get(key);
        if (map == null) return false;
        Long exp = map.get(uuid);
        if (exp == null) return false;
        if (System.currentTimeMillis() > exp) { map.remove(uuid); return false; }
        return true;
    }

    public long remainingMs(UUID uuid, String key) {
        Map<UUID, Long> map = data.get(key);
        if (map == null) return 0;
        Long exp = map.get(uuid);
        if (exp == null) return 0;
        return Math.max(0, exp - System.currentTimeMillis());
    }

    public long remainingSeconds(UUID uuid, String key) {
        return (long) Math.ceil(remainingMs(uuid, key) / 1000.0);
    }

    public void clear(UUID uuid, String key) {
        Map<UUID, Long> map = data.get(key);
        if (map != null) map.remove(uuid);
    }

    public void clearAll(UUID uuid) {
        data.values().forEach(m -> m.remove(uuid));
    }
}
