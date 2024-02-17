package net.treasure.particles.effect.script.conditional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;

@AllArgsConstructor
@Getter
public class ConditionalScript extends Script {

    private ConditionGroup parent;
    private Script firstExpression, secondExpression;

    @Override
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        boolean success = parent.test(effect, data);
        if (success)
            return firstExpression.tick(data, event, times);
        else
            return secondExpression.tick(data, event, times);
    }

    @Override
    public ConditionalScript clone() {
        return new ConditionalScript(parent, firstExpression, secondExpression);
    }
}