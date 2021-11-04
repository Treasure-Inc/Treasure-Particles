package net.cladium.util.color;

import java.awt.*;

public class Rainbow {

    private final float center = 128.0F;
    private final float width = 127.0F;

    private int colorIndex;
    private double frequency;
    private int phase;

    public Rainbow() {
        this.phase = 0;
        this.colorIndex = 0;
        this.frequency = 1.0D;
    }

    public Color[] colors(int size) {
        Color[] c = new Color[size];

        this.frequency = 6.283185307179586D / (double) size;

        for (int i = 0; i < size; i++) {
            c[i] = this.color((float) this.phase);
        }

        return c;
    }

    private Color color(final float phase) {
        int index = this.colorIndex++;
        int red = (int) (Math.sin(this.frequency * (double) index + 2.0D + (double) phase) * (double) this.width + (double) this.center);
        int green = (int) (Math.sin(this.frequency * (double) index + 0.0D + (double) phase) * (double) this.width + (double) this.center);
        int blue = (int) (Math.sin(this.frequency * (double) index + 4.0D + (double) phase) * (double) this.width + (double) this.center);
        return new Color(red, green, blue);
    }
}
