package net.treasure.color.data;

import net.treasure.color.ColorScheme;
import net.treasure.effect.data.EffectData;
import org.bukkit.Color;

public class RGBColorData extends ColorData {

    final ColorScheme color;

    public RGBColorData(ColorScheme color, float speed, boolean revertWhenDone) {
        super(speed, revertWhenDone);
        this.color = color;
        this.max = color.getColors().size();
    }

    @Override
    public Color next(EffectData data) {
        return color.getColors().get(index());
    }

    @Override
    public Color tempNext(EffectData data) {
        var tempSpeed = this.speed;
        this.speed += this.max / 2f;
        var next = color.getColors().get(tempIndex());
        this.speed = tempSpeed;
        return next;
    }
}