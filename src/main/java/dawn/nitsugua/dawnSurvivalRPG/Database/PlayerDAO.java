package dawn.nitsugua.dawnSurvivalRPG.Database;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.DatabaseManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public class PlayerDAO {
    private final DawnSurvivalRPG plugin;
    private final DatabaseManager db;

    public PlayerDAO(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.db = plugin.getDatabaseManager();
    }

    public CompletableFuture<DawnPlayer> loadPlayerAsync(Player player) {
        return CompletableFuture.supplyAsync(() -> {
           String sql = "SELECT uuid, username, first_join, last_seen FROM players WHERE uuid = ?";
           try(PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, player.getUniqueId().toString());
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                    DawnPlayer p = new DawnPlayer(player.getUniqueId());
                    p.setUsername(rs.getString("username"));
                    p.setFirstJoin(rs.getLong("first_join"));
                    p.setLastSeen(rs.getLong("last_seen"));
                    return p;
                }
           } catch (Exception e) {
               plugin.getLog().error("Failed to load player "+ player.getName() +": " + e.getMessage());
           }
           DawnPlayer dawnPlayer = new DawnPlayer(player.getUniqueId());
           dawnPlayer.setUsername(player.getName());
           dawnPlayer.flagFirstJoin();
           dawnPlayer.setFirstJoin(System.currentTimeMillis());
           return dawnPlayer;
        }, db.getDbExecutor());
    }

    public CompletableFuture<Void> savePlayerAsync(DawnPlayer player) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT OR REPLACE INTO players (uuid, username, first_join, last_seen) VALUES (?, ?, ?, ?)";
            try(PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, player.getPlayerId().toString());
                stmt.setString(2, player.getUsername());
                stmt.setLong(3, player.getFirstJoin());
                stmt.setLong(4, player.getLastSeen());
                stmt.executeUpdate();
            } catch (Exception e) {
                plugin.getLog().error("Failed to save player " + player.getUsername() +": " + e.getMessage());
            }
            }, db.getDbExecutor());
    }
}
