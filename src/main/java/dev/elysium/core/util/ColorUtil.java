package dev.elysium.core.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

public final class ColorUtil {

    private static final LegacyComponentSerializer LEGACY =
        LegacyComponentSerializer.legacyAmpersand();

    private ColorUtil() {}

    /** Chuyen &mau -> mau Minecraft */
    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /** Chuyen &mau -> Adventure Component (Paper 1.21) */
    public static Component component(String text) {
        if (text == null) return Component.empty();
        return LEGACY.deserialize(text);
    }

    /** Xoa mau */
    public static String strip(String text) {
        return ChatColor.stripColor(color(text));
    }

    /**
     * Tao progress bar dang text.
     * Vi du: progressBar(50, 100, 10, 'â–ˆ', 'â–'', "&a", "&8")
     * -> "â–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–'â–'â–'â–'â–'"
     */
    public static String progressBar(int current, int max, int length,
                                     char filled, char empty,
                                     String filledColor, String emptyColor) {
        if (max <= 0) max = 1;
        int count = (int) Math.round(Math.min(1.0, (double) current / max) * length);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(color(i < count ? filledColor : emptyColor));
            sb.append(i < count ? filled : empty);
        }
        return sb.toString();
    }

    /** Can giua text trong chat */
    public static String center(String text) {
        int spaces = (int) Math.floor((80 - strip(text).length()) / 2.0);
        return " ".repeat(Math.max(0, spaces)) + text;
    }
}
