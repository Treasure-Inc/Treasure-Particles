package net.treasure.core.database.impl;

import com.zaxxer.hikari.HikariDataSource;
import net.treasure.core.TreasurePlugin;
import net.treasure.core.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class MySQL extends Database {

    private HikariDataSource hikari;
    private final String address, port, databaseName, username, password;

    public MySQL(String address, String port, String databaseName, String username, String password) {
        this.address = address;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean connect() {
        // Configure hikari properties
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", address);
        hikari.addDataSourceProperty("port", port);
        hikari.addDataSourceProperty("databaseName", databaseName);
        hikari.addDataSourceProperty("user", username);
        hikari.addDataSourceProperty("password", password);
        hikari.addDataSourceProperty("characterEncoding", "UTF-8");
        return true;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return hikari.getConnection();
    }

    @Override
    public void close() {
        try {
            hikari.close();
        } catch (Exception e) {
            TreasurePlugin.logger().log(Level.WARNING, "Couldn't close database connection", e);
        }
    }

    @Override
    public void closeComponents(Connection connection, PreparedStatement statement, ResultSet rs) {
        try {
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            TreasurePlugin.logger().log(Level.WARNING, "Couldn't close connection components", e);
        }
    }
}