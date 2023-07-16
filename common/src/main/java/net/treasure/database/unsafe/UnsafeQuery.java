package net.treasure.database.unsafe;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface UnsafeQuery<T> {
    T accept(ResultSet rs) throws SQLException;
}