package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final DawnSurvivalRPG plugin;
    private Connection connection;

    public DatabaseManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        try {
            File dataFolder = new File(plugin.getDataFolder(), "databases");
            if(!dataFolder.exists()) dataFolder.mkdirs();

            File dbFile = new File(dataFolder, "dawn_data.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            this.connection = DriverManager.getConnection(url);
            plugin.getLog().info(TextUtil.sendLogInfo("DatabaseManager", TextUtil.LogInfo.SUCCESS, "SQLite Database Connected!"));

            createTables();
        } catch (SQLException e) {
            plugin.getLog().error("Failed to connect to SQLite: " + e.getMessage());
        }
    }

    private void createTables() {
        try(Statement stmt = connection.createStatement()) {

            //Create players table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    uuid VARCHAR(36) PRIMARY KEY,
                    username VARCHAR(16) NOT NULL,
                    first_join INTEGER NOT NULL,
                    last_seen INTERGER NOT NULL
                )
            """);

            //create banks table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS banks (
                    player_uuid VARCHAR(36) PRIMARY KEY,
                    balance REAL DEFAULT 0.0,
                    tier VARCHAR(50) DEFAULT 'COMMON',
                    last_interest INTEGER DEFAULT 0
                )
            """);

            plugin.getLog().info(TextUtil.sendLogInfo("DatabaseManager", TextUtil.LogInfo.SUCCESS, "Database tables verified!"));
        } catch(SQLException e) {
            plugin.getLog().error("Failed to create tables: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void shutdown() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLog().info(TextUtil.sendLogInfo("DatabaseManager", TextUtil.LogInfo.SUCCESS, "SQLite Database Closed!"));
            }
        } catch(SQLException e) {
            plugin.getLog().error("Failed to close database: " + e.getMessage());
        }
    }
}
