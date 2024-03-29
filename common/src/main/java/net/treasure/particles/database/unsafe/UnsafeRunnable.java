package net.treasure.particles.database.unsafe;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface UnsafeRunnable {
    void accept(ResultSet rs) throws SQLException;
}