package net.treasure.util.tuples;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
public class Triplet<A, B, C> {
    A a;
    B b;
    C c;
}