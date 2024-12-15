package net.treasure.particles.effect.script.argument;

import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.exception.ReaderException;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.reader.ReaderContext;

import java.util.List;

public interface ScriptArgument<T> {
    String getName();

    List<String> getExamples();

    T get(Script script, EffectData data);

    ScriptArgument<?> validate(ReaderContext<?> context) throws ReaderException;

    default ReaderException invalidValues() {
        return new ReaderException("Valid values for " + getName() + " argument: " + String.join(", ", getExamples()));
    }

    static ReaderException invalidValues(String name, String... examples) {
        return new ReaderException("Valid values for " + name + " argument: " + String.join(", ", examples));
    }

    default String variableExample() {
        return "{variable}";
    }
}