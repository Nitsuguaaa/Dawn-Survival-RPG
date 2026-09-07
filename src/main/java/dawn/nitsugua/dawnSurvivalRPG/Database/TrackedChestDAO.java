package dawn.nitsugua.dawnSurvivalRPG.Database;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.DatabaseManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TrackedChestDAO {
    private final DawnSurvivalRPG plugin;
    private final DatabaseManager db;

    public TrackedChestDAO(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.db = plugin.getDatabaseManager();
    }

    public CompletableFuture<Void> saveChestAsync(String worldName, int x, int y, int z, String lootTableKey) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT OR IGNORE INTO tracked_chests (world_name, x, y, z, loot_table_key, first_generated_at) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, worldName);
                stmt.setInt(2, x);
                stmt.setInt(3, y);
                stmt.setInt(4, z);
                stmt.setString(5, lootTableKey);
                stmt.setLong(6, System.currentTimeMillis());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLog().error("Failed to track chest at [" + worldName + " " + x + ", " + y + ", " + z + "]: " + e.getMessage());
            }
        }, db.getDbExecutor());
    }

    public CompletableFuture<List<TrackedChestData>> loadAllChestsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<TrackedChestData> chests = new ArrayList<>();
            String sql = "SELECT world_name, x, y, z, loot_table_key, first_generated_at FROM tracked_chests";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    chests.add(new TrackedChestData(
                            rs.getString("world_name"),
                            rs.getInt("x"),
                            rs.getInt("y"),
                            rs.getInt("z"),
                            rs.getString("loot_table_key"),
                            rs.getLong("first_generated_at")
                    ));
                }
            } catch (SQLException e) {
                plugin.getLog().error("Failed to load tracked chests: " + e.getMessage());
            }
            return chests;
        }, db.getDbExecutor());
    }

    public CompletableFuture<Integer> deleteChestAsync(String worldName, int x, int y, int z) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "DELETE FROM tracked_chests WHERE world_name = ? AND x = ? AND y = ? AND z = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, worldName);
                stmt.setInt(2, x);
                stmt.setInt(3, y);
                stmt.setInt(4, z);
                return stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLog().error("Failed to delete tracked chest: " + e.getMessage());
                return 0;
            }
        }, db.getDbExecutor());
    }

    public record TrackedChestData(String worldName, int x, int y, int z, String lootTableKey, long firstGeneratedAt) {}
}
