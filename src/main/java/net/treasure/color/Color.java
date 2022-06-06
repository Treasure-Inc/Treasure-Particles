package net.treasure.color;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Color {

    @Getter
    final String key;

    @Getter
    final List<java.awt.Color> colors;

    public Color(String key) {
        this.key = key;
        this.colors = new ArrayList<>();
    }
}
