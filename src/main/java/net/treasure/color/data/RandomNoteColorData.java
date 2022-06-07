package net.treasure.color.data;

import xyz.xenondevs.particle.data.color.NoteColor;
import xyz.xenondevs.particle.utils.MathUtils;

public class RandomNoteColorData extends ColorData {

    public RandomNoteColorData(int min, int max) {
        super(0, false, true, min, max);
    }

    public NoteColor next() {
        return new NoteColor(MathUtils.generateRandomInteger(min, max));
    }
}