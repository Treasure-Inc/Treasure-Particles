package net.treasure.particles.gui.config;

import lombok.Getter;

@Getter
public class GUILayout {
    int size;
    String[] rows;

    public GUILayout(String... rows) {
        this.rows = rows;
        this.size = rows.length * 9;
        if (size < 0 || size > 54)
            throw new IllegalStateException("Incorrect usage of GUI layout (Too many slots)");
    }
}