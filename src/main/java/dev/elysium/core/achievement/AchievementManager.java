package dev.elysium.core.achievement;

import dev.elysium.core.ElysiumCore;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.ColorUtil;
import dev.elysium.core.util.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Kiem tra va trao achievement cho player.
 *
 * Vi du su dung:
 *   // Sau khi level up:
 *   CoreAPI.getCore().getAchievementManager().checkAll(player);
 *
 *   // Kiem tra 1 achievement cu the:
 *   CoreAPI.getCore().getAchievementManager().award(player, AchievementType.FIRST_JOIN);
 */
public class AchievementManager {

    private final ElysiumCore plugin;

    public AchievementManager(ElysiumCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Kiem tra tat ca achievement cua player.
     * Goi sau khi: join, level up, balance change, class change.
     * @return Danh sach achievement moi nhan duoc
     */
    public List<AchievementType> checkAll(Player player, ElysiumPlayer ep) {
        List<AchievementType> newlyEarned = new ArrayList<>();

        checkLevel(player, ep, newlyEarned);
        checkBalance(player, ep, newlyEarned);
        checkOther(player, ep, newlyEarned);

        return newlyEarned;
    }

    private void checkLevel(Player player, ElysiumPlayer ep, List<AchievementType> list) {
        int lv = ep.getLevel();
        if (lv >= 5)   tryAward(player, ep, AchievementType.LEVEL_5, list);
        if (lv >= 10)  tryAward(player, ep, AchievementType.LEVEL_10, list);
        if (lv >= 25)  tryAward(player, ep, AchievementType.LEVEL_25, list);
        if (lv >= 50)  tryAward(player, ep, AchievementType.LEVEL_50, list);
        if (lv >= 100) tryAward(player, ep, AchievementType.LEVEL_100, list);
    }

    private void checkBalance(Player player, ElysiumPlayer ep, List<AchievementType> list) {
        // Doc balance tu Vault
        double bal = plugin.getEconomy() != null
            ? plugin.getEconomy().getBalance(player)
            : ep.getBalance();
        if (bal >= 5_000)     tryAward(player, ep, AchievementType.BALANCE_5000, list);
        if (bal >= 50_000)    tryAward(player, ep, AchievementType.BALANCE_50000, list);
        if (bal >= 500_000)   tryAward(player, ep, AchievementType.BALANCE_500000, list);
        if (bal >= 5_000_000) tryAward(player, ep, AchievementType.BALANCE_5000000, list);
    }

    private void checkOther(Player player, ElysiumPlayer ep, List<AchievementType> list) {
        // Class da chon
        if (!ep.getPlayerClass().equals("NONE") && !ep.getPlayerClass().isBlank()) {
            tryAward(player, ep, AchievementType.CLASS_CHOSEN, list);
        }
    }

    private void tryAward(Player player, ElysiumPlayer ep, AchievementType type,
                           List<AchievementType> newList) {
        if (ep.hasAchievement(type.getId())) return;
        ep.addAchievement(type.getId());
        newList.add(type);
        notifyAchievement(player, type);
    }

    /** Trao achievement thu cong (goi tu ngoai) */
    public boolean award(Player player, ElysiumPlayer ep, AchievementType type) {
        if (ep.hasAchievement(type.getId())) return false;
        ep.addAchievement(type.getId());
        notifyAchievement(player, type);
        return true;
    }

    private void notifyAchievement(Player player, AchievementType type) {
        // Chat message
        player.sendMessage(ColorUtil.color(
            "&6&l[ACHIEVEMENT] " + type.getColor() + type.getDisplayName()));
        player.sendMessage(ColorUtil.color(
            "  &7" + type.getDescription()));
        // Title
        MessageUtil.sendTitle(player,
            "&6&lACHIEVEMENT!",
            type.getColor() + type.getDisplayName(),
            10, 50, 20);
        // Sound
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        // Broadcast neu la achievement lon
        if (type == AchievementType.LEVEL_100 || type == AchievementType.BALANCE_5000000) {
            plugin.getServer().broadcastMessage(ColorUtil.color(
                "&6[Elysium] &e" + player.getName() +
                " &7da dat: " + type.getColor() + type.getDisplayName() + " &7!"));
        }
    }

    public int countAchievements(ElysiumPlayer ep) {
        return ep.getAchievements().size();
    }

    public int totalAchievements() {
        return AchievementType.values().length;
    }
    }
