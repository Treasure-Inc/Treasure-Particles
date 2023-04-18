package net.treasure.util.tuples;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pair<K, V> {
    K key;
    V value;

    public Pair<K, V> clone() {
        return new Pair<>(key, value);
    }
}