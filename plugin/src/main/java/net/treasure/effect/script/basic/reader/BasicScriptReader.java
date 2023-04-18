package net.treasure.effect.script.basic.reader;

import lombok.AllArgsConstructor;
import net.treasure.effect.Effect;
import net.treasure.effect.exception.ReaderException;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.reader.ReaderContext;
import net.treasure.effect.script.reader.ScriptReader;

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