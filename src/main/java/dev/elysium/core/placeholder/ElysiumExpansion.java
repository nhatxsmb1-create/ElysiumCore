package dev.elysium.core.placeholder;

import dev.elysium.core.ElysiumCore;
import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.ColorUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Cac placeholder:
 *   %elysium_level%        %elysium_exp%         %elysium_exp_required%
 *   %elysium_exp_bar%      %elysium_mana%         %elysium_max_mana%
 *   %elysium_mana_bar%     %elysium_class%        %elysium_balance%
 *   %elysium_guild%        %elysium_island%        %elysium_season%
 *   %elysium_battlepass%   %elysium_server_name%   %elysium_age%
 */
public class ElysiumExpansion extends PlaceholderExpansion {

    private final ElysiumCore plugin;
    public ElysiumExpansion(ElysiumCore plugin) { this.plugin = plugin; }

    @Override public @NotNull String getIdentifier() { return "elysium"; }
    @Override public @NotNull String getAuthor()     { return "Elysium"; }
    @Override public @NotNull String getVersion()    { return plugin.getDescription().getVersion(); }
    @Override public boolean persist()               { return true; }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equals("server_name")) return plugin.getConfigManager().getServerName();
        if (params.equals("age"))         return plugin.getConfigManager().getAge();

        if (player == null) return "";
        ElysiumPlayer ep = CoreAPI.getPlayer(player);
        if (ep == null) return "...";

        return switch (params.toLowerCase()) {
            case "level"        -> String.valueOf(ep.getLevel());
            case "exp"          -> String.valueOf(ep.getExp());
            case "exp_required" -> String.valueOf(ep.getExpRequired());
            case "exp_bar"      -> ColorUtil.progressBar(
                                     (int)ep.getExp(), (int)ep.getExpRequired(),
                                     10, '■', '□', "&a", "&7");
            case "mana"         -> String.valueOf(ep.getMana());
            case "max_mana"     -> String.valueOf(ep.getMaxMana());
            case "mana_bar"     -> ColorUtil.progressBar(
                                     ep.getMana(), ep.getMaxMana(),
                                     10, '■', '□', "&b", "&8");
            case "class"        -> ep.getPlayerClass().equals("NONE") ? "Chua chon" : ep.getPlayerClass();
            // Balance: uu tien Vault, fallback internal
            case "balance"      -> {
                Economy eco = plugin.getEconomy();
                yield eco != null
                    ? String.format("%.1f", eco.getBalance(player))
                    : String.format("%.1f", ep.getBalance());
            }
            case "guild"        -> ep.getGuild().isEmpty() ? "None" : ep.getGuild();
            case "island"       -> ep.getIsland().isEmpty() ? "None" : ep.getIsland();
            case "season"       -> String.valueOf(ep.getSeason());
            case "battlepass"   -> String.valueOf(ep.getBattlePassLevel());
            default             -> null;
        };
    }
}
