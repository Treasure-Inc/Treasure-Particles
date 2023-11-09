package net.treasure.particles.database;

import net.treasure.particles.database.unsafe.UnsafeQuery;
import net.treasure.particles.database.unsafe.UnsafeRunnable;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Database {

    public abstract boolean connect();

    public abstract Connection getConnection() throws SQLException;

    public abstract void closeComponents(Connection connection, PreparedStatement statement, ResultSet rs);

    public abstract void close();

    public void update(@Language("SQL") String query, Object... objects) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(query);
            if (objects.length > 0)
                for (int i = 0, length = objects.length; i < length; i++)
                    ps.setObject(i + 1, objects[i]);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeComponents(connection, ps, null);
        }
    }

    public void query(@Language("SQL") String query, UnsafeRunnable unsafe, Object... objects) {
        get(query, rs -> {
            unsafe.accept(rs);
            return null;
        }, objects);
    }

    public <T> T get(@Language("SQL") String query, UnsafeQuery<T> unsafe, Object... objects) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(query);
            if (objects.length > 0)
                for (int i = 0, length = objects.length; i < length; i++)
                    ps.setObject(i + 1, objects[i]);

            rs = ps.executeQuery();
            return unsafe.accept(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeComponents(connection, ps, rs);
        }
        return null;
    }
}