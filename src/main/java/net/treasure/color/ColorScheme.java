package net.treasure.color;

import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ColorScheme {

    final String key;
    final List<Color> colors;

    public ColorScheme(String key) {
        this.key = key;
        this.colors = new ArrayList<>();
    }
}