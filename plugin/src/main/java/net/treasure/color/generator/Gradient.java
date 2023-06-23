package net.treasure.color.generator;

import org.bukkit.Color;

public class Gradient {

    private final Color[] colors;

    private int index = 0;
    private int colorIndex = 0;

    private float factorStep = 0;
    private float phase = 0;

    public Gradient(Color... colors) {
        if (colors == null || colors.length == 0)
            this.colors = new Color[]{hex2Rgb("#ffffff"), hex2Rgb("#000000")};
        else
            this.colors = colors;
    }

    public Color[] colors(int size) {
        Color[] c = new Color[size];
        final int sectorLength = size / (this.colors.length - 1);
        this.factorStep = 1.0f / (sectorLength + this.index);
        this.phase = this.phase * sectorLength;
        this.index = 0;

        for (int i = 0; i < size; i++) {
            c[i] = this.color();
        }
        return c;
    }

    private Color color() {
        if (this.factorStep * this.index > 1) {
            this.colorIndex++;
            this.index = 0;
        }

        float factor = this.factorStep * (this.index++ + this.phase);
        if (factor > 1) {
            factor = 1 - (factor - 1);
        }

        return this.interpolate(this.colors[this.colorIndex], this.colors[this.colorIndex + 1], factor);
    }

    private Color interpolate(final Color color1, final Color color2, final float factor) {
        return Color.fromRGB(
                Math.round(color1.getRed() + factor * (color2.getRed() - color1.getRed())),
                Math.round(color1.getGreen() + factor * (color2.getGreen() - color1.getGreen())),
                Math.round(color1.getBlue() + factor * (color2.getBlue() - color1.getBlue()))
        );
    }

    public static Color hex2Rgb(String hex) {
        return Color.fromRGB(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16));
    }
}