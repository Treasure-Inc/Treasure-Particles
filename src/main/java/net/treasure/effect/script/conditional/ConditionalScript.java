package net.treasure.effect.script.conditional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class ConditionalScript extends Script {

    ConditionGroup parent;
    Script firstExpression, secondExpression;

    @Override
    public boolean tick(Player player, EffectData data, int times) {
        boolean success = parent.test(player, data);
        if (success)
            return firstExpression.tick(player, data, times);
        else
            return secondExpression.tick(player, data, times);
    }

    @Override
    public ConditionalScript clone() {
        return new ConditionalScript(parent, firstExpression, secondExpression);
    }
}