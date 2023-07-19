package net.treasure.util.unsafe;

import net.treasure.effect.exception.ReaderException;

@FunctionalInterface
public interface UnsafeFunction<T, R> {
    R apply(T t) throws ReaderException;
}