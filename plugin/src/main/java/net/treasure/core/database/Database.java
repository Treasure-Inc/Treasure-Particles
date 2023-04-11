package net.treasure.core.database;

import net.treasure.core.TreasurePlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        if (connection != null) {
            try {
                if (!connection.isClosed())
                    return connection;
            } catch (SQLException ignored) {
            }
        }

        File dataFolder = new File(TreasurePlugin.getInstance().getDataFolder(), "database.db");
        boolean exists = dataFolder.exists();
        if (!exists) {
            try {
                exists = dataFolder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                TreasurePlugin.logger().log(Level.SEVERE, "File write error: database.db", e);
                return null;
            }
        }
        if (!exists)
            return null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (Exception ex) {
            ex.printStackTrace();
            TreasurePlugin.logger().log(Level.SEVERE, "SQLite exception on initialize", ex);
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
        } catch (SQLException e) {
            TreasurePlugin.logger().log(Level.WARNING, "Failed to close PreparedStatement", e);
        }
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            TreasurePlugin.logger().log(Level.WARNING, "Failed to close ResultSet ", e);
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (Exception ignored) {
        }
    }
}