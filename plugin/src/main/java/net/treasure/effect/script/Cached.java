package net.treasure.effect.script;

import net.treasure.effect.Effect;
import net.treasure.effect.data.EffectData;

public interface Cached {
    void setIndex(int index);

    int getIndex();

    void preTick(Effect effect, EffectData data, int times);
}
