package net.treasure.color.generator;


import org.bukkit.Color;

public class Rainbow {

    private int colorIndex;
    private double frequency;

    public Rainbow() {
        this.colorIndex = 0;
        this.frequency = 1.0D;
    }

    public Color[] colors(int size) {
        Color[] c = new Color[size];

        this.frequency = 6.283185307179586D / (double) size;

        for (int i = 0; i < size; i++) {
            c[i] = this.color();
        }

        return c;
    }

    private Color color() {
        float center = 128.0F;
        float width = 127.0F;

        int index = this.colorIndex++;

        int red = (int) (Math.sin(this.frequency * (double) index + 2.0D) * (double) width + (double) center);
        int green = (int) (Math.sin(this.frequency * (double) index + 0.0D) * (double) width + (double) center);
        int blue = (int) (Math.sin(this.frequency * (double) index + 4.0D) * (double) width + (double) center);
        return Color.fromRGB(red, green, blue);
    }
}