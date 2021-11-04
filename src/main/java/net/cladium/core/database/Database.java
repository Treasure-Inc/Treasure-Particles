package net.cladium.core.database;

import net.cladium.core.CladiumPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public class Database {

    Connection connection;

    public boolean connect() {
        connection = getConnection();
        if (connection == null)
            return false;

        load();
        return true;
    }

    public Connection getConnection() {
        File dataFolder = new File(CladiumPlugin.getInstance().getDataFolder(), "database.db");
        boolean exists = dataFolder.exists();
        if (!exists) {
            try {
                exists = dataFolder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                CladiumPlugin.getInstance().getLogger().log(Level.SEVERE, "File write error: database.db");
                return null;
            }
        }
        if (!exists)
            return null;
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (Exception ex) {
            ex.printStackTrace();
            CladiumPlugin.getInstance().getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        }
        return null;
    }

    public void load() {
        update("CREATE TABLE IF NOT EXISTS data (`uuid` varchar(100) NOT NULL, `data` BLOB NOT NULL, PRIMARY KEY(`uuid`))");
    }

    public void update(String query, Object... objects) {
        execute(query, getConnection(), objects);
    }

    private void execute(String query, Connection connection, Object... objects) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(query);
            if (objects.length > 0) {
                int i = 1;
                for (Object object : objects) {
                    ps.setObject(i++, object);
                }
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(ps, null);
        }
    }

    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            CladiumPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
        }
    }
}
