package dev.elysium.core.database;

import dev.elysium.core.ElysiumCore;
import dev.elysium.core.leaderboard.LeaderboardEntry;
import dev.elysium.core.player.ElysiumPlayer;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLiteDatabase extends DatabaseManager {

    private final ElysiumCore plugin;
    private Connection connection;

    public SQLiteDatabase(ElysiumCore plugin) { this.plugin = plugin; }

    @Override
    public void initialize() {
        try {
            File folder = plugin.getDataFolder();
            if (!folder.exists()) folder.mkdirs();
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(
                "jdbc:sqlite:" + new File(folder, "elysium.db").getAbsolutePath());
            try (Statement s = connection.createStatement()) {
                s.execute("PRAGMA journal_mode=WAL");
                s.execute("PRAGMA synchronous=NORMAL");
            }
            createTable();
            migrateTable(); // Them cot moi neu chua co
            plugin.getLogger().info("SQLite initialized.");
        } catch (Exception e) {
            plugin.getLogger().severe("SQLite failed: " + e.getMessage());
        }
    }

    private void createTable() throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.execute("""
                CREATE TABLE IF NOT EXISTS elysium_players (
                    uuid              TEXT PRIMARY KEY,
                    name              TEXT    NOT NULL,
                    balance           REAL    DEFAULT 1000.0,
                    player_points     INTEGER DEFAULT 0,
                    player_class      TEXT    DEFAULT 'NONE',
                    level             INTEGER DEFAULT 1,
                    exp               INTEGER DEFAULT 0,
                    mana              INTEGER DEFAULT 100,
                    max_mana          INTEGER DEFAULT 100,
                    guild             TEXT    DEFAULT '',
                    island            TEXT    DEFAULT '',
                    battle_pass_level INTEGER DEFAULT 0,
                    season            INTEGER DEFAULT 1,
                    first_join        INTEGER DEFAULT 0,
                    last_seen         INTEGER DEFAULT 0
                )""");
        }
    }

    /** Them cot moi ma khong xoa data cu */
    private void migrateTable() {
        String[] migrations = {
            "ALTER TABLE elysium_players ADD COLUMN achievements TEXT DEFAULT ''",
            "ALTER TABLE elysium_players ADD COLUMN pet TEXT DEFAULT ''",
            "ALTER TABLE elysium_players ADD COLUMN skin TEXT DEFAULT ''"
        };
        for (String sql : migrations) {
            try (Statement s = connection.createStatement()) {
                s.execute(sql);
            } catch (SQLException ignored) {
                // Cot da ton tai — bo qua
            }
        }
        plugin.getLogger().info("Database migration check complete.");
    }

    @Override
    public ElysiumPlayer loadPlayer(UUID uuid, String name) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM elysium_players WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs, uuid);
        } catch (SQLException e) {
            plugin.getLogger().severe("loadPlayer: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void createPlayer(ElysiumPlayer p) {
        try (PreparedStatement ps = connection.prepareStatement("""
            INSERT INTO elysium_players
            (uuid,name,balance,player_points,player_class,level,exp,mana,max_mana,
             guild,island,battle_pass_level,season,first_join,last_seen,achievements,pet,skin)
            VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)""")) {
            bindFull(ps, p);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("createPlayer: " + e.getMessage());
        }
    }

    @Override
    public void savePlayer(ElysiumPlayer p) {
        try (PreparedStatement ps = connection.prepareStatement("""
            UPDATE elysium_players SET
            name=?,balance=?,player_points=?,player_class=?,
            level=?,exp=?,mana=?,max_mana=?,
            guild=?,island=?,battle_pass_level=?,season=?,last_seen=?,
            achievements=?,pet=?,skin=?
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
            ps.setString(14, p.serializeAchievements());
            ps.setString(15, p.getPet());
            ps.setString(16, p.getSkin());
            ps.setString(17, p.getUuid().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("savePlayer: " + e.getMessage());
        }
    }

    @Override
    public List<LeaderboardEntry> getTopByLevel(int limit) {
        List<LeaderboardEntry> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT uuid,name,level,exp,balance,player_class FROM elysium_players ORDER BY level DESC, exp DESC LIMIT ?")) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                list.add(new LeaderboardEntry(rank++,
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("name"), rs.getInt("level"),
                    rs.getLong("exp"), rs.getDouble("balance"),
                    rs.getString("player_class")));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("getTopByLevel: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<LeaderboardEntry> getTopByBalance(int limit) {
        List<LeaderboardEntry> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT uuid,name,level,exp,balance,player_class FROM elysium_players ORDER BY balance DESC LIMIT ?")) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                list.add(new LeaderboardEntry(rank++,
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("name"), rs.getInt("level"),
                    rs.getLong("exp"), rs.getDouble("balance"),
                    rs.getString("player_class")));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("getTopByBalance: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void resetAllForSeason(int newSeason) {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE elysium_players SET level=1,exp=0,season=?,battle_pass_level=0,achievements=''")) {
            ps.setInt(1, newSeason);
            int rows = ps.executeUpdate();
            plugin.getLogger().info("Season reset: " + rows + " players updated.");
        } catch (SQLException e) {
            plugin.getLogger().severe("resetAllForSeason: " + e.getMessage());
        }
    }

    private void bindFull(PreparedStatement ps, ElysiumPlayer p) throws SQLException {
        ps.setString(1,  p.getUuid().toString()); ps.setString(2,  p.getName());
        ps.setDouble(3,  p.getBalance());         ps.setInt(4,     p.getPlayerPoints());
        ps.setString(5,  p.getPlayerClass());     ps.setInt(6,     p.getLevel());
        ps.setLong(7,    p.getExp());             ps.setInt(8,     p.getMana());
        ps.setInt(9,     p.getMaxMana());         ps.setString(10, p.getGuild());
        ps.setString(11, p.getIsland());          ps.setInt(12,    p.getBattlePassLevel());
        ps.setInt(13,    p.getSeason());          ps.setLong(14,   p.getFirstJoin());
        ps.setLong(15,   p.getLastSeen());        ps.setString(16, p.serializeAchievements());
        ps.setString(17, p.getPet());             ps.setString(18, p.getSkin());
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
        // Safe load cot moi (co the chua ton tai tren DB cu)
        try { ep.loadAchievements(rs.getString("achievements")); } catch (SQLException ignored) {}
        try { ep.setPet(rs.getString("pet")); } catch (SQLException ignored) {}
        try { ep.setSkin(rs.getString("skin")); } catch (SQLException ignored) {}
        return ep;
    }

    @Override
    public void close() {
        try { if (connection != null && !connection.isClosed()) connection.close(); }
        catch (SQLException e) { plugin.getLogger().severe("close: " + e.getMessage()); }
    }
                       }
