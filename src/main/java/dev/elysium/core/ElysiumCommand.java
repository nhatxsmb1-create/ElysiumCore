package dev.elysium.core;

import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.gui.example.ProfileGui;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.ColorUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ElysiumCommand implements CommandExecutor {

    private final ElysiumCore plugin;
    public ElysiumCommand(ElysiumCore plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String pre = plugin.getConfigManager().getPrefix();
        if (args.length == 0) { sendHelp(sender); return true; }

        switch (args[0].toLowerCase()) {

            case "reload" -> {
                if (!sender.hasPermission("elysium.admin")) {
                    sender.sendMessage(ColorUtil.color(pre + "&cKhong co quyen!")); return true;
                }
                plugin.reloadConfig();
                plugin.getConfigManager().reload();
                sender.sendMessage(ColorUtil.color(pre + "&aReload config thanh cong!"));
                sender.sendMessage(ColorUtil.color(pre + "&8(Database & PlayerData khong reload khi server dang chay)"));
            }

            case "info" -> {
                if (!sender.hasPermission("elysium.admin")) {
                    sender.sendMessage(ColorUtil.color(pre + "&cKhong co quyen!")); return true;
                }
                if (args.length < 2) { sender.sendMessage(ColorUtil.color(pre + "&cDung: /ely info <player>")); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { sender.sendMessage(ColorUtil.color(pre + "&cPlayer khong online!")); return true; }
                ElysiumPlayer ep = CoreAPI.getPlayer(t);
                if (ep == null) { sender.sendMessage(ColorUtil.color(pre + "&cData chua load!")); return true; }

                // Balance: lay tu Vault neu co
                Economy eco = plugin.getEconomy();
                double balance = eco != null ? eco.getBalance(t) : ep.getBalance();

                sender.sendMessage(ColorUtil.color("&b====[ " + t.getName() + " ]===="));
                sender.sendMessage(ColorUtil.color("  &7Lv: &e" + ep.getLevel()
                    + " &7| EXP: &e" + ep.getExp() + "&7/&e" + ep.getExpRequired()));
                sender.sendMessage(ColorUtil.color("  &7Mana: &b" + ep.getMana() + "&7/&b" + ep.getMaxMana()));
                sender.sendMessage(ColorUtil.color("  &7Class: &d" + ep.getPlayerClass()));
                sender.sendMessage(ColorUtil.color("  &7Balance: &6" + String.format("%.1f", balance)
                    + (eco != null ? " &8(Vault)" : " &8(Internal)")));
                sender.sendMessage(ColorUtil.color("  &7Guild: &a" + (ep.getGuild().isEmpty() ? "None" : ep.getGuild())));
                sender.sendMessage(ColorUtil.color("  &7Island: &a" + (ep.getIsland().isEmpty() ? "None" : ep.getIsland())));
            }

            case "profile", "p" -> {
                if (!(sender instanceof Player player)) { sender.sendMessage("Chi player dung duoc!"); return true; }
                Player target = args.length >= 2 ? Bukkit.getPlayer(args[1]) : player;
                if (target == null) { player.sendMessage(ColorUtil.color(pre + "&cPlayer khong online!")); return true; }
                ElysiumPlayer ep = CoreAPI.getPlayer(target);
                if (ep == null) { player.sendMessage(ColorUtil.color(pre + "&cData chua load!")); return true; }
                plugin.getGuiManager().open(player, new ProfileGui(ep));
            }

            case "setlevel" -> {
                if (!sender.hasPermission("elysium.admin")) {
                    sender.sendMessage(ColorUtil.color(pre + "&cKhong co quyen!")); return true;
                }
                if (args.length < 3) { sender.sendMessage(ColorUtil.color(pre + "&cDung: /ely setlevel <player> <level>")); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { sender.sendMessage(ColorUtil.color(pre + "&cPlayer khong online!")); return true; }
                ElysiumPlayer ep = CoreAPI.getPlayer(t);
                if (ep == null) return true;
                try {
                    ep.setLevel(Integer.parseInt(args[2]));
                    sender.sendMessage(ColorUtil.color(pre + "&aDat level &e" + t.getName() + " &a= &e" + args[2]));
                } catch (NumberFormatException e) {
                    sender.sendMessage(ColorUtil.color(pre + "&cSo khong hop le!"));
                }
            }

            case "addmoney" -> {
                if (!sender.hasPermission("elysium.admin")) {
                    sender.sendMessage(ColorUtil.color(pre + "&cKhong co quyen!")); return true;
                }
                if (args.length < 3) { sender.sendMessage(ColorUtil.color(pre + "&cDung: /ely addmoney <player> <amount>")); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { sender.sendMessage(ColorUtil.color(pre + "&cPlayer khong online!")); return true; }
                try {
                    double amt = Double.parseDouble(args[2]);
                    CoreAPI.addBalance(t, amt); // Tu dong dung Vault
                    sender.sendMessage(ColorUtil.color(pre + "&aThem &6" + amt + " &acho " + t.getName()));
                } catch (NumberFormatException e) {
                    sender.sendMessage(ColorUtil.color(pre + "&cSo tien khong hop le!"));
                }
            }

            case "save" -> {
                if (!sender.hasPermission("elysium.admin")) {
                    sender.sendMessage(ColorUtil.color(pre + "&cKhong co quyen!")); return true;
                }
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    plugin.getPlayerManager().saveAll();
                    sender.sendMessage(ColorUtil.color(pre + "&aSave thanh cong!"));
                });
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
        s.sendMessage(ColorUtil.color("  &7/ely setlevel <player> <lv> &f- Set level"));
        s.sendMessage(ColorUtil.color("  &7/ely addmoney <player> <so> &f- Them tien"));
        s.sendMessage(ColorUtil.color("  &7/ely save &f- Save tat ca player"));
    }
                    }
