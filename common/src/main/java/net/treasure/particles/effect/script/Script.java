package net.treasure.particles.effect.script;

import lombok.Getter;
import lombok.Setter;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.handler.TickHandler;
import net.treasure.particles.util.TimeKeeper;

@Getter
@Setter
public abstract class Script implements Cloneable {

    protected int interval = -1;
    protected Effect effect;
    protected TickHandler tickHandler;

    public abstract TickResult tick(EffectData data, HandlerEvent event, int times);

    public TickResult doTick(EffectData data, HandlerEvent event, int times) {
        if (interval > 0 && !TimeKeeper.isElapsed(interval))
            return TickResult.NORMAL;
        return tick(data, event, times);
    }

    public Script cloneScript() {
        var script = clone();
        script.effect = effect;
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