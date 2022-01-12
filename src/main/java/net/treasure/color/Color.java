package net.treasure.color;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Color {

    @Getter
    private final String key;

    @Getter
    private final List<java.awt.Color> colors;

    public Color(String key) {
        this.key = key;
        this.colors = new ArrayList<>();
    }
}
