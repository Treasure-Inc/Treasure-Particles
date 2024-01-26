package net.treasure.particles.util.math;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.treasure.particles.constants.Patterns;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class IntRange {

    protected int min, max;

    public static IntRange of(String value) {
        var args = Patterns.DOUBLE.split(value);
        try {
            if (args.length != 2)
                throw new Exception();
            var min = Integer.parseInt(args[0]);
            var max = Integer.parseInt(args[1]);
            return new IntRange(min, max);
        } catch (Exception e) {
            return null;
        }
    }
}