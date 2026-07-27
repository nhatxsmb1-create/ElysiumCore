package dev.elysium.core.season;

import dev.elysium.core.ElysiumCore;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.ColorUtil;
import org.bukkit.Bukkit;

/**
 * Quan ly season.
 * Reset player data khi sang season moi.
 *
 * Reset bao gom: level, exp, achievements.
 * GIU NGUYEN: balance, island, guild, skin, pet.
 *
 * Su dung:
 *   /ely season reset  ← admin command
 *   CoreAPI.getCore().getSeasonManager().getCurrentSeason()
 */
public class SeasonManager {

    private final ElysiumCore plugin;

    public SeasonManager(ElysiumCore plugin) {
        this.plugin = plugin;
    }

    public int getCurrentSeason() {
        return plugin.getConfigManager().getSeason();
    }

    /**
     * Reset tat ca player sang season moi.
     * Chi goi khi admin confirm /ely season reset.
     */
    public void resetToNewSeason() {
        int newSeason = getCurrentSeason() + 1;

        plugin.getLogger().warning("=== SEASON RESET: Season " + newSeason + " ===");
        Bukkit.broadcastMessage(ColorUtil.color(
            "&6&l[Elysium] &eSeason " + newSeason + " bat dau! Data da duoc reset."));

        // Reset tat ca player online
        for (ElysiumPlayer ep : plugin.getPlayerManager().getOnlinePlayers()) {
            resetPlayer(ep, newSeason);
        }

        // Reset player offline: cap nhat DB truc tiep
        plugin.getScheduler().runAsync(() -> {
            plugin.getDatabaseManager().resetAllForSeason(newSeason);
            plugin.getLogger().info("Season reset complete: Season " + newSeason);
        });

        // Cap nhat config
        // (Admin phai tu sua config.yml season: X)
    }

    public void resetPlayer(ElysiumPlayer ep, int newSeason) {
        ep.setLevel(1);
        ep.setExp(0);
        ep.setSeason(newSeason);
        ep.clearAchievements();
        ep.setBattlePassLevel(0);
        // GIU NGUYEN: balance, island, guild, mana, class
    }

    public String getSeasonName() {
        int s = getCurrentSeason();
        String age = plugin.getConfigManager().getAge();
        return "Season " + s + " - " + age;
    }
}
