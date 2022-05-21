package net.treasure.effect.script;

import lombok.Getter;
import lombok.Setter;
import net.treasure.effect.data.EffectData;
import net.treasure.util.TimeKeeper;
import org.bukkit.entity.Player;

@Getter
@Setter
public abstract class Script implements Cloneable {

    int interval = -1;
    boolean postLine = false;

    public abstract void tick(Player player, EffectData data, int times);

    public void doTick(Player player, EffectData data, int times) {
        if (interval > 0 && !TimeKeeper.isElapsed(interval))
            return;
        tick(player, data, times);
    }

    public Script cloneScript() {
        var script = clone();
        script.interval = interval;
        script.postLine = postLine;
        return script;
    }

    @Override
    public abstract Script clone();
}
