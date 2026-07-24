package dev.elysium.core.gui;

import dev.elysium.core.ElysiumCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager implements Listener {

    private final ElysiumCore plugin;
    private final Map<UUID, ElysiumGui> open = new HashMap<>();

    public GuiManager(ElysiumCore plugin) { this.plugin = plugin; }

    public void open(Player player, ElysiumGui gui) {
        open.put(player.getUniqueId(), gui);
        gui.open(player);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        ElysiumGui gui = open.get(player.getUniqueId());
        if (gui == null || !e.getInventory().equals(gui.getInventory())) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        gui.handleClick(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;
        ElysiumGui gui = open.get(player.getUniqueId());
        if (gui == null || !e.getInventory().equals(gui.getInventory())) return;
        gui.onClose(player);
        open.remove(player.getUniqueId());
    }
}
