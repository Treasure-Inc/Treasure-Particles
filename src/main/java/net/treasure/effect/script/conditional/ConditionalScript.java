package net.treasure.effect.script.conditional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class ConditionalScript extends Script {

    ConditionGroup parent;
    Script firstExpression, secondExpression;

    @Override
    public TickResult tick(Player player, EffectData data, TickHandler handler, int times) {
        boolean success = parent.test(player, data);
        if (success)
            return firstExpression.tick(player, data, handler, times);
        else
            return secondExpression.tick(player, data, handler, times);
    }

    @Override
    public ConditionalScript clone() {
        return new ConditionalScript(parent, firstExpression, secondExpression);
    }
}