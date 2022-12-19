package net.treasure.color.data;

import java.awt.*;

public class SingleColorData extends ColorData {

    Color color;

    public SingleColorData(Color color) {
        super(0, false);
        this.max = 1;
        this.color = color;
    }

    public Color next() {
        return color;
    }
}