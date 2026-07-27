package dev.elysium.core;

import dev.elysium.core.achievement.AchievementType;
import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.gui.AdminGui;
import dev.elysium.core.gui.example.ProfileGui;
import dev.elysium.core.leaderboard.LeaderboardEntry;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.ColorUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class ElysiumCommand implements CommandExecutor {

    private final ElysiumCore plugin;
    public ElysiumCommand(ElysiumCore plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String pre = plugin.getConfigManager().getPrefix();
        if (args.length == 0) { sendHelp(sender); return true; }

        switch (args[0].toLowerCase()) {

            case "reload" -> {
                if (!sender.hasPermission("elysium.admin")) { sender.sendMessage(ColorUtil.color(pre + "&cKhong co quyen!")); return true; }
                plugin.reloadConfig(); plugin.getConfigManager().reload();
                sender.sendMessage(ColorUtil.color(pre + "&aReload config thanh cong!"));
            }

            case "info" -> {
                if (!sender.hasPermission("elysium.admin")) { sender.sendMessage(ColorUtil.color(pre + "&cKhong co quyen!")); return true; }
                if (args.length < 2) { sender.sendMessage(ColorUtil.color(pre + "&cDung: /ely info <player>")); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { sender.sendMessage(ColorUtil.color(pre + "&cKhong online!")); return true; }
                ElysiumPlayer ep = CoreAPI.getPlayer(t);
                if (ep == null) return true;
                Economy eco = plugin.getEconomy();
                double bal = eco != null ? eco.getBalance(t) : ep.getBalance();
                sender.sendMessage(ColorUtil.color("&b====[ " + t.getName() + " ]===="));
                sender.sendMessage(ColorUtil.color("  &7Lv: &e" + ep.getLevel() + " &7| EXP: &e" + ep.getExp() + "&7/&e" + ep.getExpRequired()));
                sender.sendMessage(ColorUtil.color("  &7Mana: &b" + ep.getMana() + "&7/&b" + ep.getMaxMana()));
                sender.sendMessage(ColorUtil.color("  &7Class: &d" + ep.getPlayerClass()));
                sender.sendMessage(ColorUtil.color("  &7Balance: &6" + String.format("%.1f", bal)));
                sender.sendMessage(ColorUtil.color("  &7Achievements: &e" + ep.getAchievements().size() + "&7/" + AchievementType.values().length));
            }

            case "profile", "p" -> {
                if (!(sender instanceof Player player)) { sender.sendMessage("Chi player dung duoc!"); return true; }
                Player target = args.length >= 2 ? Bukkit.getPlayer(args[1]) : player;
                if (target == null) { player.sendMessage(ColorUtil.color(pre + "&cKhong online!")); return true; }
                ElysiumPlayer ep = CoreAPI.getPlayer(target);
                if (ep == null) return true;
                plugin.getGuiManager().open(player, new ProfileGui(ep));
            }

            case "admin", "a" -> {
                if (!sender.hasPermission("elysium.admin")) { sender.sendMessage(ColorUtil.color(pre + "&cKhong co quyen!")); return true; }
                if (!(sender instanceof Player player)) { sender.sendMessage("Chi player dung duoc!"); return true; }
                plugin.getGuiManager().open(player, new AdminGui());
            }

            case "top" -> {
                String type = args.length >= 2 ? args[1] : "level";
                List<LeaderboardEntry> list = type.equalsIgnoreCase("balance")
                    ? CoreAPI.getTopByBalance(10)
                    : CoreAPI.getTopByLevel(10);
                sender.sendMessage(ColorUtil.color("&b=== Top 10 " + (type.equalsIgnoreCase("balance") ? "Giau" : "Level") + " ==="));
                for (LeaderboardEntry e : list) {
                    sender.sendMessage(ColorUtil.color(
                        "  &7#" + e.getRank() + " &f" + e.getName() +
                        " &7| Lv: &e" + e.getLevel() +
                        " &7| Balance: &6" + String.format("%.0f", e.getBalance())));
                }
            }

            case "setlevel" -> {
                if (!sender.hasPermission("elysium.admin")) { sender.sendMessage(ColorUtil.color(pre + "&cKhong co quyen!")); return true; }
                if (args.length < 3) { sender.sendMessage(ColorUtil.color(pre + "&cDung: /ely setlevel <player> <level>")); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { sender.sendMessage(ColorUtil.color(pre + "&cKhong online!")); return true; }
                ElysiumPlayer ep = CoreAPI.getPlayer(t);
                if (ep == null) return true;
                try { ep.setLevel(Integer.parseInt(args[2])); sender.sendMessage(ColorUtil.color(pre + "&aSet level " + t.getName() + " = " + args[2])); }
                catch (NumberFormatException e) { sender.sendMessage(ColorUtil.color(pre + "&cSo khong hop le!")); }
            }

            case "addmoney" -> {
                if (!sender.hasPermission("elysium.admin")) { sender.sendMessage(ColorUtil.color(pre + "&cKhong co quyen!")); return true; }
                if (args.length < 3) { sender.sendMessage(ColorUtil.color(pre + "&cDung: /ely addmoney <player> <amount>")); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { sender.sendMessage(ColorUtil.color(pre + "&cKhong online!")); return true; }
                try { CoreAPI.addBalance(t, Double.parseDouble(args[2])); sender.sendMessage(ColorUtil.color(pre + "&aThem tien thanh cong!")); }
                catch (NumberFormatException e) { sender.sendMessage(ColorUtil.color(pre + "&cSo tien khong hop le!")); }
            }

            case "save" -> {
                if (!sender.hasPermission("elysium.admin")) { sender.sendMessage(ColorUtil.color(pre + "&cKhong co quyen!")); return true; }
                plugin.getScheduler().runAsync(() -> {
                    plugin.getPlayerManager().saveAll();
                    sender.sendMessage(ColorUtil.color(pre + "&aSave thanh cong!"));
                });
            }

            case "season" -> {
                if (!sender.hasPermission("elysium.admin")) { sender.sendMessage(ColorUtil.color(pre + "&cKhong co quyen!")); return true; }
                if (args.length >= 2 && args[1].equalsIgnoreCase("reset")) {
                    sender.sendMessage(ColorUtil.color(pre + "&cGo lai: /ely season confirmreset de xac nhan!"));
                } else if (args.length >= 2 && args[1].equalsIgnoreCase("confirmreset")) {
                    plugin.getSeasonManager().resetToNewSeason();
                    sender.sendMessage(ColorUtil.color(pre + "&aSeason reset hoan tat!"));
                } else {
                    sender.sendMessage(ColorUtil.color(pre + "&7Season hien tai: &e" + CoreAPI.getSeasonName()));
                }
            }

            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender s) {
        s.sendMessage(ColorUtil.color("&b=== ElysiumCore Commands ==="));
        s.sendMessage(ColorUtil.color("  &7/ely reload &f- Reload config"));
        s.sendMessage(ColorUtil.color("  &7/ely info <player> &f- Xem thong tin"));
        s.sendMessage(ColorUtil.color("  &7/ely profile [player] &f- Profile GUI"));
        s.sendMessage(ColorUtil.color("  &7/ely admin &f- Mo Admin GUI"));
        s.sendMessage(ColorUtil.color("  &7/ely top [level|balance] &f- Leaderboard"));
        s.sendMessage(ColorUtil.color("  &7/ely setlevel <player> <lv> &f- Set level"));
        s.sendMessage(ColorUtil.color("  &7/ely addmoney <player> <so> &f- Them tien"));
        s.sendMessage(ColorUtil.color("  &7/ely save &f- Save tat ca player"));
        s.sendMessage(ColorUtil.color("  &7/ely season &f- Xem/reset season"));
    }
                                            }
