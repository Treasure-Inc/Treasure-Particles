package net.treasure.color;

import net.treasure.util.color.Gradient;

import java.util.Arrays;

public class GradientColor extends Color {

    public GradientColor(String key, int size, java.awt.Color... colors) {
        super(key);
        this.getColors().addAll(Arrays.asList(new Gradient(colors).colors(size)));
    }
}
