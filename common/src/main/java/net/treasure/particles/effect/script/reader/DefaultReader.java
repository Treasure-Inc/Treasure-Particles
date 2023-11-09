package net.treasure.particles.effect.script.reader;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.exception.ReaderException;

public abstract class DefaultReader<S> {
    public abstract S read(Effect effect, String type, String line) throws ReaderException;
}