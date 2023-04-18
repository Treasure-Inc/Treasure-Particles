package net.treasure.color.data.dynamic;

import net.treasure.color.data.ColorData;
import net.treasure.effect.data.EffectData;
import org.bukkit.Color;

public class DynamicColorData extends ColorData {

    public DynamicColorData(float speed, boolean revertWhenDone, boolean stopCycle) {
        super(speed, revertWhenDone, stopCycle);
    }

    @Override
    public Color next(EffectData data) {
        var colors = data.getColorPreferences().get(data.getCurrentEffect().getKey()).getColors();
        max = colors.size();
        var index = index();
        if (index > max) {
            currentIndex = 0;
            index = 0;
        }
        return colors.get(index);
    }
}