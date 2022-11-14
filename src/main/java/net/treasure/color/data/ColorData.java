package net.treasure.color.data;

import lombok.Getter;
import net.treasure.common.Patterns;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.exception.ReaderException;
import net.treasure.util.IntRange;

import java.util.regex.Matcher;

@Getter
public class ColorData {

    protected int min, max;
    float currentIndex = -1;
    float speed;
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

    public ColorData(float speed, boolean revertWhenDone, boolean note, int min, int max) {
        this.speed = speed;
        this.revertWhenDone = revertWhenDone;
        this.note = note;
        this.min = min;
        this.max = max;
    }

    public int index() {
        currentIndex += forward ? (speed) : (-speed);
        if (forward ? currentIndex >= max : currentIndex < min) {
            currentIndex = revertWhenDone ? (forward ? max - 2 : 1) : min;
            forward = revertWhenDone != forward;
        }
        return (int) (currentIndex = Math.max(min, currentIndex));
    }

    public int tempIndex() {
        var currentIndex = this.currentIndex;
        currentIndex += forward ? (speed) : (-speed);
        if (forward ? currentIndex >= max : currentIndex < min) {
            currentIndex = revertWhenDone ? (forward ? max - 2 : 1) : min;
            forward = revertWhenDone != forward;
        }
        return (int) (Math.max(min, currentIndex));
    }

    public static ColorData initialize(String input) throws ReaderException {
        Matcher colorMatcher = Patterns.INNER_SCRIPT.matcher(input);
        String colorName = "";
        boolean revertWhenDone = false, note = false;
        float colorSpeed = 1;

        int min = 0, max = 1;

        while (colorMatcher.find()) {
            String type = colorMatcher.group("type");
            String value = colorMatcher.group("value");
            switch (type) {
                case "name" -> colorName = value;
                case "revertWhenDone" -> revertWhenDone = Boolean.parseBoolean(value);
                case "note" -> note = Boolean.parseBoolean(value);
                case "speed" -> {
                    try {
                        colorSpeed = Float.parseFloat(value);
                    } catch (Exception ignored) {
                        throw new ReaderException("Unexpected speed value: " + value);
                    }
                }
                case "size" -> {
                    try {
                        max = Integer.parseInt(value);
                    } catch (Exception ignored) {
                        var range = IntRange.of(value);
                        if (range == null)
                            throw new ReaderException("Unexpected size value: " + value);
                        min = range.min();
                        max = range.max();
                    }
                }
            }
        }
        if (note) {
            return switch (colorName) {
                case "random-note" -> new RandomNoteColorData(min, max);
                case "rainbow" -> new ColorData(colorSpeed, revertWhenDone, true, min, 24);
                default -> {
                    if (!colorName.isEmpty())
                        throw new ReaderException("Unexpected color name value (note): " + colorName);
                    yield new ColorData(colorSpeed, revertWhenDone, true, min, max);
                }
            };
        } else {
            var color = TreasurePlugin.getInstance().getColorManager().get(colorName);
            if (color == null) throw new ReaderException("Unexpected color scheme name value: " + colorName);
            return new RGBColorData(color, colorSpeed, revertWhenDone);
        }
    }
}