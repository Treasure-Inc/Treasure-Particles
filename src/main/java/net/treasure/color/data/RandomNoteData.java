package net.treasure.color.data;

import xyz.xenondevs.particle.data.color.NoteColor;
import xyz.xenondevs.particle.utils.MathUtils;

public class RandomNoteData extends ColorData {

    public RandomNoteData(int size) {
        super(0, false, true, size);
    }

    public NoteColor next() {
        return new NoteColor(MathUtils.generateRandomInteger(0, size));
    }
}