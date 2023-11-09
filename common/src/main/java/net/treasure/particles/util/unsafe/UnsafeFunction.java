package net.treasure.particles.util.unsafe;

import net.treasure.particles.effect.exception.ReaderException;

@FunctionalInterface
public interface UnsafeFunction<T, R> {
    R apply(T t) throws ReaderException;
}