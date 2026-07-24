package dev.elysium.core.gui;

import dev.elysium.core.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta  meta;

    public ItemBuilder(Material m)           { item = new ItemStack(m);        meta = item.getItemMeta(); }
    public ItemBuilder(Material m, int amt)  { item = new ItemStack(m, amt);   meta = item.getItemMeta(); }

    public ItemBuilder name(String name) {
        meta.setDisplayName(ColorUtil.color(name));
        return this;
    }

    public ItemBuilder lore(String... lines) {
        meta.setLore(Arrays.stream(lines).map(ColorUtil::color).collect(Collectors.toList()));
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        meta.setLore(lines.stream().map(ColorUtil::color).collect(Collectors.toList()));
        return this;
    }

    /** Glowing effect khong hien enchant (Paper 1.20.5+) */
    public ItemBuilder glow() {
        meta.setEnchantmentGlintOverride(true);
        return this;
    }

    public ItemBuilder hideFlags() {
        meta.addItemFlags(ItemFlag.values());
        return this;
    }

    public ItemBuilder customModelData(int data) {
        meta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    // ── Static helpers ────────────────────────────────────────────────────────

    public static ItemStack filler(Material m) {
        return new ItemBuilder(m).name("&r").hideFlags().build();
    }

    /** Gray stained glass pane — filler pho bien nhat */
    public static ItemStack filler() {
        return filler(Material.GRAY_STAINED_GLASS_PANE);
    }
}
