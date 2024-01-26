package net.treasure.particles.gui.config;

import lombok.Getter;

@Getter
public class GUILayout {

    private final int size;
    private final String[] rows;

    public GUILayout(String... rows) {
        this.rows = rows;
        this.size = rows.length * 9;
        if (size < 0 || size > 54)
            throw new IllegalStateException("Incorrect usage of GUI layout (Too many slots)");
    }
}