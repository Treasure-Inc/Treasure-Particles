package net.treasure.effect.script;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.data.EffectData;
import net.treasure.util.MathUtil;
import net.treasure.util.Pair;
import org.bukkit.entity.Player;

import java.util.Set;

@Builder
@AllArgsConstructor
public class Variable extends Script {

    @Getter
    private int index;
    private String variable;
    private Operator operator;
    private String eval;

    @Override
    public boolean tick(Player player, EffectData data, int times) {
        Pair<String, Double> pair = data.getVariable(player, variable);
        if (pair == null) return true;
        Effect effect = data.getCurrentEffect();
        if (effect.isEnableCaching()) {
            var array = isPostLine() ? effect.getCachePost() : effect.getCache();
            pair.setValue(array[times][index]);
            return true;
        }
        double val;
        try {
            val = MathUtil.eval(data.replaceVariables(player, eval));
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        switch (operator) {
            case EQUAL -> pair.setValue(val);
            case PLUS -> pair.setValue(pair.getValue() + val);
            case MINUS -> pair.setValue(pair.getValue() - val);
            case MULTIPLY -> pair.setValue(pair.getValue() * val);
            case DIVISION -> pair.setValue(pair.getValue() / val);
        }
        return true;
    }

    public double preTick(Set<Pair<String, Double>> variables) {
        Pair<String, Double> pair = null;
        for (var var : variables)
            if (var.getKey().equals(variable)) {
                pair = var;
                break;
            }
        if (pair == null) return 0;
        double val;
        try {
            var data = new EffectData(variables);
            val = MathUtil.eval(data.replaceVariables(null, eval));
        } catch (Exception e) {
            TreasurePlugin.logger().warning("Invalid evaluation: " + eval);
            return 0;
        }
        switch (operator) {
            case EQUAL -> pair.setValue(val);
            case PLUS -> pair.setValue(pair.getValue() + val);
            case MINUS -> pair.setValue(pair.getValue() - val);
            case MULTIPLY -> pair.setValue(pair.getValue() * val);
            case DIVISION -> pair.setValue(pair.getValue() / val);
        }
        return pair.getValue();
    }

    @Override
    public Variable clone() {
        return new Variable(index, variable, operator, eval);
    }

    public enum Operator {
        EQUAL,
        PLUS,
        MINUS,
        MULTIPLY,
        DIVISION
    }
}