package dawn.nitsugua.dawnSurvivalRPG.Database;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.DatabaseManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Economy.Bank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BankDAO {
    private final DawnSurvivalRPG plugin;
    private final DatabaseManager db;

    public BankDAO(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.db = plugin.getDatabaseManager();
    }

    public CompletableFuture<Bank> loadBankAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT balance, tier, last_interest FROM banks WHERE player_uuid = ?";
            try(PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                    return new Bank(uuid, rs.getDouble("balance"), rs.getString("tier"), rs.getLong("last_interest"));
                }
            } catch(Exception e) {
                plugin.getLog().error("Failed to load bank for: " + uuid + ":" + e.getMessage());
            }
            return new Bank(uuid);
        }, db.getDbExecutor());
    }

    public CompletableFuture<Void> saveBankAsync(Bank bank) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT OR REPLACE INTO banks (player_uuid, balance, tier, last_interest) VALUES (?,?,?,?)";
            try(PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, bank.getOwnerUuid().toString());
                stmt.setDouble(2, bank.getBalance());
                stmt.setString(3, bank.getTier());
                stmt.setLong(4, bank.getLastInterest());
                stmt.executeUpdate();
            } catch (Exception e) {
                plugin.getLog().error("Failed to save bank for " + bank.getOwnerUuid() + ": " + e.getMessage());
            }
        }, db.getDbExecutor());
    }
}
