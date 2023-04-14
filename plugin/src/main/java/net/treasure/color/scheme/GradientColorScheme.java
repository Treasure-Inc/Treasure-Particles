package net.treasure.color.scheme;

import net.treasure.color.generator.Gradient;
import net.treasure.color.scheme.ColorScheme;
import org.bukkit.Color;

import java.util.Arrays;

public class GradientColorScheme extends ColorScheme {

    public GradientColorScheme(String key, String displayName, int size, Color... colors) {
        super(key, displayName);
        this.getColors().addAll(Arrays.asList(new Gradient(colors).colors(size)));
    }
}