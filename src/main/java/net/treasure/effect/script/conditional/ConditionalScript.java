package net.treasure.effect.script.conditional;

import lombok.AllArgsConstructor;
import net.treasure.effect.player.EffectData;
import net.treasure.effect.script.Script;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public class ConditionalScript extends Script {

    List<ConditionGroup> conditionGroups;
    Script firstExpression, secondExpression;

    @Override
    public void tick(Player player, EffectData data, int times) {
        boolean success = conditionGroups.get(0).test(data);
        if (success)
            firstExpression.tick(player, data, times);
        else
            secondExpression.tick(player, data, times);
    }

    @Override
    public ConditionalScript clone() {
        return new ConditionalScript(conditionGroups, firstExpression, secondExpression);
    }
}