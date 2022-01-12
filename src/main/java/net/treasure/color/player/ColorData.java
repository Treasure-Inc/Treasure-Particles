package net.treasure.color.player;

import lombok.Getter;
import lombok.Setter;
import net.treasure.color.Color;
import net.treasure.core.TreasurePlugin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorData {

    private static final Pattern COLOR = Pattern.compile("(|(?<=;))(?<type>name|revertWhenDone|speed)(?:=)(?<value>[a-zA-Z0-9{}.]+)(?:(?=;)|)");

    @Getter
    private final List<java.awt.Color> colors;

    @Getter
    private int currentIndex = -1;

    @Getter
    private final float speed;

    @Getter
    @Setter
    private boolean revertWhenDone, forward = true;

    public ColorData(Color color, float speed, boolean revertWhenDone) {
        this.colors = color.getColors();
        this.speed = speed;
        this.revertWhenDone = revertWhenDone;
    }

    public java.awt.Color next() {
        currentIndex += (int) (forward ? (speed) : (-speed));
        if (forward ? currentIndex >= colors.size() : currentIndex < 0) {
            currentIndex = revertWhenDone ? (forward ? colors.size() - 2 : 1) : 0;
            forward = revertWhenDone != forward;
        }
        return colors.get(currentIndex = Math.max(0, currentIndex));
    }

    public org.bukkit.Color nextBukkit() {
        currentIndex += (int) (forward ? (speed) : (-speed));
        if (forward ? currentIndex >= colors.size() : currentIndex < 0) {
            currentIndex = revertWhenDone ? (forward ? colors.size() - 2 : 1) : 0;
            forward = revertWhenDone != forward;
        }
        java.awt.Color color = colors.get(currentIndex = Math.max(0, currentIndex));
        return org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static ColorData initialize(String input) {
        Matcher colorMatcher = COLOR.matcher(input);
        String colorName = "";
        boolean revertWhenDone = false;
        float colorSpeed = 1;
        while (colorMatcher.find()) {
            String _type = colorMatcher.group("type");
            String _colorValue = colorMatcher.group("value");
            if (_type.equalsIgnoreCase("name"))
                colorName = _colorValue;
            else if (_type.equalsIgnoreCase("revertWhenDone"))
                revertWhenDone = Boolean.parseBoolean(_colorValue);
            else if (_type.equalsIgnoreCase("speed")) {
                try {
                    colorSpeed = Float.parseFloat(_colorValue);
                } catch (Exception ignored) {
                }
            }
        }
        return new ColorData(TreasurePlugin.getInstance().getColorManager().get(colorName), colorSpeed, revertWhenDone);
    }
}