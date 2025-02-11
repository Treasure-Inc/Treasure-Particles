package net.treasure.particles.gui.config;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public class GUILayout {

    private final int size;
    private final String[] rows;

    public GUILayout(String id, String... rows) {
        if (rows == null || rows.length == 0) {
            this.size = 0;
            this.rows = null;
            return;
        }

        if (rows.length > 6)
            throw new IllegalArgumentException("[" + id + "] Row counts must be between 0 and 6, but was " + rows.length);

        if (Stream.of(rows).anyMatch(row -> row.length() != 9))
            throw new IllegalArgumentException("[" + id + "] Rows must contain 9 characters");

        this.rows = rows;
        this.size = rows.length * 9;
    }
}