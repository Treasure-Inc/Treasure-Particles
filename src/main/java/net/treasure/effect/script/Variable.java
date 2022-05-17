package net.treasure.effect.script;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.Effect;
import net.treasure.effect.player.EffectData;
import net.treasure.util.MathUtil;
import net.treasure.util.Pair;
import net.treasure.util.TimeKeeper;
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
    public void tick(Player player, EffectData data, int times) {
        Pair<String, Double> pair = data.getVariable(variable);
        if (pair == null) return;
        Effect effect = data.getCurrentEffect();
        if (effect.isEnableCaching()) {
            var array = isPostLine() ? effect.getCachePost() : effect.getCache();
            pair.setValue(array[times][index]);
            return;
        }
        double val;
        try {
            val = MathUtil.eval(replaceVariables(data));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        switch (operator) {
            case EQUAL -> pair.setValue(val);
            case PLUS -> pair.setValue(pair.getValue() + val);
            case MINUS -> pair.setValue(pair.getValue() - val);
            case MULTIPLY -> pair.setValue(pair.getValue() * val);
            case DIVISION -> pair.setValue(pair.getValue() / val);
        }
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
            val = MathUtil.eval(replaceVariables(new EffectData(variables)));
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

    private String replaceVariables(EffectData data) {
        StringBuilder builder = new StringBuilder();

        var array = eval.toCharArray();
        int startPos = -1;
        StringBuilder variable = new StringBuilder();
        for (int pos = 0; pos < array.length; pos++) {
            var c = array[pos];
            switch (c) {
                case '{' -> {
                    if (startPos != -1) {
                        return null;
                    }
                    startPos = pos;
                }
                case '}' -> {
                    if (startPos == -1) {
                        return null;
                    }
                    var result = variable.toString();
                    var p = data.getVariable(result);
                    if (p == null) {
                        String preset = switch (result) {
                            case "TICK" -> String.valueOf(TimeKeeper.getTimeElapsed());
                            case "PI" -> String.valueOf(Math.PI);
                            case "RANDOM" -> String.valueOf(Math.random());
                            default -> "";
                        };
                        builder.append(preset);
                    } else
                        builder.append(p.getValue());
                    startPos = -1;
                    variable = new StringBuilder();
                }
                default -> {
                    if (startPos != -1)
                        variable.append(c);
                    else
                        builder.append(c);
                }
            }
        }
        return builder.toString();
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