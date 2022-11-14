package net.treasure.color;

import net.treasure.util.color.Gradient;

import java.awt.*;
import java.util.Arrays;

public class GradientColorScheme extends ColorScheme {

    public GradientColorScheme(String key, int size, Color... colors) {
        super(key);
        this.getColors().addAll(Arrays.asList(new Gradient(colors).colors(size)));
    }
}