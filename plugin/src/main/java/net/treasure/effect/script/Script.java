package net.treasure.effect.script;

import lombok.Getter;
import lombok.Setter;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.handler.HandlerEvent;
import net.treasure.effect.handler.TickHandler;
import net.treasure.util.TimeKeeper;
import org.bukkit.entity.Player;

@Getter
@Setter
public abstract class Script implements Cloneable {

    protected int interval = -1;
    protected TickHandler tickHandler;

    public abstract TickResult tick(Player player, EffectData data, HandlerEvent event, int times);

    public TickResult doTick(Player player, EffectData data, HandlerEvent event, int times) {
        if (interval > 0 && !TimeKeeper.isElapsed(interval))
            return TickResult.NORMAL;
        return tick(player, data, event, times);
    }

    public Script cloneScript() {
        var script = clone();
        script.interval = interval;
        script.tickHandler = tickHandler;
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