package net.treasure.particles.color.scheme;

import lombok.Getter;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ColorScheme {

    private final String key;
    private final String displayName;
    private final List<Color> colors;

    public ColorScheme(String key, String displayName) {
        this.key = key;
        this.displayName = displayName == null ? key : displayName;
        this.colors = new ArrayList<>();
    }
}