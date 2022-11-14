package net.treasure.color.data;

import net.treasure.color.ColorScheme;

import java.awt.Color;

public class RGBColorData extends ColorData {

    final ColorScheme color;

    public RGBColorData(ColorScheme color, float speed, boolean revertWhenDone) {
        super(speed, revertWhenDone);
        this.color = color;
        this.max = color.getColors().size();
    }

    public Color next() {
        return color.getColors().get(index());
    }

    public Color tempNext() {
        var tempSpeed = this.speed;
        this.speed += 15f;
        var next = color.getColors().get(tempIndex());
        this.speed = tempSpeed;
        return next;
    }

    public org.bukkit.Color nextBukkit() {
        var color = next();
        return org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }
}