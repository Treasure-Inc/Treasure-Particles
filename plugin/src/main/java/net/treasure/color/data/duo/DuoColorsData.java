package net.treasure.color.data.duo;

import net.treasure.color.ColorScheme;
import net.treasure.color.data.ColorData;
import net.treasure.util.Pair;
import org.bukkit.Color;

public class DuoColorsData extends ColorData implements DuoImpl {

    final ColorScheme color1, color2;

    public DuoColorsData(ColorScheme color1, ColorScheme color2, float speed, boolean revertWhenDone, boolean stopCycle) {
        super(speed, revertWhenDone, stopCycle);
        this.color1 = color1;
        this.color2 = color2;
        this.max = color1.getColors().size();
    }

    @Override
    public Pair<Color, Color> nextDuo() {
        var index = index();
        return new Pair<>(color1.getColors().get(index), color2.getColors().get(index));
    }
}