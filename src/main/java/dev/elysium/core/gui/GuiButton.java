package dev.elysium.core.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiButton {

    private final ItemStack item;
    private final ClickAction action;

    public GuiButton(ItemStack item, ClickAction action) { this.item = item; this.action = action; }
    public GuiButton(ItemStack item)                     { this.item = item; this.action = null; }

    public void onClick(InventoryClickEvent e) { if (action != null) action.onClick(e); }
    public ItemStack getItem() { return item; }

    @FunctionalInterface
    public interface ClickAction {
        void onClick(InventoryClickEvent e);
    }
}
