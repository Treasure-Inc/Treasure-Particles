package net.treasure.effect.script.reader;

public interface ScriptReader<T> {
    T read(String line);
}