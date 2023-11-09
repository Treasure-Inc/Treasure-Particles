package net.treasure.particles.util.unsafe;

@FunctionalInterface
public interface UnsafeConsumer<T> {
    void accept(T t) throws Exception;
}