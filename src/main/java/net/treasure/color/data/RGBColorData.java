package net.treasure.color.data;

import net.treasure.color.Color;

import java.util.List;

public class RGBColorData extends ColorData {

    final List<java.awt.Color> colors;

    public RGBColorData(Color color, float speed, boolean revertWhenDone) {
        super(speed, revertWhenDone);
        this.colors = color.getColors();
        this.size = colors.size();
    }

    public java.awt.Color next() {
        return colors.get(index());
    }

    public org.bukkit.Color nextBukkit() {
        var color = next();
        return org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }
}
