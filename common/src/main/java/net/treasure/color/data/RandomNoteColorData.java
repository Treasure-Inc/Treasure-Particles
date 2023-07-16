package net.treasure.color.data;

import net.treasure.util.math.MathUtils;

public class RandomNoteColorData extends ColorData {

    public RandomNoteColorData(int min, int max) {
        super(0, false, false, true, min, max);
    }

    public int random() {
        return MathUtils.generateRandomInteger(min, max);
    }
}