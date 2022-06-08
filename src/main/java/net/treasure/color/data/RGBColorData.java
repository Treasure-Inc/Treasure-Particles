package net.treasure.color.data;

import net.treasure.color.Color;

public class RGBColorData extends ColorData {

    final Color color;

    public RGBColorData(Color color, float speed, boolean revertWhenDone) {
        super(speed, revertWhenDone);
        this.color = color;
        this.max = color.getColors().size();
    }

    public java.awt.Color next() {
        return color.getColors().get(index());
    }

    public java.awt.Color tempNext() {
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