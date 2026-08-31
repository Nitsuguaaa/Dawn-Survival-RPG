package dawn.nitsugua.dawnSurvivalRPG.Database;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.DatabaseManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BankDAO {
    private final DawnSurvivalRPG plugin;
    private final DatabaseManager db;

    public BankDAO(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.db = plugin.getDatabaseManager();
    }

    //READ FROM DATABASE
    public double getBalance(UUID uuid) {
        String sql = "SELECT balance FROM banks WHERE player_uuid = ?";
        try(PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            plugin.getLog().error("Failed to get balance for " + uuid + ": " + e.getMessage());
        }
        return 0.0;
    }

    //WRITE to Database (Upsert - Insert if new, Replace if exists)
    public void saveBalance(UUID uuid, double balance) {
        String sql = "INSERT OR REPLACE INTO banks (player_uuid, balance) VALUES (?, ?)";
        try(PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setDouble(2, balance);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLog().error("Failed to save balance for " + uuid + ": " + e.getMessage());
        }
    }
}
