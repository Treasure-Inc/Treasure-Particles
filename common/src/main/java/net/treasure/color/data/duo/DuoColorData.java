package net.treasure.color.data.duo;

import net.treasure.color.scheme.ColorScheme;
import net.treasure.color.data.ColorData;
import net.treasure.util.tuples.Pair;
import org.bukkit.Color;

public class DuoColorData extends ColorData implements DuoImpl {

    final ColorScheme color;
    final Color duo;

    public DuoColorData(ColorScheme color, Color duo, float speed, boolean revertWhenDone, boolean stopCycle) {
        super(speed, revertWhenDone, stopCycle);
        this.color = color;
        this.duo = duo;
        this.max = color.getColors().size();
    }

    @Override
    public Pair<Color, Color> nextDuo() {
        var index = index();
        return new Pair<>(color.getColors().get(index), duo);
    }
}