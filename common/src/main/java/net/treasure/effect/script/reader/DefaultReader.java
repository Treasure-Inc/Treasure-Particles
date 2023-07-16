package net.treasure.effect.script.reader;

import net.treasure.effect.Effect;
import net.treasure.effect.exception.ReaderException;

public abstract class DefaultReader<S> {
    public abstract S read(Effect effect, String type, String line) throws ReaderException;
}