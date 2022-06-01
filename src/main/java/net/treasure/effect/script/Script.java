package net.treasure.effect.script;

import lombok.Getter;
import lombok.Setter;
import net.treasure.effect.data.EffectData;
import net.treasure.util.TimeKeeper;
import org.bukkit.entity.Player;

@Getter
@Setter
public abstract class Script implements Cloneable {

    protected int interval = -1, index;
    protected String tickHandlerKey = null;

    public abstract boolean tick(Player player, EffectData data, int times);

    public boolean doTick(Player player, EffectData data, int times) {
        if (interval > 0 && !TimeKeeper.isElapsed(interval))
            return true;
        return tick(player, data, times);
    }

    public Script cloneScript() {
        var script = clone();
        script.interval = interval;
        script.tickHandlerKey = tickHandlerKey;
        return script;
    }

    @Override
    public abstract Script clone();
}