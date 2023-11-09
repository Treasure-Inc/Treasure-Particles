package net.treasure.particles.effect.script.basic.reader;

import lombok.AllArgsConstructor;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.reader.ReaderContext;
import net.treasure.particles.effect.script.reader.ScriptReader;

import java.util.function.Function;

@AllArgsConstructor
public class BasicScriptReader<T extends Script> extends ScriptReader<ReaderContext<T>, T> {
    final Function<String, T> callable;

    @Override
    public T read(Effect effect, String type, String line) throws ReaderException {
        try {
            return callable.apply(line);
        } catch (Exception e) {
            return null;
        }
    }
}