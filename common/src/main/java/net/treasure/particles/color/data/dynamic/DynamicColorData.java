package net.treasure.particles.color.data.dynamic;

import net.treasure.particles.color.data.ColorData;
import net.treasure.particles.effect.data.EffectData;
import org.bukkit.Color;

public class DynamicColorData extends ColorData {

    public DynamicColorData(float speed, boolean revertWhenDone, boolean stopCycle) {
        super(speed, revertWhenDone, stopCycle);
    }

    @Override
    public Color next(EffectData data) {
        var colors = data.getColorPreference().getColors();
        max = colors.size();
        var index = index();
        if (index > max) {
            currentIndex = 0;
            index = 0;
        }
        return colors.get(index);
    }
}