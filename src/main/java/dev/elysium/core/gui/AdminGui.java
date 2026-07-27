package dev.elysium.core.gui;

import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collection;
import java.util.List;

/**
 * Admin GUI - danh sach player online.
 * Mo bang: /ely admin
 * Click vao player head -> mo PlayerEditGui.
 */
public class AdminGui extends ElysiumGui {

    public AdminGui() {
        super("&c⚙ Admin Panel", 54);
    }

    @Override
    public void build(Player viewer) {
        fill(ItemBuilder.filler());

        Collection<ElysiumPlayer> online = CoreAPI.getOnlinePlayers();

        // Header info
        setButton(4, new GuiButton(
            new ItemBuilder(Material.NETHER_STAR)
                .name("&c⚙ Admin Panel")
                .lore(
                    "&7Online: &e" + online.size() + " players",
                    "&7Server: &b" + CoreAPI.getServerName(),
                    "&7Season: &e" + CoreAPI.getSeason()
                ).build()
        ));

        // Hien thi player online (toi da 45 slot)
        int slot = 9;
        for (ElysiumPlayer ep : online) {
            if (slot >= 54) break;

            Player target = Bukkit.getPlayer(ep.getUuid());
            if (target == null) { slot++; continue; }

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(ep.getUuid()));
            meta.setDisplayName(ColorUtil.color("&f" + ep.getName()));
            meta.setLore(List.of(
                ColorUtil.color("&7Lv: &e" + ep.getLevel() + " &7| Class: &d" + ep.getPlayerClass()),
                ColorUtil.color("&7Mana: &b" + ep.getMana() + "/" + ep.getMaxMana()),
                ColorUtil.color(""),
                ColorUtil.color("&eClick de chinh sua")
            ));
            skull.setItemMeta(meta);

            final ElysiumPlayer finalEp = ep;
            setButton(slot, new GuiButton(skull, e -> {
                Player admin = (Player) e.getWhoClicked();
                // Mo PlayerEditGui cho player duoc chon
                dev.elysium.core.ElysiumCore.getInstance().getGuiManager()
                    .open(admin, new PlayerEditGui(finalEp));
            }));
            slot++;
        }

        // Leaderboard button
        setButton(49, new GuiButton(
            new ItemBuilder(Material.GOLD_INGOT)
                .name("&6Leaderboard")
                .lore("&7Xem top player")
                .glow()
                .build()
        ));
    }
              }
