package dev.elysium.core.util;

import dev.elysium.core.ElysiumCore;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * Tien ich gui title, actionbar, sound cho player.
 * Dung nhieu noi nen dat o Core.
 */
public final class MessageUtil {

    private MessageUtil() {}

    // ── Title ─────────────────────────────────────────────────────────────────

    public static void sendTitle(Player player, String title, String subtitle,
                                 int fadeInTicks, int stayTicks, int fadeOutTicks) {
        Title.Times times = Title.Times.times(
            Duration.ofMillis(fadeInTicks  * 50L),
            Duration.ofMillis(stayTicks    * 50L),
            Duration.ofMillis(fadeOutTicks * 50L)
        );
        player.showTitle(Title.title(
            ColorUtil.component(title),
            ColorUtil.component(subtitle),
            times
        ));
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 10, 60, 20);
    }

    // ── Action Bar ────────────────────────────────────────────────────────────

    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(ColorUtil.component(message));
    }

    // ── Sound ─────────────────────────────────────────────────────────────────

    public static void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    // ── Preset: Level Up ──────────────────────────────────────────────────────

    public static void sendLevelUp(Player player, int newLevel) {
        ElysiumCore core = ElysiumCore.getInstance();

        if (core.getConfigManager().isLevelUpTitle()) {
            sendTitle(player,
                "&e&lLEVEL UP!",
                "&7Ban da dat &eLevel " + newLevel);
        }

        if (core.getConfigManager().isLevelUpSound()) {
            playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.2f);
        }

        // Particles
        player.getWorld().spawnParticle(
            org.bukkit.Particle.HAPPY_VILLAGER,
            player.getLocation().add(0, 1, 0),
            20, 0.5, 0.5, 0.5, 0.1);
    }

    // ── Preset: Prefix message ────────────────────────────────────────────────

    public static void send(Player player, String message) {
        player.sendMessage(ColorUtil.color(
            ElysiumCore.getInstance().getConfigManager().getPrefix() + message));
    }
}
