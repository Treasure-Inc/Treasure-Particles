package net.treasure.particles.effect.script.conditional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class ConditionalScript extends Script {

    ConditionGroup parent;
    Script firstExpression, secondExpression;

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        boolean success = parent.test(player, effect, data);
        if (success)
            return firstExpression.tick(player, data, event, times);
        else
            return secondExpression.tick(player, data, event, times);
    }

    @Override
    public ConditionalScript clone() {
        return new ConditionalScript(parent, firstExpression, secondExpression);
    }
}