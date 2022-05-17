package net.treasure.effect.script.conditional;

import net.treasure.effect.player.EffectData;

public interface Predicate {
    boolean test(EffectData data);
}