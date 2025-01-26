package net.treasure.particles.effect.script.delay;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
@NoArgsConstructor
public class DelayScript extends Script {

    private int delay;
    private TickResult action;

    private int counter;

    public DelayScript(int delay, TickResult action) {
        this.delay = delay;
        this.action = action;
        this.counter = 0;
    }

    @Override
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        counter++;
        if (counter == delay) {
            counter = 0;
            return TickResult.NORMAL;
        }
        return action;
    }

    @Override
    public DelayScript clone() {
        return new DelayScript(delay, action);
    }
}