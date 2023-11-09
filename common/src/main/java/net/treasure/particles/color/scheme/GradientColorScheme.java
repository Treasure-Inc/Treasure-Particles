package net.treasure.particles.color.scheme;

import net.treasure.particles.color.generator.Gradient;
import org.bukkit.Color;

import java.util.Arrays;

public class GradientColorScheme extends ColorScheme {

    public GradientColorScheme(String key, String displayName, int size, Color... colors) {
        super(key, displayName);
        this.getColors().addAll(Arrays.asList(new Gradient(colors).colors(size)));
    }
}