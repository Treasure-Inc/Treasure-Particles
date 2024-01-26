package net.treasure.particles.util.tuples;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
@AllArgsConstructor
public class Triplet<X, Y, Z> {

    private X x;
    private Y y;
    private Z z;

    public Triplet<X, Y, Z> clone() {
        return new Triplet<>(x, y, z);
    }
}