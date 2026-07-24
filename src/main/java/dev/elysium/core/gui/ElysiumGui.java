package dev.elysium.core.gui;

import dev.elysium.core.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class cho tat ca Elysium GUIs.
 *
 * Vi du su dung:
 *   public class ShopMenu extends ElysiumGui {
 *       public ShopMenu() { super("&6Shop", 54); }
 *
 *       public void build(Player p) {
 *           setButton(22, new GuiButton(
 *               new ItemBuilder(Material.DIAMOND).name("&bDiamond").build(),
 *               e -> p.sendMessage("Ban click vao Diamond!")
 *           ));
 *           fill(ItemBuilder.filler()); // fill tat ca o trong
 *       }
 *   }
 */
public abstract class ElysiumGui {

    protected Inventory inventory;
    protected final Map<Integer, GuiButton> buttons = new HashMap<>();
    protected final String title;
    protected final int size;

    public ElysiumGui(String title, int size) {
        this.title = title;
        this.size  = size;
        Component comp = ColorUtil.component(title);
        this.inventory = Bukkit.createInventory(null, size, comp);
    }

    /** Build noi dung GUI — goi moi lan open. */
    public abstract void build(Player player);

    public void open(Player player) {
        buttons.clear();
        inventory.clear();
        build(player);
        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5f, 1.2f);
    }

    public void handleClick(InventoryClickEvent e) {
        GuiButton btn = buttons.get(e.getSlot());
        if (btn != null) {
            ((Player) e.getWhoClicked()).playSound(
                e.getWhoClicked().getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            btn.onClick(e);
        }
    }

    public void onClose(Player player) {}

    public void setButton(int slot, GuiButton button) {
        buttons.put(slot, button);
        inventory.setItem(slot, button.getItem());
    }

    /** Dat item vao slot (khong co action). */
    public void fill(int slot, org.bukkit.inventory.ItemStack item) {
        inventory.setItem(slot, item);
    }

    /** Fill tat ca o trong = filler. */
    public void fill(org.bukkit.inventory.ItemStack filler) {
        for (int i = 0; i < size; i++) {
            if (inventory.getItem(i) == null) inventory.setItem(i, filler);
        }
    }

    public Inventory getInventory() { return inventory; }
    public String    getTitle()     { return title; }
}
