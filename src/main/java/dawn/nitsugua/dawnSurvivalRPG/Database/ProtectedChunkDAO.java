package dawn.nitsugua.dawnSurvivalRPG.Database;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.DatabaseManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProtectedChunkDAO {
    private final DawnSurvivalRPG plugin;
    private final DatabaseManager db;

    public ProtectedChunkDAO(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.db = plugin.getDatabaseManager();
    }

    public CompletableFuture<Void> saveChunkAsync(String worldName, int chunkX, int chunkZ, int minY, int maxY, String structureKey) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT OR REPLACE INTO protected_chunks (world_name, chunk_x, chunk_z, min_y, max_y, structure_key) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)){
                stmt.setString(1, worldName);
                stmt.setInt(2, chunkX);
                stmt.setInt(3, chunkZ);
                stmt.setInt(4, minY);
                stmt.setInt(5, maxY);
                stmt.setString(6, structureKey);
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLog().error("Failed to save protected chunk: " + e.getMessage());
            }
        }, db.getDbExecutor());
    }

    public CompletableFuture<List<ProtectedChunkData>> loadAllChunksAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<ProtectedChunkData> chunks = new ArrayList<>();
            String sql = "SELECT world_name, chunk_x, chunk_z, min_y, max_y, structure_key FROM protected_chunks";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while(rs.next()) {
                    chunks.add(new ProtectedChunkData(
                            rs.getString("world_name"),
                            rs.getInt("chunk_x"),
                            rs.getInt("chunk_z"),
                            rs.getInt("min_y"),
                            rs.getInt("max_y"),
                            rs.getString("structure_key")
                    ));
                }
            } catch (SQLException e) {
                plugin.getLog().error("Failed to load protected chunks: " + e.getMessage());
            }
            return chunks;
        }, db.getDbExecutor());
    }

    public CompletableFuture<Integer> updateYRangeByStructureAsync(String structureKey, int newMinY, int newMaxY) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "UPDATE protected_chunks SET min_y = ?, max_y = ? WHERE structure_key = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setInt(1, newMinY);
                stmt.setInt(2, newMaxY);
                stmt.setString(3, structureKey);
                int rowsUpdated = stmt.executeUpdate();
                plugin.getLog().info("[Database] Updated Y-range for " + rowsUpdated + " chunks of " + structureKey);
                return rowsUpdated;
            } catch (SQLException e) {
                plugin.getLog().error("Failed to update Y-range for " + structureKey + ": " + e.getMessage());
                return 0;
            }
        }, db.getDbExecutor());
    }

    public record ProtectedChunkData(String worldName, int chunkX, int chunkZ, int minY, int maxY, String structureKey){};
}
