package net.treasure.particles.color.generator;


import org.bukkit.Color;

public class Rainbow {

    private int colorIndex;
    private double frequency;

    public Rainbow() {
        this.colorIndex = 0;
        this.frequency = 1.0D;
    }

    public Color[] colors(int size) {
        var c = new Color[size];

        this.frequency = 6.283185307179586D / (double) size;

        for (var i = 0; i < size; i++) {
            c[i] = this.color();
        }

        return c;
    }

    private Color color() {
        final var center = 128.0F;
        final var width = 127.0F;

        var index = this.colorIndex++;

        var red = (int) (Math.sin(this.frequency * (double) index + 2.0D) * (double) width + (double) center);
        var green = (int) (Math.sin(this.frequency * (double) index + 0.0D) * (double) width + (double) center);
        var blue = (int) (Math.sin(this.frequency * (double) index + 4.0D) * (double) width + (double) center);
        return Color.fromRGB(red, green, blue);
    }
}