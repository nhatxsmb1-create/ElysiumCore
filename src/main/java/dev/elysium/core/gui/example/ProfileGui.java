package dev.elysium.core.gui.example;

import dev.elysium.core.gui.ElysiumGui;
import dev.elysium.core.gui.GuiButton;
import dev.elysium.core.gui.ItemBuilder;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

/**
 * GUI Profile hien thi thong tin player.
 * Mo bang: /ely profile [player]
 * Cac plugin khac co the ke thua class nay de mo rong.
 */
public class ProfileGui extends ElysiumGui {

    private final ElysiumPlayer ep;

    public ProfileGui(ElysiumPlayer ep) {
        super("&b✦ Profile: &f" + ep.getName(), 27);
        this.ep = ep;
    }

    @Override
    public void build(Player viewer) {

        // Fill vien
        fill(ItemBuilder.filler());

        // ── Slot 11: Dau player + thong tin chinh ─────────────────────────────
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(
            org.bukkit.Bukkit.getOfflinePlayer(ep.getUuid()));
        skullMeta.setDisplayName(ColorUtil.color("&f" + ep.getName()));
        skullMeta.setLore(List.of(
            ColorUtil.color("&7Class: &d" + (ep.getPlayerClass().equals("NONE") ? "Chua chon" : ep.getPlayerClass())),
            ColorUtil.color("&7Level: &e" + ep.getLevel()),
            ColorUtil.color("&7EXP: &e" + ep.getExp() + "&7/&e" + ep.getExpRequired()),
            ColorUtil.color("&7Guild: &a" + (ep.getGuild().isEmpty() ? "Chua co" : ep.getGuild()))
        ));
        skull.setItemMeta(skullMeta);
        setButton(11, new GuiButton(skull));

        // ── Slot 13: Kinh te ───────────────────────────────────────────────────
        setButton(13, new GuiButton(
            new ItemBuilder(Material.GOLD_INGOT)
                .name("&6Kinh Te")
                .lore(
                    "&7Balance: &6" + String.format("%.1f", ep.getBalance()) + " coins",
                    "&7PlayerPoints: &e" + ep.getPlayerPoints(),
                    "&7Season: &b" + ep.getSeason(),
                    "&7Battle Pass Lv: &e" + ep.getBattlePassLevel()
                ).build()
        ));

        // ── Slot 15: Chi so combat ─────────────────────────────────────────────
        setButton(15, new GuiButton(
            new ItemBuilder(Material.NETHER_STAR)
                .name("&bChi So")
                .lore(
                    "&7Mana: &b" + ep.getMana() + "&7/&b" + ep.getMaxMana(),
                    "&7Island: &a" + (ep.getIsland().isEmpty() ? "Chua co" : ep.getIsland()),
                    "",
                    "&8Lan cuoi online: &7" + formatTime(ep.getLastSeen())
                ).build()
        ));
    }

    private String formatTime(long timestamp) {
        if (timestamp == 0) return "N/A";
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / 60000;
        if (minutes < 1) return "Vua xong";
        if (minutes < 60) return minutes + " phut truoc";
        long hours = minutes / 60;
        if (hours < 24) return hours + " gio truoc";
        return (hours / 24) + " ngay truoc";
    }
}
