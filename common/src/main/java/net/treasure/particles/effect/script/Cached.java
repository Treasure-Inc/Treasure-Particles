package net.treasure.particles.effect.script;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.data.EffectData;

public interface Cached {
    void setIndex(int index);

    int getIndex();

    void preTick(Effect effect, EffectData data, int times);
}
