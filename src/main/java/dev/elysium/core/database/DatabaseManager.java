package dev.elysium.core.database;

import dev.elysium.core.player.ElysiumPlayer;

import java.util.UUID;

/**
 * Abstract database layer.
 * Cac plugin khac KHONG duoc truy cap SQL truc tiep.
 * Phai goi qua CoreAPI hoac DatabaseManager nay.
 */
public abstract class DatabaseManager {
    public abstract void initialize();
    public abstract void close();
    public abstract ElysiumPlayer loadPlayer(UUID uuid, String name);
    public abstract void createPlayer(ElysiumPlayer player);
    public abstract void savePlayer(ElysiumPlayer player);
}
