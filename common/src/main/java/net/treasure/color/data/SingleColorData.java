package net.treasure.color.data;

import lombok.Getter;
import net.treasure.color.generator.Gradient;
import net.treasure.effect.data.EffectData;
import org.bukkit.Color;

@Getter
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
        this.color = Gradient.hex2Rgb(hex);
    }

    @Override
    public Color next(EffectData data) {
        return color;
    }
}