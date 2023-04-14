package net.treasure.color.scheme;

import lombok.Getter;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ColorScheme {

    final String key;
    final String displayName;
    final List<Color> colors;

    public ColorScheme(String key, String displayName) {
        this.key = key;
        this.displayName = displayName == null ? key : displayName;
        this.colors = new ArrayList<>();
    }
}