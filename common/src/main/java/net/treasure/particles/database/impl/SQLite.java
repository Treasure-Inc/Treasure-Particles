package net.treasure.particles.database.impl;

import lombok.Getter;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.database.Database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLite extends Database {

    @Getter
    Connection connection;

    @Override
    public boolean connect() {
        File dataFolder = new File(TreasureParticles.getDataFolder(), "database.db");
        boolean exists = dataFolder.exists();
        if (!exists) {
            try {
                exists = dataFolder.createNewFile();
            } catch (IOException e) {
                TreasureParticles.logger().log(Level.SEVERE, "File write error: database.db", e);
                return false;
            }
        }
        if (!exists)
            return false;


        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return true;
        } catch (Exception e) {
            TreasureParticles.logger().log(Level.SEVERE, "SQLite exception on initialize", e);
        }
        return true;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void closeComponents(Connection connection, PreparedStatement statement, ResultSet rs) {
        try {
            if (statement != null)
                statement.close();
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            TreasureParticles.logger().log(Level.WARNING, "Couldn't close connection components", e);
        }
    }
}