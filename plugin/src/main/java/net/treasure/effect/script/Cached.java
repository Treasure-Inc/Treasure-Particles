package net.treasure.effect.script;

import net.treasure.effect.Effect;

public interface Cached {
    void setIndex(int index);

    int getIndex();

    void preTick(Effect effect, int times);
}
