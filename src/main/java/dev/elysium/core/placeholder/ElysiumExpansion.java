package dev.elysium.core.placeholder;

import dev.elysium.core.ElysiumCore;
import dev.elysium.core.api.CoreAPI;
import dev.elysium.core.player.ElysiumPlayer;
import dev.elysium.core.util.ColorUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Cac placeholder co san:
 *   %elysium_level%         - Level hien tai
 *   %elysium_exp%           - EXP hien tai
 *   %elysium_exp_required%  - EXP can de len level
 *   %elysium_exp_bar%       - Progress bar EXP
 *   %elysium_mana%          - Mana hien tai
 *   %elysium_max_mana%      - Mana toi da
 *   %elysium_mana_bar%      - Progress bar Mana
 *   %elysium_class%         - Class cua player
 *   %elysium_balance%       - So tien
 *   %elysium_guild%         - Ten guild
 *   %elysium_island%        - ID dao
 *   %elysium_season%        - Season hien tai
 *   %elysium_battlepass%    - Level battle pass
 *   %elysium_server_name%   - Ten server
 *   %elysium_age%           - Age hien tai
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
                                     10, 'â– ', 'â–¡', "&a", "&7");
            case "mana"         -> String.valueOf(ep.getMana());
            case "max_mana"     -> String.valueOf(ep.getMaxMana());
            case "mana_bar"     -> ColorUtil.progressBar(
                                     ep.getMana(), ep.getMaxMana(),
                                     10, 'â– ', 'â–¡', "&b", "&8");
            case "class"        -> ep.getPlayerClass();
            case "balance"      -> String.format("%.1f", ep.getBalance());
            case "guild"        -> ep.getGuild().isEmpty() ? "None" : ep.getGuild();
            case "island"       -> ep.getIsland().isEmpty() ? "None" : ep.getIsland();
            case "season"       -> String.valueOf(ep.getSeason());
            case "battlepass"   -> String.valueOf(ep.getBattlePassLevel());
            default             -> null;
        };
    }
}
