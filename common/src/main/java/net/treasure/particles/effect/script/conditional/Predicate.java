package net.treasure.particles.effect.script.conditional;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.data.EffectData;

public interface Predicate {
    boolean test(Effect effect, EffectData data);
}