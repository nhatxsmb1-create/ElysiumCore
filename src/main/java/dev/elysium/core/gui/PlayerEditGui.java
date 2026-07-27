package dev.elysium.core.gui;

import dev.elysium.core.ElysiumCore;
import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.ColorUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

/**
 * GUI chinh sua thong tin player (cho admin).
 * Mo tu AdminGui khi click vao player.
 */
public class PlayerEditGui extends ElysiumGui {

    private final ElysiumPlayer ep;

    public PlayerEditGui(ElysiumPlayer ep) {
        super("&c⚙ Edit: &f" + ep.getName(), 27);
        this.ep = ep;
    }

    @Override
    public void build(Player admin) {
        fill(ItemBuilder.filler());

        Economy eco = ElysiumCore.getInstance().getEconomy();
        double balance = eco != null
            ? eco.getBalance(Bukkit.getOfflinePlayer(ep.getUuid()))
            : ep.getBalance();

        // ── Slot 4: Info ──────────────────────────────────────────────────────
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setOwningPlayer(Bukkit.getOfflinePlayer(ep.getUuid()));
        sm.setDisplayName(ColorUtil.color("&f" + ep.getName()));
        sm.setLore(List.of(
            ColorUtil.color("&7Level: &e" + ep.getLevel()),
            ColorUtil.color("&7EXP: &e" + ep.getExp()),
            ColorUtil.color("&7Balance: &6" + String.format("%.1f", balance)),
            ColorUtil.color("&7Mana: &b" + ep.getMana() + "/" + ep.getMaxMana()),
            ColorUtil.color("&7Class: &d" + ep.getPlayerClass())
        ));
        skull.setItemMeta(sm);
        fill(4, skull);

        // ── Level buttons ─────────────────────────────────────────────────────
        setButton(9, new GuiButton(
            new ItemBuilder(Material.LIME_DYE).name("&a+1 Level").build(),
            e -> { ep.setLevel(Math.min(ep.getLevel() + 1, 100)); refresh(admin); }
        ));
        setButton(18, new GuiButton(
            new ItemBuilder(Material.RED_DYE).name("&c-1 Level").build(),
            e -> { ep.setLevel(Math.max(ep.getLevel() - 1, 1)); refresh(admin); }
        ));

        // ── Balance buttons ───────────────────────────────────────────────────
        setButton(11, new GuiButton(
            new ItemBuilder(Material.GOLD_INGOT).name("&6+1000 Coins").build(),
            e -> {
                Player target = Bukkit.getPlayer(ep.getUuid());
                if (target != null) CoreAPI.addBalance(target, 1000);
                else if (eco != null) eco.depositPlayer(Bukkit.getOfflinePlayer(ep.getUuid()), 1000);
                refresh(admin);
            }
        ));
        setButton(20, new GuiButton(
            new ItemBuilder(Material.GOLD_NUGGET).name("&c-1000 Coins").build(),
            e -> {
                Player target = Bukkit.getPlayer(ep.getUuid());
                if (target != null) CoreAPI.removeBalance(target, 1000);
                else if (eco != null) eco.withdrawPlayer(Bukkit.getOfflinePlayer(ep.getUuid()), 1000);
                refresh(admin);
            }
        ));

        // ── Mana ─────────────────────────────────────────────────────────────
        setButton(13, new GuiButton(
            new ItemBuilder(Material.LAPIS_LAZULI).name("&bHoi phuc Mana").lore("&7Hoi phuc mana ve max").build(),
            e -> { ep.setMana(ep.getMaxMana()); refresh(admin); }
        ));

        // ── EXP ──────────────────────────────────────────────────────────────
        setButton(15, new GuiButton(
            new ItemBuilder(Material.EXPERIENCE_BOTTLE).name("&a+1000 EXP").build(),
            e -> {
                Player target = Bukkit.getPlayer(ep.getUuid());
                if (target != null) CoreAPI.addExp(target, 1000);
                else ep.addExp(1000);
                refresh(admin);
            }
        ));

        // ── Save ─────────────────────────────────────────────────────────────
        setButton(26, new GuiButton(
            new ItemBuilder(Material.EMERALD).name("&aSave").lore("&7Luu thay doi").glow().build(),
            e -> {
                ElysiumCore.getInstance().getScheduler().runAsync(() ->
                    ElysiumCore.getInstance().getDatabaseManager().savePlayer(ep));
                admin.sendMessage(ColorUtil.color("&a[Admin] Da save data cua " + ep.getName()));
                admin.playSound(admin.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
                refresh(admin);
            }
        ));
    }

    private void refresh(Player admin) {
        ElysiumCore.getInstance().getGuiManager().open(admin, new PlayerEditGui(ep));
    }
          }
