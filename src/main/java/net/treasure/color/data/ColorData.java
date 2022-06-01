package net.treasure.color.data;

import lombok.Getter;
import net.treasure.common.Patterns;
import net.treasure.core.TreasurePlugin;

import java.util.regex.Matcher;

@Getter
public class ColorData {

    int size;
    float currentIndex = -1;
    final float speed;
    final boolean revertWhenDone, note;
    boolean forward = true;

    public ColorData(float speed, boolean revertWhenDone) {
        this(speed, revertWhenDone, false);
    }

    public ColorData(float speed, boolean revertWhenDone, boolean note) {
        this.speed = speed;
        this.revertWhenDone = revertWhenDone;
        this.note = note;
    }

    public int index() {
        currentIndex += forward ? (speed) : (-speed);
        if (forward ? currentIndex >= size : currentIndex < 0) {
            currentIndex = revertWhenDone ? (forward ? size - 2 : 1) : 0;
            forward = revertWhenDone != forward;
        }
        return (int) (currentIndex = Math.max(0, currentIndex));
    }

    public static ColorData initialize(String input) {
        Matcher colorMatcher = Patterns.COLOR.matcher(input);
        String colorName = "";
        boolean revertWhenDone = false, note = false;
        float colorSpeed = 1;
        while (colorMatcher.find()) {
            String type = colorMatcher.group("type");
            String value = colorMatcher.group("value");
            switch (type) {
                case "name":
                    colorName = value;
                    break;
                case "revertWhenDone":
                    revertWhenDone = Boolean.parseBoolean(value);
                    break;
                case "note":
                    note = Boolean.parseBoolean(value);
                    break;
                case "speed":
                    try {
                        colorSpeed = Float.parseFloat(value);
                    } catch (Exception ignored) {
                    }
                    break;
            }
        }
        return note ? new ColorData(colorSpeed, revertWhenDone, true) : new RGBColorData(TreasurePlugin.getInstance().getColorManager().get(colorName), colorSpeed, revertWhenDone);
    }
}