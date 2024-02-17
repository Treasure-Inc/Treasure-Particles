package net.treasure.particles.effect.script.argument;

import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.reader.ReaderContext;

public interface ScriptArgument<T> {
    T get(Script script, EffectData data);

    ScriptArgument<?> validate(ReaderContext<?> context) throws ReaderException;
}