package net.treasure.particles.util.tuples;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pair<K, V> {

    private K key;
    private V value;

    public Pair<K, V> clone() {
        return new Pair<>(key, value);
    }
}