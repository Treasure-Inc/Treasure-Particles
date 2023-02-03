package net.treasure.effect.script.sound.reader;

import net.treasure.effect.Effect;
import net.treasure.effect.script.ReaderContext;
import net.treasure.effect.script.sound.PlaySound;

public class SoundReaderContext extends ReaderContext<PlaySound> {
    public SoundReaderContext(Effect effect, String type, String line) {
        super(effect, type, line, new PlaySound());
    }
}
