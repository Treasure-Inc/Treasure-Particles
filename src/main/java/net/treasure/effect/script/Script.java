package net.treasure.effect.script;

import lombok.Getter;
import lombok.Setter;
import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.util.TimeKeeper;
import org.bukkit.entity.Player;

@Getter
@Setter
public abstract class Script implements Cloneable {

    protected int interval = -1, index;
    protected String tickHandlerKey = null;

    public abstract TickResult tick(Player player, EffectData data, TickHandler handler, int times);

    public TickResult doTick(Player player, EffectData data, TickHandler handler, int times) {
        if (interval > 0 && !TimeKeeper.isElapsed(interval))
            return TickResult.NORMAL;
        return tick(player, data, handler, times);
    }

    public Script cloneScript() {
        var script = clone();
        script.interval = interval;
        script.tickHandlerKey = tickHandlerKey;
        return script;
    }

    @Override
    public abstract Script clone();

    public enum TickResult {
        NORMAL,
        BREAK,
        BREAK_HANDLER,
        RETURN
    }
}