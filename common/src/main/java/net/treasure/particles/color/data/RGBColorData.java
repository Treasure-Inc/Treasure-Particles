package net.treasure.particles.color.data;

import net.treasure.particles.color.scheme.ColorScheme;
import net.treasure.particles.effect.data.EffectData;
import org.bukkit.Color;

public class RGBColorData extends ColorData {

    private final ColorScheme color;

    public RGBColorData(ColorScheme color, float speed, boolean revertWhenDone, boolean stopCycle) {
        super(speed, revertWhenDone, stopCycle);
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

    @Override
    public RGBColorData clone() {
        return new RGBColorData(color, speed, revertWhenDone, stopCycle);
    }
}