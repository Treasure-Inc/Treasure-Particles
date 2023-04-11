package net.treasure.color.data;

import lombok.Getter;
import net.treasure.color.data.duo.DuoColorData;
import net.treasure.color.data.duo.DuoColorsData;
import net.treasure.color.data.dynamic.DynamicColorData;
import net.treasure.common.Patterns;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.exception.ReaderException;
import net.treasure.util.math.IntRange;
import org.bukkit.Color;

import java.util.regex.Matcher;

@Getter
public class ColorData {

    protected int min, max;
    protected float currentIndex = -1;
    float speed;
    final boolean revertWhenDone, note, stopCycle;
    protected boolean forward = true;

    public ColorData(float speed, boolean revertWhenDone) {
        this(speed, revertWhenDone, false);
    }

    public ColorData(float speed, boolean revertWhenDone, boolean stopCycle) {
        this(speed, revertWhenDone, stopCycle, false);
    }

    public ColorData(float speed, boolean revertWhenDone, boolean stopCycle, boolean note) {
        this.speed = speed;
        this.revertWhenDone = revertWhenDone;
        this.stopCycle = stopCycle;
        this.note = note;
    }

    public ColorData(float speed, boolean revertWhenDone, boolean stopCycle, boolean note, int min, int max) {
        this.speed = speed;
        this.revertWhenDone = revertWhenDone;
        this.stopCycle = stopCycle;
        this.note = note;
        this.min = min;
        this.max = max;
    }

    public int index() {
        currentIndex += forward ? (speed) : (-speed);
        if (forward ? currentIndex >= max : currentIndex < min) {
            if (stopCycle && forward) return max - 1;
            currentIndex = revertWhenDone ? (forward ? max - 2 : 1) : min;
            forward = revertWhenDone != forward;
        }
        return (int) (currentIndex = Math.max(min, currentIndex));
    }

    public int tempIndex() {
        var currentIndex = this.currentIndex;
        currentIndex += forward ? (speed) : (-speed);
        if (forward ? currentIndex >= max : currentIndex < min) {
            if (stopCycle) return forward ? max - 1 : 0;
            currentIndex = revertWhenDone ? (forward ? max - 2 : 1) : min;
            forward = revertWhenDone != forward;
        }
        return (int) (Math.max(min, currentIndex));
    }

    public Color next(EffectData data) {
        return null;
    }

    public Color tempNext(EffectData data) {
        return next(data);
    }

    public static ColorData fromString(String input) throws ReaderException {
        Matcher colorMatcher = Patterns.INNER_SCRIPT.matcher(input);
        String colorName = "";
        String duoName = null;
        boolean revertWhenDone = false, stopCycle = false, note = false, dynamic = false;
        float colorSpeed = 1;

        int min = 0, max = 1;

        while (colorMatcher.find()) {
            String type = colorMatcher.group("type");
            String value = colorMatcher.group("value");
            switch (type) {
                case "name" -> colorName = value;
                case "duo", "duo-name" -> duoName = value;
                case "dynamic" -> dynamic = Boolean.parseBoolean(value);
                case "revert-when-done", "revert" -> revertWhenDone = Boolean.parseBoolean(value);
                case "stop-cycle", "stop" -> stopCycle = Boolean.parseBoolean(value);
                case "note" -> note = Boolean.parseBoolean(value);
                case "speed" -> {
                    try {
                        colorSpeed = Float.parseFloat(value);
                    } catch (Exception ignored) {
                        throw new ReaderException("Unexpected 'speed' value: " + value);
                    }
                }
                case "size" -> {
                    try {
                        max = Integer.parseInt(value);
                    } catch (Exception ignored) {
                        var range = IntRange.of(value);
                        if (range == null)
                            throw new ReaderException("Unexpected 'size' value: " + value);
                        min = range.min();
                        max = range.max();
                    }
                }
                default -> throw new ReaderException("Unexpected 'color' argument: " + type);
            }
        }
        if (revertWhenDone && stopCycle)
            throw new ReaderException("You can't set both 'revert-when-done' and 'stop-cycle' true");
        if (dynamic)
            return new DynamicColorData(colorSpeed, revertWhenDone, stopCycle);
        if (note) {
            return switch (colorName) {
                case "random-note" -> new RandomNoteColorData(min, max);
                case "rainbow" -> new ColorData(colorSpeed, revertWhenDone, stopCycle, true, min, 24);
                default -> {
                    if (!colorName.isEmpty())
                        throw new ReaderException("Unexpected color name value (note): " + colorName);
                    yield new ColorData(colorSpeed, revertWhenDone, stopCycle, true, min, max);
                }
            };
        } else {
            var colorScheme = TreasurePlugin.getInstance().getColorManager().getColorScheme(colorName);
            if (colorScheme == null) throw new ReaderException("Unexpected color scheme name value: " + colorName);
            if (duoName != null) {
                var duoScheme = TreasurePlugin.getInstance().getColorManager().getColorScheme(duoName);
                if (duoScheme == null) {
                    try {
                        var singleColor = new SingleColorData("#" + duoName).next(null);
                        return new DuoColorData(colorScheme, singleColor, colorSpeed, revertWhenDone, stopCycle);
                    } catch (Exception e) {
                        throw new ReaderException("Unexpected duo color scheme name value: " + duoName);
                    }
                }
                if (duoScheme.getColors().size() != colorScheme.getColors().size())
                    throw new ReaderException("Origin and Duo color schemes must have the same size");
                return new DuoColorsData(colorScheme, duoScheme, colorSpeed, revertWhenDone, stopCycle);
            }
            return new RGBColorData(colorScheme, colorSpeed, revertWhenDone, stopCycle);
        }
    }
}