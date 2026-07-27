package dev.elysium.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.elysium.core.ElysiumCore;
import dev.elysium.core.leaderboard.LeaderboardEntry;
import dev.elysium.core.player.ElysiumPlayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQLDatabase extends DatabaseManager {

    private final ElysiumCore plugin;
    private HikariDataSource ds;

    public MySQLDatabase(ElysiumCore plugin) { this.plugin = plugin; }

    @Override
    public void initialize() {
        var c = plugin.getConfig();
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:mysql://"
            + c.getString("database.mysql.host","localhost") + ":"
            + c.getInt("database.mysql.port",3306) + "/"
            + c.getString("database.mysql.database","elysium")
            + "?useSSL=false&autoReconnect=true&characterEncoding=UTF-8");
        cfg.setUsername(c.getString("database.mysql.username","root"));
        cfg.setPassword(c.getString("database.mysql.password",""));
        cfg.setMaximumPoolSize(c.getInt("database.mysql.pool-size",10));
        cfg.setMinimumIdle(2);
        cfg.setConnectionTimeout(30_000);
        cfg.setIdleTimeout(600_000);
        cfg.setMaxLifetime(1_800_000);
        cfg.setPoolName("ElysiumPool");
        ds = new HikariDataSource(cfg);
        createTable();
        plugin.getLogger().info("MySQL initialized.");
    }

    private void createTable() {
        try (Connection conn = ds.getConnection(); Statement s = conn.createStatement()) {
            s.execute("""
                CREATE TABLE IF NOT EXISTS elysium_players (
                    uuid              VARCHAR(36) PRIMARY KEY,
                    name              VARCHAR(16) NOT NULL,
                    balance           DOUBLE      DEFAULT 1000.0,
                    player_points     INT         DEFAULT 0,
                    player_class      VARCHAR(32) DEFAULT 'NONE',
                    level             INT         DEFAULT 1,
                    exp               BIGINT      DEFAULT 0,
                    mana              INT         DEFAULT 100,
                    max_mana          INT         DEFAULT 100,
                    guild             VARCHAR(64) DEFAULT '',
                    island            VARCHAR(64) DEFAULT '',
                    battle_pass_level INT         DEFAULT 0,
                    season            INT         DEFAULT 1,
                    first_join        BIGINT      DEFAULT 0,
                    last_seen         BIGINT      DEFAULT 0
                )""");
        } catch (SQLException e) {
            plugin.getLogger().severe("createTable: " + e.getMessage());
        }
    }

    @Override
    public ElysiumPlayer loadPlayer(UUID uuid, String name) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM elysium_players WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs, uuid);
        } catch (SQLException e) { plugin.getLogger().severe("loadPlayer: " + e.getMessage()); }
        return null;
    }

    @Override
    public void createPlayer(ElysiumPlayer p) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO elysium_players
                (uuid,name,balance,player_points,player_class,level,exp,mana,max_mana,
                 guild,island,battle_pass_level,season,first_join,last_seen)
                VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)""")) {
            bind(ps, p);
            ps.executeUpdate();
        } catch (SQLException e) { plugin.getLogger().severe("createPlayer: " + e.getMessage()); }
    }

    @Override
    public void savePlayer(ElysiumPlayer p) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                UPDATE elysium_players SET
                name=?,balance=?,player_points=?,player_class=?,
                level=?,exp=?,mana=?,max_mana=?,
                guild=?,island=?,battle_pass_level=?,season=?,last_seen=?
                WHERE uuid=?""")) {
            ps.setString(1,  p.getName());
            ps.setDouble(2,  p.getBalance());
            ps.setInt(3,     p.getPlayerPoints());
            ps.setString(4,  p.getPlayerClass());
            ps.setInt(5,     p.getLevel());
            ps.setLong(6,    p.getExp());
            ps.setInt(7,     p.getMana());
            ps.setInt(8,     p.getMaxMana());
            ps.setString(9,  p.getGuild());
            ps.setString(10, p.getIsland());
            ps.setInt(11,    p.getBattlePassLevel());
            ps.setInt(12,    p.getSeason());
            ps.setLong(13,   p.getLastSeen());
            ps.setString(14, p.getUuid().toString());
            ps.executeUpdate();
        } catch (SQLException e) { plugin.getLogger().severe("savePlayer: " + e.getMessage()); }
    }

    // ── Leaderboard ───────────────────────────────────────────────────────────

    @Override
    public List<LeaderboardEntry> getTopByLevel(int limit) {
        List<LeaderboardEntry> list = new ArrayList<>();
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT uuid,name,level,exp,balance,player_class FROM elysium_players ORDER BY level DESC, exp DESC LIMIT ?")) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                list.add(new LeaderboardEntry(
                    rank++,
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("name"),
                    rs.getInt("level"),
                    rs.getLong("exp"),
                    rs.getDouble("balance"),
                    rs.getString("player_class")
                ));
            }
        } catch (SQLException e) { plugin.getLogger().severe("getTopByLevel: " + e.getMessage()); }
        return list;
    }

    @Override
    public List<LeaderboardEntry> getTopByBalance(int limit) {
        List<LeaderboardEntry> list = new ArrayList<>();
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT uuid,name,level,exp,balance,player_class FROM elysium_players ORDER BY balance DESC LIMIT ?")) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                list.add(new LeaderboardEntry(
                    rank++,
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("name"),
                    rs.getInt("level"),
                    rs.getLong("exp"),
                    rs.getDouble("balance"),
                    rs.getString("player_class")
                ));
            }
        } catch (SQLException e) { plugin.getLogger().severe("getTopByBalance: " + e.getMessage()); }
        return list;
    }

    // ── Season Reset ──────────────────────────────────────────────────────────

    @Override
    public void resetAllForSeason(int newSeason) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE elysium_players SET level=1,exp=0,season=?,battle_pass_level=0")) {
            ps.setInt(1, newSeason);
            int rows = ps.executeUpdate();
            plugin.getLogger().info("Season reset -> " + newSeason + " | " + rows + " players.");
        } catch (SQLException e) { plugin.getLogger().severe("resetAllForSeason: " + e.getMessage()); }
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private void bind(PreparedStatement ps, ElysiumPlayer p) throws SQLException {
        ps.setString(1,  p.getUuid().toString()); ps.setString(2,  p.getName());
        ps.setDouble(3,  p.getBalance());         ps.setInt(4,     p.getPlayerPoints());
        ps.setString(5,  p.getPlayerClass());     ps.setInt(6,     p.getLevel());
        ps.setLong(7,    p.getExp());             ps.setInt(8,     p.getMana());
        ps.setInt(9,     p.getMaxMana());         ps.setString(10, p.getGuild());
        ps.setString(11, p.getIsland());          ps.setInt(12,    p.getBattlePassLevel());
        ps.setInt(13,    p.getSeason());          ps.setLong(14,   p.getFirstJoin());
        ps.setLong(15,   p.getLastSeen());
    }

    private ElysiumPlayer map(ResultSet rs, UUID uuid) throws SQLException {
        ElysiumPlayer ep = new ElysiumPlayer(uuid, rs.getString("name"));
        ep.setBalance(rs.getDouble("balance"));
        ep.setPlayerPoints(rs.getInt("player_points"));
        ep.setPlayerClass(rs.getString("player_class"));
        ep.setLevel(rs.getInt("level"));
        ep.setExp(rs.getLong("exp"));
        ep.setMana(rs.getInt("mana"));
        ep.setMaxMana(rs.getInt("max_mana"));
        ep.setGuild(rs.getString("guild"));
        ep.setIsland(rs.getString("island"));
        ep.setBattlePassLevel(rs.getInt("battle_pass_level"));
        ep.setSeason(rs.getInt("season"));
        ep.setFirstJoin(rs.getLong("first_join"));
        ep.setLastSeen(rs.getLong("last_seen"));
        return ep;
    }

    @Override
    public void close() { if (ds != null && !ds.isClosed()) ds.close(); }
             }
