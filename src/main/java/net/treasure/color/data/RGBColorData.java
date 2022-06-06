package net.treasure.color.data;

import net.treasure.color.Color;

public class RGBColorData extends ColorData {

    final Color color;

    public RGBColorData(Color color, float speed, boolean revertWhenDone) {
        super(speed, revertWhenDone);
        this.color = color;
        this.size = color.getColors().size();
    }

    public java.awt.Color next() {
        return color.getColors().get(index());
    }

    public org.bukkit.Color nextBukkit() {
        var color = next();
        return org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }
}