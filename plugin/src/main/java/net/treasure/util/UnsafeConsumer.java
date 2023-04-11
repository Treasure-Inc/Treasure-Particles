package net.treasure.util;

@FunctionalInterface
public interface UnsafeConsumer<T> {
    void accept(T t) throws Exception;
}