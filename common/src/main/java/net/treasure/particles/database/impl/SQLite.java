package net.treasure.particles.database.impl;

import lombok.Getter;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.database.Database;
import net.treasure.particles.util.logging.ComponentLogger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class SQLite extends Database {

    private Connection connection;

    @Override
    public boolean connect() {
        File dataFolder = new File(TreasureParticles.getDataFolder(), "database.db");
        boolean exists = dataFolder.exists();
        if (!exists) {
            try {
                exists = dataFolder.createNewFile();
            } catch (IOException e) {
                ComponentLogger.log("File write error: database.db", e);
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
            ComponentLogger.log("SQLite exception on initialize", e);
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
            ComponentLogger.log("Couldn't close connection components", e);
        }
    }
}