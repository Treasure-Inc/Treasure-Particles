package net.treasure.color.data;

import net.treasure.effect.data.EffectData;
import org.bukkit.Color;

public class SingleColorData extends ColorData {

    Color color;

    public SingleColorData(Color color) {
        super(0, false);
        this.max = 1;
        this.color = color;
    }

    public SingleColorData(String hex) {
        super(0, false);
        this.max = 1;
        int i = Integer.decode(hex);
        this.color = Color.fromRGB((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
    }

    @Override
    public Color next(EffectData data) {
        return color;
    }
}