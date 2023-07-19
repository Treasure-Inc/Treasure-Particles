package net.treasure.util.unsafe;

@FunctionalInterface
public interface UnsafeConsumer<T> {
    void accept(T t) throws Exception;
}